package backend.instructions;

import backend.RegName;

public class Add extends MipsInstruction{
    private int targetReg;
    private int leftReg;
    private int rightReg;
    public Add(int targetReg,int leftReg,int rightReg) {
        super("addu");
        this.targetReg=targetReg;
        this.leftReg=leftReg;
        this.rightReg=rightReg;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();

        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(targetReg));
        sb.append(", ").append(RegName.getInstance().getName(leftReg)).append(", ").append(RegName.getInstance().getName(rightReg)).append('\n');
        return sb.toString();
    }
}
