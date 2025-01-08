package frontend.symbols;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.lexer.Word;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable implements Cloneable {
    private int maxArea;
    private HashMap<Integer, ArrayList<Symbol>> symbols; //某一作用域的符号表
    private ArrayList<Integer> upperAreaIndex; //该级或上级作用域的序号

    public SymbolTable() {
        this.maxArea = 0;
        this.symbols = new HashMap<>();
        this.upperAreaIndex = new ArrayList<>();
    }

    public void updateMaxArea(int maxArea) {
        this.maxArea = maxArea;
    }

    public void enterNewArea(int area) {
        if (area > maxArea) {
            maxArea = area;
        }
        if (!symbols.containsKey(area)) {
            symbols.put(area, new ArrayList<>());
        }
        upperAreaIndex.add(area);
    }

    public Integer getLastArea() {
        return upperAreaIndex.get(upperAreaIndex.size() - 1);
    }

    public void leaveArea() {
        upperAreaIndex.remove(upperAreaIndex.size() - 1);
    }

    public boolean hasSymbolInArea(int area, Symbol symbol) {
        ArrayList<Symbol> symbols1 = symbols.get(area);
        for (int i = 0; i < symbols1.size(); i++) {
            if (symbols1.get(i).getName().equals(symbol.getName())) {
                return true;
            }
        }
        return false;
    }

    public void addSymbol(int area, Symbol symbol) {
        boolean checkResult = true;
        if (hasSymbolInArea(area, symbol)) {
            checkResult = false;
        }
        if (checkResult) {
            symbols.get(area).add(symbol);
        } else {
            //checkB()
            ErrorHandler.getInstance().addError(new Error('b', symbol.getIdent().getLineNum()));
        }

    }

    public Symbol getSymbolInArea(String name, int area) {
        ArrayList<Symbol> symbols1 = symbols.get(area);
        for (int i = 0; i < symbols1.size(); i++) {
            if (symbols1.get(i).getName().equals(name)) {
                return symbols1.get(i);
            }
        }
        return null;
    }

    public Symbol getSymbol(String name) {

        for (int i = upperAreaIndex.size() - 1; i >= 0; i--) {
            Symbol symbol = getSymbolInArea(name, upperAreaIndex.get(i));
            if (symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    public boolean isSymbolConst(String name) {
        Symbol symbol = getSymbol(name);
        if (symbol != null) {
            return ((VarSymbol) symbol).isConst();
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int j = 1; j <= maxArea; j++) {
            if (symbols.containsKey(j) && !symbols.get(j).isEmpty()) {
                ArrayList<Symbol> symbols1 = symbols.get(j);
                for (int i = 0; i < symbols1.size(); i++) {
                    sb.append(j);
                    sb.append(" ");
                    sb.append(symbols1.get(i).toString());
                }
            }
        }
        return sb.toString();

    }
//    public boolean hasSymbol(String name){
//
//        for(int i=0;i<symbolList.size();i++){
//            if(symbolList.get(i).getName().equals(name)){
//                return true;
//            }
//        }
//        return false;
//    }
//    public Symbol getSymbol(String name){
//        for(int i=0;i<symbolList.size();i++){
//            if(symbolList.get(i).getName().equals(name)){
//                return symbolList.get(i);
//            }
//        }
//        return null;
//    }
//    public void addSymbol(String name,boolean isConst,boolean isInt,int dimension,int lineNum){
//        symbolList.add(new VarSymbol(name,isConst,isInt,dimension,lineNum));
//    }
//    public void addSymbol(String name, int returnType, ArrayList<VarSymbol> paras,int lineNum){
//        symbolList.add(new FuncSymbol(name,returnType,paras,lineNum));
//    }
//    public void addSymbol(FuncSymbol funcSymbol){
//        symbolList.add(funcSymbol);
//    }
////    @Override
////    public SymbolTable clone() {
////        SymbolTable clonedTable = new SymbolTable();
////        for (String key : this.symbolList.keySet()) {
////            Symbol symbol = this.symbolList.get(key);
////            if (symbol instanceof VarSymbol) {
////                clonedTable.symbolList.put(key, ((VarSymbol) symbol).clone());
////            } else if (symbol instanceof FuncSymbol) {
////                clonedTable.symbolList.put(key, ((FuncSymbol) symbol).clone());
////            }
////        }
////        return clonedTable;
////    }
//    @Override
//    public String toString(){
//        StringBuilder stringBuilder=new StringBuilder();
//        for(int i=0;i<symbolList.size();i++){
//            stringBuilder.append(area);
//            stringBuilder.append(" ");
//            stringBuilder.append(symbolList.get(i).toString());
//        }
//        return stringBuilder.toString();
//    }
}
