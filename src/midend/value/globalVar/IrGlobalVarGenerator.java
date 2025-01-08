package midend.value.globalVar;

import frontend.lexer.Word;
import frontend.parser.specificUnit.*;
import midend.symbols.IrSymbol;
import midend.symbols.IrSymbolVar;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.constant.IrConstantArray;
import midend.value.constant.IrConstantInt;

import java.util.ArrayList;
import java.util.Collections;

import static midend.type.IrValueType.I32;
import static midend.type.IrValueType.I8;

public class IrGlobalVarGenerator {
    private LinkSymbolTable curLinkSymbolTable;
    private ConstDecl constDecl;
    private VarDecl varDecl;
    public IrGlobalVarGenerator(LinkSymbolTable linkSymbolTable,ConstDecl constDecl){
        this.curLinkSymbolTable=linkSymbolTable;
        this.constDecl=constDecl;
        this.varDecl=null;
    }
    public IrGlobalVarGenerator(LinkSymbolTable linkSymbolTable,VarDecl varDecl){
        this.curLinkSymbolTable=linkSymbolTable;
        this.constDecl=null;
        this.varDecl=varDecl;
    }
    public ArrayList<IrGlobalVar> generateIrGlobalVar(){
        ArrayList<IrGlobalVar> irGlobalVars=new ArrayList<>();

        if(constDecl!=null){
            ArrayList<ConstDef> constDefs=constDecl.getConstDefs();
            Word word=constDecl.getType();
            for(ConstDef constDef:constDefs){
                IrValueType irValueType=null;
                if(word.equals(Word.INTTK)&&constDef.isArray()){
                    irValueType=IrValueType.I32_ARR;
                }else if(word.equals(Word.INTTK)&&!constDef.isArray()){
                    irValueType=I32;
                }else if(word.equals(Word.CHARTK)&&constDef.isArray()){
                    irValueType=IrValueType.I8_ARR;
                }else {
                    irValueType=I8;
                }

                irGlobalVars.add(generateSingleConstGlobalVar(constDef,irValueType));
            }
        }else {
            ArrayList<VarDef> varDefs=varDecl.getVarDefs();
            Word word=varDecl.getType();
            for(VarDef varDef:varDefs){
                IrValueType irValueType=null;
                if(word.equals(Word.INTTK)&&varDef.isArray()){
                    irValueType=IrValueType.I32_ARR;
                }else if(word.equals(Word.INTTK)&&!varDef.isArray()){
                    irValueType=I32;
                }else if(word.equals(Word.CHARTK)&&varDef.isArray()){
                    irValueType=IrValueType.I8_ARR;
                }else {
                    irValueType=I8;
                }
                irGlobalVars.add(generateSingleVarGlobalVar(varDef,irValueType));
            }


        }
        return irGlobalVars;
    }
    public IrGlobalVar generateSingleVarGlobalVar(VarDef varDef,IrValueType irValueType){
        boolean isArray=varDef.isArray();
        IrGlobalVar irGlobalVar=null;

        IrSymbolVar irSymbolVar=new IrSymbolVar(varDef.getIdent().getContext(),false,isArray,irGlobalVar);
        int dimension=0;
        if(varDef.getConstExp()!=null){
            dimension=varDef.getConstExp().calculate(curLinkSymbolTable);
        }
        irSymbolVar.setDimension(dimension);
        if(varDef.getInitVal()!=null){
            irSymbolVar.setGlobalVarInitVal(varDef.getInitVal(),curLinkSymbolTable);
        }

        curLinkSymbolTable.addSymbol(irSymbolVar);

        if(!isArray){
            IrConstantInt irConstantInt=null;
            if(irSymbolVar.isAssigned()){
                irConstantInt=new IrConstantInt(irValueType,irSymbolVar.getInitVal());
            }else {
                irConstantInt=new IrConstantInt(irValueType,0);
            }
            String name="@_GlobalVariable" + IrGlobalVarCnt.getInstance().getCnt();
            irGlobalVar=new IrGlobalVar(irValueType,name,false,irConstantInt,0);
        }else {

            IrConstantArray irConstantArray=null;
            if(irSymbolVar.isAssigned()){
                irConstantArray=new IrConstantArray(irValueType,irSymbolVar.getInitvals());
            }else {
                ArrayList<Integer> tmp=new ArrayList<>();
                for(int i=0;i<dimension;i++){
                    tmp.add(0);
                }
                irConstantArray=new IrConstantArray(irValueType,tmp);
            }
            String name="@_GlobalVariable" + IrGlobalVarCnt.getInstance().getCnt();
            irGlobalVar=new IrGlobalVar(irValueType,name,false,irConstantArray,dimension);
        }
        irSymbolVar.setIrValue(irGlobalVar);
        return irGlobalVar;
    }
    //填写符号表，生成全局变量
    public IrGlobalVar generateSingleConstGlobalVar(ConstDef constDef,IrValueType irValueType){
        boolean isArray=constDef.isArray();
        IrGlobalVar irGlobalVar=null;
        IrSymbolVar irSymbol=new IrSymbolVar(constDef.getIdent().getContext(),true, isArray,irGlobalVar);
        int dimension=0;
        if(constDef.getConstExp()!=null){
            dimension=constDef.getConstExp().calculate(curLinkSymbolTable);
        }
        irSymbol.setDimension(dimension);
        irSymbol.setInitVal(constDef.getConstInitVal(),curLinkSymbolTable);

        curLinkSymbolTable.addSymbol(irSymbol);

        if(!isArray){
            IrConstantInt irConstantInt=null;
            if(irSymbol.isAssigned()){
                irConstantInt=new IrConstantInt(irValueType,irSymbol.getInitVal());
            }
            String name="@_GlobalConst" + IrGlobalVarCnt.getInstance().getCnt();
            irGlobalVar=new IrGlobalVar(irValueType,name,true,irConstantInt,0);

        }else {
            IrConstantArray irConstantArray=null;
            if(irSymbol.isAssigned()){
                irConstantArray=new IrConstantArray(irValueType,irSymbol.getInitvals());
            }
            String name="@_GlobalConst" + IrGlobalVarCnt.getInstance().getCnt();
            irGlobalVar=new IrGlobalVar(irValueType,name,true,irConstantArray,dimension);

        }
        irSymbol.setIrValue(irGlobalVar);
        return irGlobalVar;

    }
}
