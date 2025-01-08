package midend.value.instructions.terminal;

import midend.IrValue;
import midend.type.IrValueType;
import midend.value.function.IrFunction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

import java.util.ArrayList;
import java.util.Objects;

public class IrCall extends IrInstruction {
    private IrValue function;
    private String functionName;
    private ArrayList<IrValue> rParams; //实参

    public IrCall(IrValueType irValueType, String name,  int opNum,IrInstructionType irInstructionType,IrValue irValue,ArrayList<IrValue> rParams) {
        super(irValueType, name,opNum ,irInstructionType);
        this.setIrUses(irValue,0);
        for(int i=0;i<rParams.size();i++){
            this.setIrUses(rParams.get(i),i+1);
        }
        this.function=irValue;
        this.functionName=irValue.getName();
        this.rParams=rParams;
    }
    public boolean isConst(String name) {
        if (name.startsWith("@") || name.startsWith("%")) {
            return false;
        }
        return true;
    }
    //getint getchar
    public IrCall(IrValueType irValueType,String varName,String functionName){
        super(irValueType, varName,0 ,IrInstructionType.Call);
        this.functionName=functionName;
    }
    //putint putchar
    public IrCall(String functionName,IrValue irValue){
        super(IrValueType.VOID, "",2 ,IrInstructionType.Call);
        this.functionName=functionName;
//        IrValue newIrValue=null;
//        if(isConst(irValue.getName())&&functionName.equals("@putchar")){
//            newIrValue=new IrValue(IrValueType.I8,String.valueOf((int)(char)(Integer.parseInt(irValue.getName()))));
//        }else {
//            newIrValue=irValue;
//        }
        this.setIrUses(irValue,1); //函数还是第一个操作数
    }
    public String getFunctionName(){
        return functionName;
    }
    public ArrayList<IrValue> getrParams(){
        ArrayList<IrValue> results=new ArrayList<>();
        for(int i=0;i<rParams.size();i++){
            results.add(getOperandFromIndex(i+1));
        }
        return results;
    }

    //TODO:IrCall待做
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        if(!Objects.equals(getName(), "")){
            sb.append(getName()).append(" = ");
        }
        sb.append("call ");
        sb.append(getIrValueType()).append(' ');
        sb.append(functionName).append('(');
        if(getOperandFromIndex(1)!=null){
            sb.append(getOperandFromIndex(1).getIrValueType().toString().toLowerCase()).append(' ');
            sb.append(getOperandFromIndex(1).getName());
        }
        int i=2;
        while (getOperandFromIndex(i)!=null){
            sb.append(", ");
            sb.append(getOperandFromIndex(i).getIrValueType().toString().toLowerCase()).append(' ');
            sb.append(getOperandFromIndex(i).getName());
            i++;
        }

        sb.append(')');
        sb.append('\n');
        return sb.toString();
    }
}
