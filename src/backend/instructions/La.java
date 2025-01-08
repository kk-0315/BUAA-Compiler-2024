package backend.instructions;

import backend.RegName;

public class La extends MipsInstruction{
    private int destReg;
    private String label;
    public La(int destReg,String label) {
        super("la");
        this.destReg=destReg;
        this.label=label;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(destReg)).append(", ").append(label).append('\n');
        return sb.toString();
    }
}
