package backend.instructions;

import backend.RegName;

public class Jr extends MipsInstruction{
    private int regIndex;
    public Jr(int regIndex) {
        super("jr");
        this.regIndex=regIndex;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(regIndex));
        sb.append('\n');
        return sb.toString();
    }
}
