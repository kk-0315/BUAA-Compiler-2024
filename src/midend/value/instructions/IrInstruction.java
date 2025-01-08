package midend.value.instructions;

import midend.IrUser;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;

public class IrInstruction extends IrUser {
    private IrInstructionType irInstructionType;
    private IrBasicBlock parentBlock=null;


    public IrInstruction(IrValueType irValueType, int opNum,IrInstructionType irInstructionType) {
        super(irValueType, opNum);
        this.irInstructionType=irInstructionType;
    }
    public IrInstruction(IrValueType irValueType,String name, int opNum,IrInstructionType irInstructionType) {
        super(irValueType,name,opNum);
        this.irInstructionType=irInstructionType;
    }
    public IrInstructionType getIrInstructionType(){
        return irInstructionType;
    }
    public void setParentBlock(IrBasicBlock parentBlock){
        this.parentBlock=parentBlock;
    }
    public IrBasicBlock getParentBlock(){
        return parentBlock;
    }

}
