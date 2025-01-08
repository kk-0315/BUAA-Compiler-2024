package backend.instructions;

import backend.RegName;

public class Addi extends MipsInstruction{
    private int targetReg;
    private int leftReg;
    private int imm;
    public Addi(int targetReg,int leftReg,int imm) {
        super("addiu");
        this.targetReg=targetReg;
        this.leftReg=leftReg;
        this.imm=imm;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(targetReg));
        sb.append(", ").append(RegName.getInstance().getName(leftReg)).append(", ").append(imm);
        sb.append('\n');
        return sb.toString();
    }
}
