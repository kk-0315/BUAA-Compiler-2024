package midend.value.instructions.terminal;

import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.IrLabel;

public class IrGoto extends IrInstruction {
    private IrBasicBlock gotoBB;
    public IrGoto(IrLabel irLabel) {
        super(IrValueType.LABEL, 1, IrInstructionType.Goto);
        this.setIrUses(irLabel,0);
    }
    public void setGotoBB(IrBasicBlock irBasicBlock){
        this.gotoBB=irBasicBlock;
        IrLabel gotoLabel=irBasicBlock.getLabelOrCreate();
        this.setIrUses(gotoLabel,0);
    }
    public IrBasicBlock getGotoBB(){
        return gotoBB;
    }
    public void setAndChangeGotoBB(IrBasicBlock irBasicBlock){
        this.gotoBB=irBasicBlock;
        IrLabel irLabel=irBasicBlock.getLabelOrCreate();
        this.setIrUses(irLabel,0);

    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append("goto ").append(getOperandFromIndex(0).getName()).append('\n');
        return sb.toString();
    }
}
