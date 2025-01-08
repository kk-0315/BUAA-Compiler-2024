package frontend.symbols;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.specificUnit.FuncFParams;
import frontend.parser.specificUnit.FuncRParams;
import frontend.parser.specificUnit.FuncType;
import frontend.parser.specificUnit.Param;
import midend.IrValue;

import java.util.ArrayList;

public class FuncSymbol implements Symbol {
    private Token ident;
    private FuncType funcType;
    private FuncFParams funcFParams;

    public FuncSymbol(Token ident, FuncType funcType, FuncFParams funcFParams) {
        this.ident = ident;
        this.funcType = funcType;
        this.funcFParams = funcFParams;
    }

    public boolean needReturn() {
        if (!funcType.getFuncType().getWord().equals(Word.VOIDTK)) {
            return true;
        } else {
            return false;
        }
    }

    public Word getReturnType() {
        return funcType.getFuncType().getWord();
    }

    public void checkParams(FuncRParams funcRParams, Token funcIdent) {
        if (funcFParams == null && funcRParams != null) {

            ArrayList<Param> rParams = funcRParams.getParams();
            if (rParams.size() == 1 && rParams.get(0).getType().equals(Word.VOIDTK)) {

            } else {
                ErrorHandler.getInstance().addError(new Error('d', funcIdent.getLineNum()));
            }

        } else if (funcFParams != null && funcRParams == null) {
            ErrorHandler.getInstance().addError(new Error('d', funcIdent.getLineNum()));
        } else if (funcFParams != null && funcRParams != null) {
            int rParamNum = funcRParams.getRParamNum();
            int fParamNum = funcFParams.getFParamNum();
            ArrayList<Param> fParams = funcFParams.getParams();
            ArrayList<Param> rParams = funcRParams.getParams();
            if (rParamNum != fParamNum) {
                ErrorHandler.getInstance().addError(new Error('d', funcIdent.getLineNum()));
            } else {
                for (int i = 0; i < rParamNum; i++) {
                    if (!fParams.get(i).equals(rParams.get(i))) {
                        ErrorHandler.getInstance().addError(new Error('e', funcIdent.getLineNum()));
                        break;
                    }
                }
            }
        }


    }

    @Override
    public Token getIdent() {
        return ident;
    }

    @Override
    public String getName() {
        return ident.getContext();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ident.getContext());
        sb.append(" ");
        if (funcType.getFuncType().getWord().equals(Word.INTTK)) {
            sb.append("Int");
        } else if (funcType.getFuncType().getWord().equals(Word.CHARTK)) {
            sb.append("Char");
        } else {
            sb.append("Void");
        }
        //sb.append(funcType.getFuncType().getContext());
        sb.append("Func");
        sb.append("\n");
        return sb.toString();
    }

//    private String name;
//    private int returnType; //1--void,2--int,3--char
//    private ArrayList<VarSymbol> paras;
//    private int lineNum;
//
//    public FuncSymbol(String name,int returnType,ArrayList<VarSymbol> paras,int lineNum){
//        this.name=name;
//        this.returnType=returnType;
//        this.paras=paras;
//        this.lineNum=lineNum;
//    }
//    public void addPara(String varName,boolean isConst,boolean isInt,int dimension,int line){
//        paras.add(new VarSymbol(varName,isConst,isInt,dimension,line));
//    }
//    public ArrayList<VarSymbol> getParas(){
//        return paras;
//    }
//    public String getName(){
//        return name;
//    }
//    public int getLineNum(){
//        return lineNum;
//    }
////    @Override
////    public FuncSymbol clone() {
////        return new FuncSymbol(name,returnType,paras);
////    }
//    @Override
//    public String toString(){
//
//        if(returnType==1){
//            return name+" "+"VoidFunc"+"\n";
//        }else if(returnType==2){
//            return name+" "+"IntFunc"+"\n";
//        }else {
//            return name+" "+"CharFunc"+"\n";
//        }
//    }

}
