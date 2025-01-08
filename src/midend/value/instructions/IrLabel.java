package midend.value.instructions;

import midend.type.IrValueType;

public class IrLabel extends IrInstruction{

    public IrLabel(String name) {
        super(IrValueType.LABEL, name, 0, IrInstructionType.Label);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(getName()).append(':').append('\n');
        return sb.toString();
    }
}
