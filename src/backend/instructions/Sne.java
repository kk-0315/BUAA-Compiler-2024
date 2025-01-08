package backend.instructions;

import backend.RegName;

public class Sne extends MipsInstruction{
    private int destReg;
    private int leftReg;
    private int rightReg;

    public Sne(int destReg,int leftReg,int rightReg) {
        super("sne");
        this.destReg=destReg;
        this.leftReg=leftReg;
        this.rightReg=rightReg;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ');
        sb.append(RegName.getInstance().getName(destReg)).append(", ");
        sb.append(RegName.getInstance().getName(leftReg)).append(", ").append(RegName.getInstance().getName(rightReg)).append('\n');
        return sb.toString();
    }
}
