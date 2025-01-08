package Optimize;

import backend.instructions.Add;
import midend.*;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.Phi;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrLoad;
import midend.value.instructions.memory.IrStore;

import java.util.*;

public class Mem2Reg {
    private IrModule irModule;
    private IrInstruction curAllocInstr;
    private ArrayList<IrInstruction> defInstrList;
    private ArrayList<IrInstruction> useInstrList;
    private ArrayList<IrBasicBlock> defBlockList;
    private ArrayList<IrBasicBlock> useBlockList;
    private Stack<IrValue> stack;

    public Mem2Reg(IrModule irModule){
        this.irModule=irModule;
        curAllocInstr=null;
        defInstrList=new ArrayList<>();
        useInstrList=new ArrayList<>();
        defBlockList=new ArrayList<>();
        useBlockList=new ArrayList<>();
        stack=new Stack<>();
    }
    public void run(){
        for(IrFunction irFunction:irModule.getIrFunctions()){
            for(IrBasicBlock irBasicBlock:irFunction.getIrBasicBlocks()){
                ArrayList<IrInstruction> copy=new ArrayList<>(irBasicBlock.getInstructions());
                for(IrInstruction irInstruction:copy){
                    if(irInstruction instanceof IrAlloc && (irInstruction.getIrValueType().equals(IrValueType.I32)||irInstruction.getIrValueType().equals(IrValueType.I8))){ //只对非数组
                        //以下内容默认针对一个变量
                        clearAll(); //全部清空，准备下一个变量
                        setDefUse((IrAlloc)irInstruction);
                        insertPhi((IrAlloc)irInstruction);
                        rename(irFunction.getIrBasicBlocks().get(0)); //从第一个基本块开始
                        clearAll(); //全部清空，准备下一个变量
                    }
                }
            }
        }
    }
    public void clearAll(){
        curAllocInstr=null;
        defInstrList=new ArrayList<>();
        useInstrList=new ArrayList<>();
        defBlockList=new ArrayList<>();
        useBlockList=new ArrayList<>();
        stack=new Stack<>();
    }
    public void setDefUse(IrAlloc irAlloc){
        curAllocInstr=irAlloc;
        for(IrUse irUse:irAlloc.getIrUses()){
            IrUser irUser=irUse.getIrUser();
            if(irUser instanceof IrStore && ! ((IrStore) irUser).getParentBlock().getIsDeleted()){
                if(!defBlockList.contains(((IrStore) irUser).getParentBlock())){
                    defBlockList.add(((IrStore) irUser).getParentBlock());
                }
                if(!defInstrList.contains(irUser)){
                    defInstrList.add((IrInstruction) irUser);
                }
            }else if(irUser instanceof IrLoad && !((IrLoad) irUser).getParentBlock().getIsDeleted()){
                if(!useBlockList.contains(((IrLoad) irUser).getParentBlock())){
                    useBlockList.add(((IrLoad) irUser).getParentBlock());
                }
                if(!useInstrList.contains(irUser)){
                    useInstrList.add((IrInstruction) irUser);
                }
            }
        }
    }
    public void insert(IrBasicBlock irBasicBlock){ //插入phi
        int cnt=irBasicBlock.getParentFunction().getIrFunctionCnt().getCnt();
        String name="%_LocalVariable"+cnt;
        //  %4 = phi i32 [ 1, %2 ], [ %6, %5 ]
        Phi phi=new Phi(IrValueType.I32,name,irBasicBlock.getPreBlocks()); //类型暂定为I32，需要变更
        irBasicBlock.add2FirstInstrExceptLabel(phi);
        //既是def又是use
        defInstrList.add(phi);
        useInstrList.add(phi);
    }
    public void insertPhi(IrAlloc irAlloc){
        HashSet<IrBasicBlock> F=new HashSet<>();
        Stack<IrBasicBlock> W=new Stack<>();
        for(IrBasicBlock B:defBlockList){
            W.push(B);
        }
        while (!W.isEmpty()){
            IrBasicBlock x=W.pop();
            for(IrBasicBlock Y:x.getDFs()){
                if(!F.contains(Y)){
                    insert(Y);
                    F.add(Y);
                    if(!defBlockList.contains(Y)){
                        W.push(Y);
                    }
                }
            }
        }
    }
    //dfs对load store phi重命名
    public void rename(IrBasicBlock entry){
        int singleVarStackCnt=0;
        Iterator<IrInstruction> iterator = entry.getInstructions().iterator();
        while (iterator.hasNext()) {
            IrInstruction irInstruction = iterator.next();

            if (irInstruction.equals(curAllocInstr)) {
                iterator.remove();  // 安全地移除元素
            } else if (irInstruction instanceof IrLoad && useInstrList.contains(irInstruction)) {
                // 弹出栈顶的值作为该变量的当前值, 将所有使用load的指令的值更改为新的值
                IrValue newValue = null;
                if (stack.isEmpty()) {
                    newValue = new UndefinedValue(irInstruction.getIrValueType());
                } else {
                    newValue = stack.peek();
                }
                irInstruction.modifyUser2NewValue(newValue);
                iterator.remove();  // 安全地移除元素
            } else if (irInstruction instanceof IrStore && defInstrList.contains(irInstruction)) {
                // 更新该变量的新定义值
                if(((IrStore) irInstruction).getOffset()!=null) continue;
                stack.push(((IrStore) irInstruction).getStoreValue());
                singleVarStackCnt++;
                iterator.remove();  // 安全地移除元素
            } else if (irInstruction instanceof Phi && defInstrList.contains(irInstruction)) {
                // 更新该变量的新定义值
                stack.push(irInstruction);
                singleVarStackCnt++;
            }
        }

        //遍历entry的后继集合，将最新的define（stack.peek）填充进每个后继块的第一个phi指令中
        for(IrBasicBlock irBasicBlock:entry.getSucBlocks()){
            if(irBasicBlock.getInstructions().isEmpty()) break;
            IrInstruction firstInstr=irBasicBlock.getFirstInstrExceptLabel();
            if(firstInstr==null) break;
            if(firstInstr instanceof Phi && useInstrList.contains(firstInstr)){
                IrValue irValue=null;
                if(stack.isEmpty()){
                    irValue=new UndefinedValue(firstInstr.getIrValueType());
                }else {
                    irValue=stack.peek();
                }
                ((Phi)firstInstr).addIrUseForPhi(irValue,entry);
                firstInstr.setIrValueType(irValue.getIrValueType());
            }
        }
        //从entry出发沿着支配树 dfs
        for(IrBasicBlock childBlock:entry.getDirectDomChildrenBlocks()){
            rename(childBlock);
        }
        //清空，本次压入的全部弹出
        for(int i=0;i<singleVarStackCnt;i++){
            stack.pop();
        }

    }
}
