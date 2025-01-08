package backend.instructions;

import backend.RegName;

public class Bne extends MipsInstruction{
    private int leftReg;
    private int rightReg;
    private String gotoLabel;
    public Bne(int leftReg,int rightReg,String gotoLabel) {
        super("bne");
        this.leftReg=leftReg;
        this.rightReg=rightReg;
        this.gotoLabel=gotoLabel;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(leftReg));
        sb.append(", ").append(RegName.getInstance().getName(rightReg)).append(", ").append(gotoLabel).append('\n');
        return  sb.toString();
    }
}
