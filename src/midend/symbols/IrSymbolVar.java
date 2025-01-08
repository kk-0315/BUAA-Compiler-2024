package midend.symbols;

import frontend.parser.specificUnit.ConstInitVal;
import frontend.parser.specificUnit.InitVal;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.memory.IrStore;

import java.util.ArrayList;

public class IrSymbolVar extends IrSymbol{
    private boolean isConst;
    private boolean isArray;
    private int dimension;
    private int initVal;
    private ArrayList<Integer> initvals;
    private boolean assigned;
    public IrSymbolVar(String name, boolean isConst, boolean isArray, IrValue irValue) {
        super(name,irValue);
        this.isArray=isArray;
        this.isConst=isConst;
        this.assigned=false;
    }
    public void setDimension(int dimension){
        this.dimension=dimension;
    }
    public boolean isArray(){
        return isArray;
    }
    public boolean isAssigned(){
        return assigned;
    }
    public void setInitVal(ConstInitVal constInitVal,LinkSymbolTable linkSymbolTable){
        if(constInitVal!=null){
            assigned=true;
            if(!isArray){
                this.initVal=constInitVal.calculate(linkSymbolTable).get(0);
            }else {
                this.initvals=constInitVal.calculate(linkSymbolTable);
                if(initvals.size()<dimension){
                    for(int i=initvals.size();i<dimension;i++){
                        initvals.add(0);
                    }
                }
            }
        }
    }

    public void setGlobalVarInitVal(InitVal initVal, LinkSymbolTable linkSymbolTable){
        if(initVal!=null){
            assigned=true;
            if(!isArray){
                this.initVal=initVal.calculate(linkSymbolTable).get(0);
            }else {
                this.initvals=initVal.calculate(linkSymbolTable);
                if(initvals.size()<dimension){
                    for(int i=initvals.size();i<dimension;i++){
                        initvals.add(0);
                    }
                }
            }
        }
    }
//    public void setInitVal(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten,InitVal initVal){
//        if(initVal!=null){
//            assigned=true;
//            ArrayList<IrValue> irValues=initVal.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
//            IrValue storeValue=irValues.get(0);
//            IrValue storeAddress=getIrValue();
//            IrValue offset=storeAddress.getDimensionIndex();
//            IrStore irStore=new IrStore(storeValue,storeAddress,offset);
//            instructions.add(irStore);
//        }
//    }
    public void setAssigned(boolean assigned){
        this.assigned=assigned;
    }
    public int getInitVal(){
        return initVal;
    }
    public ArrayList<Integer> getInitvals(){
        return initvals;
    }
    public int getDimension(){
        return dimension;
    }
}
