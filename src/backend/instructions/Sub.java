package backend.instructions;

import backend.RegName;

public class Sub extends MipsInstruction{
    private int destReg;
    private int leftReg;
    private int rightReg;
    public Sub(int destReg,int leftReg,int rightReg) {
        super("subu");
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
