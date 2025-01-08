package midend.value.instructions.memory;

import midend.IrValue;
import midend.type.IrValueType;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

public class IrLoad extends IrInstruction {


    public IrLoad(IrValueType irValueType, String name, IrInstructionType irInstructionType,IrValue irValue) {
        super(irValueType, name, 2, irInstructionType);
        this.setIrUses(irValue,0);
//        if(irValue.getDimensionIndex()!=null){
//            this.setDimensionNum(0);
//            this.setDimensionIndex(null);
//        }else {
//            this.setDimensionNum(irValue.getDimensionNum());
//        }
//
//        this.offset=irValue.getDimensionIndex();
    }
    public void setOffset(IrValue offset){

        if(offset!=null){
            this.setIrUses(offset,1);
        }
    }



    public IrValue getOffset(){
        return getOperandFromIndex(1);
    }

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("\t");
        sb.append(getName());
        sb.append(" = ");
        sb.append("load ");
        sb.append(getIrValueType());
        sb.append(',').append(' ');
        sb.append(getOperandFromIndex(0).getIrValueType()).append(' ');
        sb.append(getOperandFromIndex(0).getName());
        if(getOperandFromIndex(1)!=null){
            sb.append('[').append(getOperandFromIndex(1).getName()).append(']');
        }
        sb.append('\n');
        return sb.toString();
    }
}
