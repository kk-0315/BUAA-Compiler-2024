package midend.value.instructions.memory;

import midend.IrValue;
import midend.type.IrValueType;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

public class IrMove extends IrInstruction {

    public IrMove(IrValue fromValue,IrValue toValue) {
        super(IrValueType.VOID, "move",2, IrInstructionType.Move);
        this.setIrUses(fromValue,0);
        this.setIrUses(toValue,1);
    }

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t').append(getName()).append(' ').append(getOperand1().getName()).append("==>").append(getOperand2().getName()).append('\n');
        return sb.toString();
    }
}
