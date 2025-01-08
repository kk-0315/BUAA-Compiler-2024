package midend.value.constant;

import midend.type.IrValueType;

public class IrConstantInt extends IrConstant{
    private int initVal;

    public IrConstantInt(IrValueType irValueType,int initVal){
        super(irValueType,String.valueOf(initVal));
        this.initVal=initVal;

    }
    public int getInitVal(){
        return initVal;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(getIrValueType().toString().toLowerCase()).append(' ');
        sb.append(initVal);
        return sb.toString();
    }
}
