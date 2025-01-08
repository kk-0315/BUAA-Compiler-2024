package midend.value.instructions.memory;

import midend.IrUser;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

public class IrAlloc extends IrInstruction {
    private IrValue allocFor;

    public IrAlloc(IrValueType irValueType) {
        super(irValueType, 0, IrInstructionType.Alloc);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("\t");
        sb.append(getName());
        sb.append(" ");
        sb.append("alloc ");
        sb.append(getIrValueType().toString().toLowerCase());
        if(getDimensionNum()!=0){
            sb.append('[');
            sb.append(getDimensionNum());
            sb.append(']');
        }
        sb.append('\n');
        return sb.toString();
    }
}
