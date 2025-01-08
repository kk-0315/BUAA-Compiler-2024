package midend.value.instructions.terminal;

import midend.IrValue;
import midend.type.IrValueType;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

public class IrRet extends IrInstruction {

    public IrRet(IrValueType irValueType, IrInstructionType irInstructionType, IrValue irValue) {
        super(irValueType, 1, irInstructionType);
        this.setIrUses(irValue,0);
    }
    public IrRet(IrValueType irValueType, IrInstructionType irInstructionType) {
        super(irValueType, 0, irInstructionType);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("\t");
        sb.append("ret ");
        sb.append(getIrValueType().toString().toLowerCase());
        if(!getIrValueType().equals(IrValueType.VOID)){
            sb.append(' ');
            sb.append(getOperandFromIndex(0).getName());
        }
        sb.append('\n');
        return sb.toString();
    }
}
