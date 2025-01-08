package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import frontend.symbols.SymbolTable;
import frontend.symbols.VarSymbol;

import java.util.ArrayList;

public class FuncFParam extends ParseUnit {
    private Token BType;
    private Token ident;
    private boolean isArray;

    public FuncFParam(String name, ArrayList<ParseUnit> subUnits,
                      Token BType, Token ident, boolean isArray) {
        super(name, subUnits);
        this.BType = BType;
        this.ident = ident;
        this.isArray = isArray;
    }
    public Token getIdent(){
        return ident;
    }
    public Token getBType(){
        return BType;
    }
    public boolean isArray(){
        return isArray;
    }
    public void addSymbol(SymbolTable symbolTable, int area) {
        VarSymbol varSymbol = new VarSymbol(ident, false, BType, isArray);
        symbolTable.addSymbol(area, varSymbol);
    }

    public Param getParam() {
        return new Param(BType.getWord(), isArray);
    }
}
