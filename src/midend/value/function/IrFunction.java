package midend.value.function;

import backend.RegName;
import frontend.parser.specificUnit.FuncFParams;
import midend.IrUser;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.basicBlock.IrBasicBlockCnt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IrFunction extends IrValue {

    private ArrayList<IrParam> irParams;
    private ArrayList<IrBasicBlock> irBasicBlocks;
    private IrFunctionCnt irFunctionCnt;
    private IrBasicBlockCnt irBasicBlockCnt;

    private HashMap<IrValue,Integer> val2Reg;


    public IrFunction(IrValueType irValueType, String name) {
        super(irValueType, name);
        this.irParams=new ArrayList<>();
        this.irBasicBlocks=new ArrayList<>();
        this.val2Reg=new HashMap<>();
    }
    public HashMap<IrValue,Integer> getVal2Reg(){
        return val2Reg;
    }
    public IrBasicBlockCnt getIrBasicBlockCnt(){
        return irBasicBlockCnt;
    }
    public void setIrBasicBlockCnt(IrBasicBlockCnt irBasicBlockCnt){
        this.irBasicBlockCnt=irBasicBlockCnt;
    }
    public void setIrFunctionCnt(IrFunctionCnt irFunctionCnt){
        this.irFunctionCnt=irFunctionCnt;
    }
    public IrFunctionCnt getIrFunctionCnt(){
        return irFunctionCnt;
    }
    public void setVal2Reg(HashMap<IrValue,Integer> val2Reg){
        this.val2Reg=val2Reg;
    }

    public void addIrBasicBlock(IrBasicBlock irBasicBlock){
        irBasicBlocks.add(irBasicBlock);
    }
    public void addAllBasicBlocks(ArrayList<IrBasicBlock> irBasicBlocks){
        this.irBasicBlocks.addAll(irBasicBlocks);
    }
    public void addIrParam(IrParam irParam){
        irParams.add(irParam);
    }
    public int getIrParamSize(){
        return irParams.size();
    }
    public ArrayList<IrBasicBlock> getIrBasicBlocks(){
        return irBasicBlocks;
    }
    public IrParam getIndexParam(int index){
        for(IrParam irParam:irParams){
            if(irParam.getRank()==index){
                return irParam;
            }
        }
        return null;
    }
    public ArrayList<IrParam> getIrParams(){
        return irParams;
    }
    @Override
    public String toString(){
        if(val2Reg!=null){
            for(IrValue irValue:val2Reg.keySet()){
                System.out.println(irValue.getName()+"==>"+ RegName.getInstance().getName(val2Reg.get(irValue)));
            }
        }
        StringBuilder sb=new StringBuilder();
        sb.append("define ");
        sb.append("dso_local ");
        sb.append(getIrValueType().toString().toLowerCase());
        sb.append(" ");
        sb.append(getName());
        sb.append('(');
        if(!irParams.isEmpty()){

            sb.append(irParams.get(0).toString());
            for(int i=1;i<irParams.size();i++){
                sb.append(',').append(' ');
                sb.append(irParams.get(i).toString());
            }
        }
        sb.append(')');
        sb.append(" {\n");
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            sb.append(irBasicBlock.toString());
        }
        sb.append("}\n");
        return sb.toString();
    }
}
