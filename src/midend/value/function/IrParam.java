package midend.value.function;

import midend.IrValue;
import midend.type.IrValueType;

public class IrParam extends IrValue {
    private int rank;

    public IrParam(IrValueType valueType,String name,int rank) {
        super(valueType,name);
        this.rank=rank;
    }
    public int getRank(){
        return rank;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        if(getIrValueType().equals(IrValueType.I8_ARR)||getIrValueType().equals(IrValueType.I32_ARR)){
            sb.append(getIrValueType().toString().toLowerCase(), 0, getIrValueType().toString().length()-4).append('*').append(' ');
        }else {
            sb.append(getIrValueType().toString().toLowerCase());
        }

        sb.append(' ').append(getName());
        return sb.toString();
    }
}
