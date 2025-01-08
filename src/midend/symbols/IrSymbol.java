package midend.symbols;

import midend.IrValue;
import midend.type.IrValueType;

public class IrSymbol {
    private String name;
    private IrValue irValue;
    public IrSymbol(String name,IrValue irValue){
        this.name=name;
        this.irValue=irValue;
    }
    public String getName(){
        return name;
    }
    public IrValue getIrValue(){
        return irValue;
    }
    public void setIrValue(IrValue irValue){
        this.irValue=irValue;
    }

}
