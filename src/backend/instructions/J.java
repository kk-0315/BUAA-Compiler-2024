package backend.instructions;

import midend.value.instructions.IrLabel;

public class J extends MipsInstruction{
    private String label;
    public J(String label) {
        super("j");
        this.label=label;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(label).append('\n');
        return sb.toString();
    }
}
