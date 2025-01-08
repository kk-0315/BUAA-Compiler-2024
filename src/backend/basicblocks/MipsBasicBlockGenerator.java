package backend.basicblocks;

import backend.functions.MipsFunction;
import backend.instructions.*;
import backend.symbols.MipsSymbolTable;
import midend.IrValue;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.terminal.IrCall;

import java.util.ArrayList;

public class MipsBasicBlockGenerator {
    private MipsSymbolTable mipsSymbolTable; //block所在符号表
    private IrBasicBlock irBasicBlock; //待处理的irBlock
    private MipsFunction parentFunction;
    public MipsBasicBlockGenerator(MipsSymbolTable mipsSymbolTable,IrBasicBlock irBasicBlock,MipsFunction parentFunction){
        this.mipsSymbolTable=mipsSymbolTable;
        this.irBasicBlock=irBasicBlock;
        this.parentFunction=parentFunction;
    }
    public boolean isConst(String name) {
        if (name.startsWith("@") || name.startsWith("%")) {
            return false;
        }
        return true;
    }
    public MipsBasicBlock generateMipsBasicBlock(){
        MipsBasicBlock mipsBasicBlock=new MipsBasicBlock(parentFunction);
        ArrayList<IrInstruction> irInstructions=irBasicBlock.getInstructions();
        for(int i=0;i<irInstructions.size();i++){
            IrInstruction irInstruction=irInstructions.get(i);
//            if(irInstruction instanceof IrCall && ((((IrCall) irInstruction).getFunctionName().equals("@putchar")&&isConst(irInstruction.getOperandFromIndex(1).getName()))||(((IrCall)irInstruction).getFunctionName().equals("@putint")&&isConst(irInstruction.getOperandFromIndex(1).getName())))){
//                IrInstruction tmp=irInstruction;
//                StringBuilder sb=new StringBuilder();
//                while ((((IrCall)tmp).getFunctionName().equals("@putchar")&&isConst(tmp.getOperandFromIndex(1).getName()))||(((IrCall)tmp).getFunctionName().equals("@putint")&&isConst(tmp.getOperandFromIndex(1).getName()))){
//                    IrValue printValue=tmp.getOperandFromIndex(1);
//                    if(((IrCall) tmp).getFunctionName().equals("@putchar")){
//                        sb.append((char) Integer.parseInt(printValue.getName()));
//                    }else {
//                        sb.append(Integer.parseInt(printValue.getName()));
//                    }
//                    i++;
//                    if(i>=irInstructions.size()){
//                        break;
//                    }
//                    tmp=irInstructions.get(i);
//                    if(!(tmp instanceof IrCall)){
//                        break;
//                    }
//                }
//                i--;
//                int cnt= AsciizCnt.getInstance().getCnt();
//                Asciiz asciiz=new Asciiz("str_"+cnt,sb.toString());
//                this.parentFunction.getMipsModule().addAsciiz(asciiz);
//                mipsBasicBlock.addMipsInstruction(new Move(3,4)); //临时保存a0
//
//
//                mipsBasicBlock.addMipsInstruction(new Li(2,4)); //打印字符串的系统调用号
//                mipsBasicBlock.addMipsInstruction(new La(4,asciiz.getName()));
//                mipsBasicBlock.addMipsInstruction(new Syscall());
//                mipsBasicBlock.addMipsInstruction(new Move(4,3));
//
//            }else {
//                MipsInstructionGenerator mipsInstructionGenerator=new MipsInstructionGenerator(irInstruction,mipsSymbolTable,mipsBasicBlock);
//                mipsBasicBlock.addAllMipsInstructions(mipsInstructionGenerator.generateMipsInstruction());
//            }
            MipsInstructionGenerator mipsInstructionGenerator=new MipsInstructionGenerator(irInstruction,mipsSymbolTable,mipsBasicBlock);
            mipsBasicBlock.addAllMipsInstructions(mipsInstructionGenerator.generateMipsInstruction());
        }
        return mipsBasicBlock;
    }
}
