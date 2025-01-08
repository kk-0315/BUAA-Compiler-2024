package backend.instructions;

import backend.RegName;

public class Sll extends MipsInstruction{
    private int targetReg;
    private int sourceReg;
    private int offset;
    public Sll(int targetReg,int sourceReg,int offset) {
        super("sll");
        this.targetReg=targetReg;
        this.sourceReg=sourceReg;
        this.offset=offset;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(targetReg)).append(", ").append(RegName.getInstance().getName(sourceReg));
        sb.append(", ").append(offset).append('\n');
        return sb.toString();
    }
}
