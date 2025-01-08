package Optimize;

import midend.IrModule;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.instructions.IrBinaryInstruction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.Phi;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrLoad;
import midend.value.instructions.terminal.IrCall;

import java.util.*;

public class RegAllocator {
    private IrModule irModule;
    private HashMap<Integer, IrValue> reg2Val;
    private HashMap<IrValue, Integer> val2Reg;
    private HashMap<IrValue, IrValue> lastUse;
    private HashSet<IrValue> tmpFree;
    private HashSet<Integer> availRegs; //可用的临时寄存器 t0-t9 s0-s7
    private HashSet<IrValue> defs;
    private int lastUseReg=8;

    public RegAllocator(IrModule irModule){
        this.irModule=irModule;
        this.reg2Val=new HashMap<>();
        this.val2Reg=new HashMap<>();
        this.availRegs=new HashSet<>();

        for(int i=8;i<=25;i++){
            availRegs.add(i);
        }
        this.defs=new HashSet<>();
    }
    public void init(){
        this.reg2Val=new HashMap<>();
        this.val2Reg=new HashMap<>();

    }
    public HashMap<IrValue,Integer> getVal2Reg(){
        return val2Reg;
    }
    public void clear(){
        lastUse=new HashMap<>();
        tmpFree=new HashSet<>();
        defs=new HashSet<>();
    }
    public int allocRegForVal(){
        Set<Integer> busyRegs=reg2Val.keySet();
        for(Integer i:availRegs){
            if(!busyRegs.contains(i)){
                return i;
            }
        }
        return orderSort();

    }
    public int orderSort(){
        lastUseReg = (lastUseReg + 1) % availRegs.size();
        return new ArrayList<>(availRegs).get(lastUseReg);
    }
    public int choseLeastUse(){
        HashMap<Integer,Integer> cnt=new HashMap<>();
        for(Integer reg:val2Reg.values()){
            if(cnt.containsKey(reg)){
                int past=cnt.get(reg);
                cnt.replace(reg,past+1);
            }else {
                cnt.put(reg,1);
            }
        }
        int min=99999;
        int chose=8;
        for(Integer reg:cnt.keySet()){
            if(cnt.get(reg)<min){
                min=cnt.get(reg);
                chose=reg;
            }
        }
        return chose;
    }
    public int randomSpill(){
        Random random=new Random();
        List<Integer> regs=new ArrayList<>(availRegs);
        int randomIndex= random.nextInt(regs.size());
        return regs.get(randomIndex);
    }
    public boolean isConst(String name) {
        if (name.startsWith("@") || name.startsWith("%")) {
            return false;
        }
        return true;
    }
    public void allocRegForBlock(IrBasicBlock entry){
        HashMap<IrValue, IrValue> lastUse=new HashMap<>();
        HashSet<IrValue> tmpFree=new HashSet<>();
        HashSet<IrValue> defs=new HashSet<>();
        //步骤 1：记录变量的最后使用位置
        for(IrInstruction irInstruction:entry.getInstructions()){
            for(IrValue operand:irInstruction.getOperands()){
                if(operand!=null&&!isConst(operand.getName())){
                    lastUse.put(operand,irInstruction);
                }
            }
        }
        //步骤 2：对指令进行遍历，如果当前指令为某个之前的`Value`的最后一次使用，而且该基本块的$out$中不含有该`value`，将其所占有的寄存器暂时释放，在离开基本块后进行恢复。
        for(IrInstruction irInstruction:entry.getInstructions()){
            if(!(irInstruction instanceof Phi)){ //Phi的
                for(IrValue operand:irInstruction.getOperands()){
                    if(operand!=null&&val2Reg.containsKey(operand)&&lastUse.get(operand).equals(irInstruction)&&!entry.getOut().contains(operand)){
                        reg2Val.remove(val2Reg.get(operand));
                        System.out.println("free "+"$"+val2Reg.get(operand));
                        tmpFree.add(operand);
                    }
                }
            }
            if(isDefInstr(irInstruction)){ //除去对数组的Alloc指令
                defs.add(irInstruction);
                int reg=allocRegForVal();
                if(reg2Val.containsKey(reg)){ //如果该寄存器已经被使用
                    val2Reg.remove(reg2Val.get(reg)); //消除原有的映射,放入内存
                }
                reg2Val.put(reg,irInstruction);
                System.out.println("alloc "+"$"+reg+" for "+irInstruction.getName());
                val2Reg.put(irInstruction,reg);
            }


        }

        //步骤 3：处理子基本块
        for(IrBasicBlock child:entry.getDirectDomChildrenBlocks()){
            HashMap<Integer,IrValue> tmpReg2Val=new HashMap<>();
            //如果某个寄存器中的变量不进入子块，可用暂时释放，这里记录映射关系

            Iterator<Integer> iterator = reg2Val.keySet().iterator();
            while (iterator.hasNext()) {
                Integer i = iterator.next();
                if (!child.getIn().contains(reg2Val.get(i))) {
                    tmpReg2Val.put(i, reg2Val.get(i));
//                    iterator.remove();  // 使用 Iterator 的 remove 方法删除元素
                }
            }
            for(Integer i:tmpReg2Val.keySet()){
                reg2Val.remove(i);
                System.out.println("free "+"$"+i);
            }
            //递归调用
            allocRegForBlock(child);
            //恢复
            for(Integer i:tmpReg2Val.keySet()){
                reg2Val.put(i,tmpReg2Val.get(i));
                System.out.println("alloc "+"$"+i+" for "+tmpReg2Val.get(i).getName());
            }

        }
        //至此该基本块完成寄存器分配
        //步骤 4：释放基本块定义的变量的寄存器
        for(IrValue irValue:defs){
            reg2Val.remove(val2Reg.get(irValue));
            System.out.println("free "+"$"+val2Reg.get(irValue));
        }

        //步骤 5：恢复前驱基本块传递过来的变量的寄存器映射
        for(IrValue irValue:tmpFree){
            if(!defs.contains(irValue)){ //没有再重新定义
                reg2Val.put(val2Reg.get(irValue),irValue);
            }
        }
    }
    public Boolean isDefInstr(IrInstruction irInstruction){
        if(irInstruction instanceof IrAlloc&&(irInstruction.getIrValueType().equals(IrValueType.I8)||irInstruction.getIrValueType().equals(IrValueType.I32)) ||irInstruction instanceof IrLoad ||irInstruction instanceof IrCall&&!(((IrCall) irInstruction).getFunctionName().equals("@putint")||((IrCall) irInstruction).getFunctionName().equals("@putchar"))&&!irInstruction.getIrValueType().equals(IrValueType.VOID) ||irInstruction instanceof IrBinaryInstruction ||irInstruction instanceof Phi){
            return true;
        }
        return false;
    }
    public void run(){
        for(IrFunction irFunction:irModule.getIrFunctions()){
            IrBasicBlock entry=irFunction.getIrBasicBlocks().get(0);
            init();
            allocRegForBlock(entry);
            irFunction.setVal2Reg(val2Reg); //保存到irFunction

        }
    }
}
