package midend.value.instructions;

import backend.instructions.Add;
import midend.IrUse;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;

import java.util.ArrayList;
import java.util.HashSet;

public class Phi extends IrInstruction{
    private ArrayList<IrBasicBlock> preBlocks;
    public Phi(IrValueType irValueType, String name,ArrayList<IrBasicBlock> preBlocks) {
        super(irValueType, name,preBlocks.size(), IrInstructionType.Phi);
        this.preBlocks=preBlocks;
    }
    public void addIrUseForPhi(IrValue irValue,IrBasicBlock preBlock){
        this.setIrUses(irValue,preBlocks.indexOf(preBlock));
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(" = ");
        sb.append("phi ").append(getIrValueType().toString()).append(' ');
        for(IrBasicBlock preBlock:preBlocks){
            sb.append('[').append(getOperandFromIndex(preBlocks.indexOf(preBlock)).getName()).append(", ").append(preBlock.getName()).append(']').append(' ');
        }
        sb.append('\n');
        return sb.toString();

    }
}
