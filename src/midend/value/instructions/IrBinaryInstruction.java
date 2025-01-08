package midend.value.instructions;

import midend.IrValue;
import midend.type.IrValueType;

public class IrBinaryInstruction extends IrInstruction{
    public IrBinaryInstruction(IrValueType irValueType,  String name,IrInstructionType irInstructionType, IrValue left,IrValue right) {
        super(irValueType, name,2, irInstructionType);
        
        this.setIrUses(left,0);
        if(right!=null){
            this.setIrUses(right,1);
        }

    }
    public String toGVNHash(){
        StringBuilder sb=new StringBuilder();
        sb.append(getIrInstructionType().toString().toLowerCase()).append(' ');
        if(getIrInstructionType().equals(IrInstructionType.Not)){
            sb.append(getOperand1().getName());
        }
        else if(getIrInstructionType().equals(IrInstructionType.ADD)||getIrInstructionType().equals(IrInstructionType.MUL)){
            if(getOperand1().getName().hashCode()<getOperand2().getName().hashCode()){
                sb.append(getOperand1().getName()).append(", ").append(getOperand2().getName());
            }else {
                sb.append(getOperand2().getName()).append(", ").append(getOperand1().getName());
            }
        }else {
            sb.append(getOperand1().getName()).append(", ").append(getOperand2().getName());
        }
        return sb.toString();
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("\t");
        sb.append(this.getName());
        sb.append(" = ");
        sb.append(this.getIrInstructionType().toString().toLowerCase());
        sb.append(" ");
        sb.append(getIrValueType());
        sb.append(" ");
        sb.append(this.getOperandFromIndex(0).getName());
        if(this.getOperandFromIndex(1)!=null && !this.getIrInstructionType().equals(IrInstructionType.Not)){
            sb.append(" ");
            sb.append(this.getOperandFromIndex(1).getName());
        }
        sb.append('\n');
        return sb.toString();
    }
}
