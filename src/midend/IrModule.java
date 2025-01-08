package midend;

import midend.value.function.IrFunction;
import midend.value.globalVar.IrGlobalVar;

import java.util.ArrayList;
import java.util.function.Function;

public class IrModule {
    private ArrayList<IrGlobalVar> irGlobalVars;
    private ArrayList<IrFunction> irFunctions;
    public IrModule(){
        irGlobalVars=new ArrayList<>();
        irFunctions=new ArrayList<>();
    }

    public void addIrGlobalVar(IrGlobalVar irGlobalVar){
        irGlobalVars.add(irGlobalVar);
    }
    public void addIrFunction(IrFunction irFunction){
        irFunctions.add(irFunction);
    }
    public ArrayList<IrGlobalVar> getIrGlobalVars(){
        return irGlobalVars;
    }
    public ArrayList<IrFunction> getIrFunctions(){
        return irFunctions;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        for (IrGlobalVar irGlobalVar:irGlobalVars){
            sb.append(irGlobalVar.toString());
        }
        for(IrFunction irFunction:irFunctions){
            //TODO
            sb.append(irFunction.toString());
        }
        return sb.toString();
    }
}
