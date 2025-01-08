package Optimize;

import backend.functions.MipsFunction;
import backend.instructions.Sll;
import midend.IrModule;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.instructions.IrBinaryInstruction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.memory.IrMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class Other {
    private IrModule irModule;
    public Other(IrModule irModule){
        this.irModule=irModule;
    }
    public boolean isConst(String name) {
        if (name.startsWith("@") || name.startsWith("%")) {
            return false;
        }
        return true;
    }
    public void run(){
        for (IrFunction irFunction : irModule.getIrFunctions()) {
            Iterator<IrBasicBlock> bbIterator = irFunction.getIrBasicBlocks().iterator();
            while (bbIterator.hasNext()) {
                IrBasicBlock irBasicBlock = bbIterator.next();
                Iterator<IrInstruction> instrIterator = irBasicBlock.getInstructions().iterator();
                ArrayList<IrInstruction> toBeRemove=new ArrayList<>();
                ArrayList<IrInstruction> toBeAdd=new ArrayList<>();
                ArrayList<IrInstruction> moveRemove=new ArrayList<>();
                while (instrIterator.hasNext()) {
                    IrInstruction irInstruction = instrIterator.next();
                    if (irInstruction instanceof IrBinaryInstruction) {
                        if (irInstruction.getIrInstructionType().equals(IrInstructionType.MUL)) {
                            IrValue op1 = irInstruction.getOperandFromIndex(0);
                            IrValue op2 = irInstruction.getOperandFromIndex(1);
                            if (isConst(op2.getName())) {
                                int n = Integer.parseInt(op2.getName());
                                if (n > 0 && (n & (n - 1)) == 0) {
                                    int exp = Integer.bitCount(n - 1);

                                    IrValue irValue=new IrValue(IrValueType.I32,String.valueOf(exp));
                                    toBeAdd.add(new IrBinaryInstruction(irInstruction.getIrValueType(),irInstruction.getName(),IrInstructionType.Sll,irInstruction.getOperandFromIndex(0),irValue));
                                    toBeRemove.add(irInstruction);
                                }
                            }
                        }
                        //除优化太复杂了，不做了，感觉性价比不高
                    }else if(irInstruction instanceof IrMove){
                        HashMap<IrValue,Integer> val2Reg=irFunction.getVal2Reg();
                        IrValue srcValue=irInstruction.getOperandFromIndex(0);
                        IrValue dstValue=irInstruction.getOperandFromIndex(1);
                        if(val2Reg.get(srcValue)!=null&& Objects.equals(val2Reg.get(srcValue), val2Reg.get(dstValue))){
                            moveRemove.add(irInstruction);
                        }
                    }

                }
                for(int i=0;i<toBeRemove.size();i++){
                    int index=irBasicBlock.getInstructions().indexOf(toBeRemove.get(i));
                    irBasicBlock.getInstructions().remove(toBeRemove.get(i));
                    irBasicBlock.getInstructions().add(index,toBeAdd.get(i));

                }
                for(IrInstruction irInstruction:moveRemove){
                    irBasicBlock.getInstructions().remove(irInstruction);
                }
            }
        }



    }
}
