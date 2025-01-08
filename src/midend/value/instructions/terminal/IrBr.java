package midend.value.instructions.terminal;

import midend.IrValue;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.IrLabel;

public class IrBr extends IrInstruction {
    private IrBasicBlock gotoBB;

    public IrBr(IrInstructionType irInstructionType, IrValue left, IrValue right, IrLabel gotoLabel) {
        super(IrValueType.LABEL, irInstructionType.name(), 3, irInstructionType);
        this.setIrUses(left,0);
        this.setIrUses(right,1);
        this.setIrUses(gotoLabel,2);

    }
    public IrBasicBlock getGotoBB(){
        return gotoBB;
    }
    public void setGotoBB(IrBasicBlock irBasicBlock){
        this.gotoBB=irBasicBlock;
        IrLabel gotoLabel=irBasicBlock.getLabelOrCreate();
        this.setIrUses(gotoLabel,2);
    }
    public void setAndChangeGotoBB(IrBasicBlock irBasicBlock){
        this.gotoBB=irBasicBlock;
        IrLabel irLabel=irBasicBlock.getLabelOrCreate();
        this.setIrUses(irLabel,2);

    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(getOperandFromIndex(0).getName()).append(", ");
        sb.append(getOperandFromIndex(1).getName()).append(", ").append(getOperandFromIndex(2).getName());
        sb.append('\n');
        return sb.toString();
    }
}
