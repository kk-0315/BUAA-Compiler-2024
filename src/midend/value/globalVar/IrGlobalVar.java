package midend.value.globalVar;

import midend.IrUser;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.constant.IrConstant;
import midend.value.constant.IrConstantArray;
import midend.value.constant.IrConstantInt;

public class IrGlobalVar extends IrValue {
    private boolean isConst;
    private IrConstant initVal;
    private int dimensionNum;
    public IrGlobalVar(IrValueType irValueType, String name,  boolean isConst, IrConstant initVal,int dimensionNum) {
        super(irValueType, name);
        this.isConst=isConst;
        this.initVal=initVal;
       this.setDimensionNum(dimensionNum);
       this.dimensionNum=dimensionNum;
    }
    public IrConstant getInitVal(){
        return initVal;
    }
    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();

        ret.append(this.getName())
                .append(" = dso_local global ");
        if(getIrValueType().equals(IrValueType.I32_ARR)||getIrValueType().equals(IrValueType.I8_ARR)){
            ret.append('[').append(dimensionNum).append(' ').append('x').append(' ').append(getIrValueType().toString().toLowerCase(), 0, getIrValueType().toString().length()-4).append(']').append(' ');
            ret.append(initVal.toString()).append('\n');

        }else {
            ret.append(initVal.toString()).append('\n');
        }

        return ret.toString();
    }

}
