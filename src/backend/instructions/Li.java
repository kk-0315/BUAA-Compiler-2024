package backend.instructions;

import backend.RegName;

public class Li extends MipsInstruction{
    private int regIndex;
    private int imm;
    public Li(int regIndex,int imm) {
        super("li");
        this.regIndex=regIndex;
        this.imm=imm;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(regIndex));
        sb.append(", ");
        sb.append(imm);
        sb.append('\n');
        return sb.toString();
    }
}
