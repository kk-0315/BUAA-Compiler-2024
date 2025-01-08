package midend.value.constant;

import midend.type.IrValueType;

import java.util.ArrayList;

public class IrConstantArray extends IrConstant{
    private ArrayList<Integer> initVals;

    public IrConstantArray(IrValueType irValueType,ArrayList<Integer> initVals){
        super(irValueType,String.valueOf(initVals));
        this.initVals=initVals;

    }
    public ArrayList<Integer> getInitVals(){
        return initVals;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        if(!initVals.isEmpty()){
            sb.append('[');
            sb.append(getIrValueType().toString().toLowerCase(), 0, getIrValueType().toString().length()-4).append(' ');
            sb.append(initVals.get(0));
            for(int i=1;i<initVals.size();i++){
                sb.append(',').append(' ');
                sb.append(getIrValueType().toString().toLowerCase(), 0, getIrValueType().toString().length()-4).append(' ');
                sb.append(initVals.get(i));
            }
            sb.append(']');
        }
        return sb.toString();
    }
}
