package backend.basicblocks;

import backend.functions.MipsFunction;
import backend.instructions.MipsInstruction;

import java.util.ArrayList;

public class MipsBasicBlock {
    private MipsFunction parentFunction;
    private ArrayList<MipsInstruction> mipsInstructions;
    //它的符号表就是parentFunction的符号表
    public MipsBasicBlock(MipsFunction parentFunction){
        this.parentFunction=parentFunction;
        this.mipsInstructions=new ArrayList<>();
    }
    public void addMipsInstruction(MipsInstruction mipsInstruction){
        this.mipsInstructions.add(mipsInstruction);
    }
    public void addAllMipsInstructions(ArrayList<MipsInstruction> mipsInstruction){
        this.mipsInstructions.addAll(mipsInstruction);
    }
    public MipsFunction getParentFunction(){
        return parentFunction;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        for(MipsInstruction mipsInstruction:mipsInstructions){
            sb.append(mipsInstruction.toString());
        }
        return sb.toString();
    }
}
