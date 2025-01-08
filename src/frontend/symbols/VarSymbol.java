package frontend.symbols;

import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.Parser;

public class VarSymbol implements Symbol {
    private Token ident;
    private boolean isConst;
    private Token type;
    private boolean isArray;

    public VarSymbol(Token ident, boolean isConst, Token type, boolean isArray) {
        this.ident = ident;
        this.isConst = isConst;
        this.type = type;
        this.isArray = isArray;
    }

    @Override
    public Token getIdent() {
        return ident;
    }

    public boolean isConst() {
        return isConst;
    }

    public Token getType() {
        return type;
    }

    public boolean isArray() {
        return isArray;
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
        if (isConst) {
            sb.append("Const");
        }
        if (type.getWord().equals(Word.INTTK)) {
            sb.append("Int");
        } else {
            sb.append("Char");
        }
        //sb.append(type.getContext());
        if (isArray) {
            sb.append("Array");
        }
        sb.append("\n");
        return sb.toString();
    }
//    private String name;
//    private boolean isInt;
//    private boolean isConst;
//    private int dimension;
//    private int lineNum;
//
//    public VarSymbol(String name,boolean isConst,boolean isInt,int dimension,int lineNum){
//
//        this.isInt=isInt;
//        this.name=name;
//        this.isConst=isConst;
//        this.dimension=dimension;
//        this.lineNum=lineNum;
//    }
//    public String getTypeName(){
//        return typeName;
//    }
//    public String getName(){
//        return name;
//    }
////    @Override
////    public VarSymbol clone() {
////        return new VarSymbol(name,isConst,isInt,dimension);
////    }
//    @Override
//    public String toString(){
//        if(dimension==0){
//            if(isConst){
//                if(isInt){
//                    return name+" "+"ConstInt"+"\n";
//                }else {
//                    return name+" "+"ConstChar"+"\n";
//                }
//            }else {
//                if(isInt){
//                    return name+" "+"Int"+"\n";
//                }else {
//                    return name+" "+"Char"+"\n";
//                }
//            }
//        }else {
//            if(isConst){
//                if(isInt){
//                    return name+" "+"ConstIntArray"+"\n";
//                }else {
//                    return name+" "+"ConstCharArray"+"\n";
//                }
//            }else {
//                if(isInt){
//                    return name+" "+"IntArray"+"\n";
//                }else {
//                    return name+" "+"CharArray"+"\n";
//                }
//            }
//        }
//    }
}
