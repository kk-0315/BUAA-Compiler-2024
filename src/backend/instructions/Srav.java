package backend.instructions;

import backend.RegName;

public class Srav extends MipsInstruction{
    private int targetReg;
    private int sourceReg;
    private int offsetReg;

    public Srav(int targetReg,int sourceReg,int offsetReg) {
        super("srav");
        this.targetReg=targetReg;
        this.sourceReg=sourceReg;
        this.offsetReg=offsetReg;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(targetReg)).append(", ").append(RegName.getInstance().getName(sourceReg));
        sb.append(", ").append(RegName.getInstance().getName(offsetReg)).append('\n');
        return sb.toString();
    }
}
