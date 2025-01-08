package midend;

import midend.type.IrValueType;
import midend.value.function.IrFunction;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrLoad;

import java.util.ArrayList;
import java.util.LinkedList;

//维护User到Value的一条关系
public class IrValue {

    private IrValueType irValueType;
    private String name;
    private int dimensionNum=0;
    private IrValue dimensionIndex;
    private ArrayList<IrUse> irUses;
    public IrValue(IrValueType irValueType,String name){
        this.irValueType=irValueType;
        this.name=name;
        this.irUses=new ArrayList<>();
    }
    public IrValue(IrValueType valueType) {
        this.irValueType = valueType;
        this.name = "";
        this.irUses = new ArrayList<>();
    }
    public void modifyUser2NewValue(IrValue newValue){
        ArrayList<IrUse> irUses1=new ArrayList<>(irUses);
        for(IrUse irUse:irUses1){
            irUse.getIrUser().changeIrUse(this,newValue);
        }

    }
    public void deleteUser(IrUser irUser){
        for(int i=irUses.size()-1;i>=0;i--){
            if(irUses.get(i).getIrUser().equals(irUser)){
                irUses.remove(i);
            }
        }
    }
    public ArrayList<IrUse> getIrUses(){
        return irUses;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setDimensionNum(int dimension){
        this.dimensionNum=dimension;
    }
    public int getDimensionNum(){
        return dimensionNum;
    }
    public void setDimensionIndex(IrValue dimensionIndex){
        this.dimensionIndex=dimensionIndex;
    }
    public IrValue getDimensionIndex(ArrayList<IrInstruction> instructions, IrFunctionCnt irFunctionCnt){
        if(dimensionIndex instanceof IrAlloc){
            IrLoad irLoad=new IrLoad(dimensionIndex.getIrValueType(),"%_LocalVariable"+irFunctionCnt.getCnt(), IrInstructionType.Load,dimensionIndex);
            instructions.add(irLoad);
            return irLoad;
        }
        return dimensionIndex;
    }
    public String getName(){
        return name;
    }
    public IrValueType getIrValueType(){
        return irValueType;
    }
    public void setIrValueType(IrValueType irValueType){
        this.irValueType=irValueType;
    }
    public void removeIrUse(IrUse irUse){
        irUses.remove(irUse);
    }
    public void addIrUse(IrUse irUse){
        irUses.add(irUse);
    }
    public IrValue cloneForCall() {
        // 创建新的 IrValue 对象并复制基本属性
        IrValue ret = new IrValue(this.irValueType, this.name);
        ret.setDimensionNum(this.dimensionNum);


        // 处理 dimensionIndex，如果不为空，则递归克隆
        if (this.dimensionIndex != null) {
            ret.setDimensionIndex(this.dimensionIndex.cloneForCall());
        } else {
            ret.setDimensionIndex(null);
        }

        // 直接引用原始对象的 irUses 列表
        ret.irUses = this.irUses;

        return ret;
    }
    @Override
    public String toString(){
        return name;
    }
}
