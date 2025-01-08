package midend;

import midend.type.IrValueType;

public class UndefinedValue extends IrValue{
    public UndefinedValue(IrValueType irValueType) {
        super(irValueType, "0");
    }
    @Override
    public String toString(){
        return "undefined";
    }

}
