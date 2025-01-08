package midend;

import midend.type.IrValueType;

import java.util.ArrayList;

public class IrUser extends IrValue{
    private int opNum;
    private ArrayList<IrValue> operands;

    public IrUser(IrValueType irValueType, String name,int opNum) {
        super(irValueType, name);
        this.opNum=opNum;
        this.operands=new ArrayList<>();
        for(int i=0;i<opNum;i++){
            operands.add(null);
        }
    }
    public IrUser(IrValueType irValueType,int opNum){
        super(irValueType);
        this.operands=new ArrayList<>();
        this.opNum=opNum;
        for(int i=0;i<opNum;i++){
            operands.add(null);
        }
    }

    public int getOpNum(){
        return opNum;
    }
    public void setIrUses(IrValue irValue,int index){
        if(operands.get(index)==null){
            operands.set(index,irValue);
            irValue.addIrUse(new IrUse(index,this,irValue));
        }else {
            IrValue oldValue=operands.get(index);
            oldValue.deleteUser(this);
            operands.set(index,irValue);
            irValue.addIrUse(new IrUse(index,this,irValue));
        }
    }
    public void changeIrUse(IrValue oldValue,IrValue newValue){
        int index=operands.indexOf(oldValue);
        oldValue.deleteUser(this);
        operands.set(index,newValue);
        newValue.addIrUse(new IrUse(index,this,newValue));
    }
    public IrValue getOperand1(){
        if(!operands.isEmpty()){
            return operands.get(0);
        }
        return null;
    }
    public IrValue getOperand2(){
        if(operands.size()>1){
            return operands.get(1);
        }
        return null;
    }
    public IrValue getOperandFromIndex(int index){
        if(index<opNum){
            return operands.get(index);
        }else {
            return null;
        }
    }
    public ArrayList<IrValue> getOperands(){
        return operands;
    }
}
