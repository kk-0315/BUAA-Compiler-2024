package midend;

import frontend.parser.specificUnit.Comp;
import frontend.parser.specificUnit.ConstDecl;
import frontend.parser.specificUnit.FuncDef;
import frontend.parser.specificUnit.VarDecl;
import frontend.symbols.SymbolTable;
import midend.symbols.LinkSymbolTable;
import midend.value.function.IrFunctionGenerator;
import midend.value.globalVar.IrGlobalVar;
import midend.value.globalVar.IrGlobalVarGenerator;

import java.util.ArrayList;

public class IrBuilder {
    private Comp comp;
    private IrModule irModule;
    private LinkSymbolTable curLinkSymbolTable;
    public IrBuilder(Comp comp){
        this.comp=comp;
        this.irModule=new IrModule();
        curLinkSymbolTable=new LinkSymbolTable();
    }
    public IrModule generateIrModule(){
        for(ConstDecl constDecl:comp.getConstDels()){
            IrGlobalVarGenerator irGlobalVarGenerator=new IrGlobalVarGenerator(curLinkSymbolTable,constDecl);
            ArrayList<IrGlobalVar> irGlobalVars=irGlobalVarGenerator.generateIrGlobalVar();
            for(IrGlobalVar irGlobalVar:irGlobalVars){
                if(irGlobalVar!=null){
                    this.irModule.addIrGlobalVar(irGlobalVar);
                }
            }
        }
        for(VarDecl varDecl:comp.getVarDecls()){
            IrGlobalVarGenerator irGlobalVarGenerator=new IrGlobalVarGenerator(curLinkSymbolTable,varDecl);
            ArrayList<IrGlobalVar> irGlobalVars=irGlobalVarGenerator.generateIrGlobalVar();
            for(IrGlobalVar irGlobalVar:irGlobalVars){
                if(irGlobalVar!=null){
                    this.irModule.addIrGlobalVar(irGlobalVar);
                }
            }
        }
        //TODO :function
        for(FuncDef funcDef:comp.getFuncDefs()){
            LinkSymbolTable linkSymbolTable=new LinkSymbolTable(curLinkSymbolTable);
            IrFunctionGenerator irFunctionGenerator=new IrFunctionGenerator(linkSymbolTable,funcDef);
            this.irModule.addIrFunction(irFunctionGenerator.generateFuncDefFunction());
        }
        LinkSymbolTable linkSymbolTable=new LinkSymbolTable(curLinkSymbolTable);
        IrFunctionGenerator irFunctionGenerator=new IrFunctionGenerator(linkSymbolTable,comp.getMainFuncDef());
        this.irModule.addIrFunction(irFunctionGenerator.generateMainFuncDefFunction());
        return this.irModule;
    }
}
