package Optimize;

import midend.IrModule;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.constant.IrConstantInt;
import midend.value.function.IrFunction;
import midend.value.instructions.IrBinaryInstruction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.Phi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class GVN {
    private IrModule irModule;
    private HashMap<String, IrValue> valueMap;
    public GVN(IrModule irModule){
        this.irModule=irModule;
    }
    public void clear(){
        valueMap=new HashMap<>();
    }
    public void run(){
        for(IrFunction irFunction:irModule.getIrFunctions()){
            clear();
            dfsGVN(irFunction.getIrBasicBlocks().get(0));
        }
    }
    public void dfsGVN(IrBasicBlock entry){
        constantFold(entry);
        HashSet<String> inserted=new HashSet<>();
        Iterator<IrInstruction> iterator=entry.getInstructions().iterator();
        while (iterator.hasNext()) {
            IrInstruction irInstruction=iterator.next();
            if(irInstruction instanceof IrBinaryInstruction){
                if(valueMap.containsKey(((IrBinaryInstruction) irInstruction).toGVNHash())){
                    irInstruction.modifyUser2NewValue(valueMap.get(((IrBinaryInstruction) irInstruction).toGVNHash()));
                    iterator.remove();
                }else {
                    inserted.add(((IrBinaryInstruction) irInstruction).toGVNHash());
                    valueMap.put(((IrBinaryInstruction) irInstruction).toGVNHash(),irInstruction);
                }
            }
        }
        //dfs
        for(IrBasicBlock child:entry.getDirectDomChildrenBlocks()){
            dfsGVN(child);
        }
        //清除本基本块插入的instr
        for(String s:inserted){
            valueMap.remove(s);
        }

    }
    public boolean isConst(String name) {
        if (name.startsWith("@") || name.startsWith("%")||name.equals("undefined")) {
            return false;
        }
        return true;
    }
    public IrValue calFor2Const(IrValue operand1,IrValue operand2,IrInstructionType op,IrValueType irValueType){
        switch (op){
            case ADD:
                return new IrConstantInt(irValueType,Integer.parseInt(operand1.getName())+Integer.parseInt(operand2.getName()));

            case SUB:
                return new IrConstantInt(irValueType,Integer.parseInt(operand1.getName())-Integer.parseInt(operand2.getName()));

            case MUL:
                return new IrConstantInt(irValueType,Integer.parseInt(operand1.getName())*Integer.parseInt(operand2.getName()));

            case DIV:
                return new IrConstantInt(irValueType,Integer.parseInt(operand1.getName())/Integer.parseInt(operand2.getName()));

            case MOD:
                return new IrConstantInt(irValueType,Integer.parseInt(operand1.getName())%Integer.parseInt(operand2.getName()));

            case Lt:
                return Integer.parseInt(operand1.getName())<Integer.parseInt(operand2.getName())?new IrConstantInt(IrValueType.I32,1):new IrConstantInt(IrValueType.I32,0);

            case Le:
                return Integer.parseInt(operand1.getName())<=Integer.parseInt(operand2.getName())?new IrConstantInt(IrValueType.I32,1):new IrConstantInt(IrValueType.I32,0);

            case Gt:
                return Integer.parseInt(operand1.getName())>Integer.parseInt(operand2.getName())?new IrConstantInt(IrValueType.I32,1):new IrConstantInt(IrValueType.I32,0);

            case Ge:
                return Integer.parseInt(operand1.getName())>=Integer.parseInt(operand2.getName())?new IrConstantInt(IrValueType.I32,1):new IrConstantInt(IrValueType.I32,0);

            case Eq:
                return Integer.parseInt(operand1.getName())==Integer.parseInt(operand2.getName())?new IrConstantInt(IrValueType.I32,1):new IrConstantInt(IrValueType.I32,0);

            case Ne:
                return Integer.parseInt(operand1.getName())!=Integer.parseInt(operand2.getName())?new IrConstantInt(IrValueType.I32,1):new IrConstantInt(IrValueType.I32,0);

            default:
                break;
        }
        return null;
    }
    public IrValue calFor1Const(IrValue operand1,IrValue operand2,IrInstructionType op,IrValueType irValueType){
        switch (op){
            case ADD:
                if (operand1.getName().equals("0")){
                    return operand2;
                }else if(operand2.getName().equals("0")){
                    return operand1;
                }
                break;
            case SUB:
                if(operand2.getName().equals("0")){
                    return operand1;
                }
                break;
            case MUL:
                if (operand1.getName().equals("0")||operand2.getName().equals("0")){
                    return new IrConstantInt(IrValueType.I32,0);
                }else if(operand1.getName().equals("1")){
                    return operand2;
                }else if(operand2.getName().equals("1")){
                    return operand1;
                }
                break;
            case DIV:
                if(operand2.getName().equals("1")){
                    return operand1;
                }else if(operand1.getName().equals("0")){
                    return operand1;
                }
                break;
            case MOD:
                if(operand2.getName().equals("1")){
                    return new IrConstantInt(IrValueType.I32,0);
                }
                break;
            default:
                break;
        }
        return null;
    }

    public void constantFold(IrBasicBlock entry){
        Iterator<IrInstruction> iterator=entry.getInstructions().iterator();
        while (iterator.hasNext()){
            IrInstruction irInstruction=iterator.next();
            if(irInstruction instanceof IrBinaryInstruction){
                IrValue operand1=irInstruction.getOperand1();
                IrValue operand2=irInstruction.getOperand2();
                IrInstructionType op=irInstruction.getIrInstructionType();
                IrValueType irValueType=irInstruction.getIrValueType();

                IrValue newResult=null;
                if(operand2==null){ //Not
                    if(isConst(operand1.getName())){
                        if(!operand1.getName().equals("0")){
                            newResult=new IrConstantInt(IrValueType.I32,0);
                        }else {
                            newResult=new IrConstantInt(IrValueType.I32,1);
                        }
                    }
                }else {
                    if(isConst(operand1.getName()) && isConst(operand2.getName())){
                        newResult=calFor2Const(operand1,operand2,op,irValueType);
                    }else if(isConst(operand1.getName())){
                        newResult=calFor1Const(operand1,operand2,op,irValueType);
                    }else if(isConst(operand2.getName())){
                        newResult=calFor1Const(operand1,operand2,op,irValueType);
                    }
                }

                if(newResult!=null){ //null表示不可优化
                    irInstruction.modifyUser2NewValue(newResult);
                    iterator.remove();
                }
            }
        }
    }



}
