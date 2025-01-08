package midend.value.instructions;

import midend.IrValue;
import midend.type.IrValueType;

import java.util.ArrayList;

public class PCopy extends IrInstruction{
    private ArrayList<IrValue> fromValues;
    private ArrayList<IrValue> destValues;
    public PCopy() {
        super(IrValueType.VOID, "pcopy",1, IrInstructionType.PCopy); //操作数随便设置的
        this.destValues=new ArrayList<>();
        this.fromValues=new ArrayList<>();
    }
    public Boolean isEmpty(){
        return fromValues.isEmpty();
    }
    public void add2First(IrValue fromValue,IrValue destValue){
        this.fromValues.add(0,fromValue);
        this.destValues.add(0,destValue);
    }
    public void modifyFromValue(IrValue oldFromValue,IrValue newFromValue){
        if(fromValues.contains(oldFromValue)){
            this.fromValues.set(fromValues.indexOf(oldFromValue),newFromValue);
        }
    }

    public void addValue(IrValue fromValue,IrValue destValue){
        this.fromValues.add(fromValue);
        this.destValues.add(destValue);
    }
    public ArrayList<IrValue> getFromValues(){
        return fromValues;
    }
    public ArrayList<IrValue> getDestValues(){
        return destValues;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t').append(getName()).append(' ');
        for(int i=0;i<fromValues.size();i++){
            IrValue fromValue=fromValues.get(i);
            IrValue destValue=destValues.get(i);
            sb.append(fromValue.getName()).append("==>").append(destValue.getName()).append(" ");
        }
        sb.append('\n');
        return sb.toString();
    }
}
