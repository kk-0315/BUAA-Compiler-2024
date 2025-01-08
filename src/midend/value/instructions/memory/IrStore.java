package midend.value.instructions.memory;

import midend.IrValue;
import midend.type.IrValueType;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

public class IrStore extends IrInstruction {

    //private IrValue offset;
    public IrStore(IrValue storeValue,IrValue storeAddress,IrValue offset) {
        super(IrValueType.VOID, 3, IrInstructionType.Store);
        this.setIrUses(storeValue,0);
        this.setIrUses(storeAddress,1);
        if(offset!=null){
            this.setIrUses(offset,2);
        }
    }
    public IrValue getStoreValue(){
        return getOperandFromIndex(0);
    }
    public IrValue getStoreAddress(){
        return getOperandFromIndex(1);
    }
    public IrValue getOffset(){
        return getOperandFromIndex(2);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("\t");
        sb.append("store ");
        sb.append(getOperandFromIndex(0).getIrValueType().toString().toLowerCase());
        sb.append(' ');
        sb.append(getOperandFromIndex(0).getName());
        sb.append(", ");
        sb.append(getOperandFromIndex(1).getIrValueType().toString().toLowerCase());
        sb.append("* ");
        sb.append(getOperandFromIndex(1).getName());
        if(getOperandFromIndex(2)!=null){
            sb.append("[").append(getOperandFromIndex(2).getName()).append("]");
        }
        sb.append('\n');
        return sb.toString();
    }
}
