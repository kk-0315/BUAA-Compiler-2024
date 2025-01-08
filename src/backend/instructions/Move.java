package backend.instructions;

import backend.RegName;

public class Move extends MipsInstruction{
    private int targetRegIndex;
    private int sourceRegIndex;
    public Move(int targetRegIndex,int sourceRegIndex) {
        super("move");
        this.targetRegIndex=targetRegIndex;
        this.sourceRegIndex=sourceRegIndex;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(RegName.getInstance().getName(targetRegIndex)).append(", ").append(RegName.getInstance().getName(sourceRegIndex)).append('\n');
        return sb.toString();
    }
}
