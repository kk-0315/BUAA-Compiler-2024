package backend.instructions;

import backend.RegName;

public class Mfhi extends MipsInstruction{
    private int destReg;
    public Mfhi(int destReg) {
        super("mfhi");
        this.destReg=destReg;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(destReg)).append('\n');
        return sb.toString();
    }
}
