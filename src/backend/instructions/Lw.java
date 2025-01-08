package backend.instructions;

import backend.RegName;

public class Lw extends MipsInstruction{
    private int destRegIndex;
    private int base;
    private int offset;
    public Lw(int destRegIndex,int base,int offset) {
        super("lw");
        this.destRegIndex=destRegIndex;
        this.base=base;
        this.offset = offset;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(destRegIndex)).append(", ");
        sb.append(offset).append('(').append(RegName.getInstance().getName(base)).append(')').append('\n');
        return sb.toString();
    }
}
