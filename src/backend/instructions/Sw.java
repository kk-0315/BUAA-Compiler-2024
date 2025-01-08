package backend.instructions;

import backend.RegName;

public class Sw extends MipsInstruction{
    private int sourceRegIndex;
    private int base;
    private int offset;

    public Sw(int sourceRegIndex,int base,int offset) {
        super("sw");
        this.sourceRegIndex=sourceRegIndex;
        this.base=base;
        this.offset = offset;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(sourceRegIndex));
        sb.append(',').append(' ').append(offset).append('(').append(RegName.getInstance().getName(base)).append(')').append('\n');
        return sb.toString();
    }
}
