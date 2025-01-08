package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import midend.symbols.LinkSymbolTable;

import java.util.ArrayList;

public class VarDef extends ParseUnit {
    private Token ident;
    private boolean isArray;
    private ConstExp constExp;
    private InitVal initVal;

    public VarDef(String name, ArrayList<ParseUnit> subUnits,
                  Token ident, boolean isArray, InitVal initVal,ConstExp constExp) {
        super(name, subUnits);
        this.ident = ident;
        this.isArray = isArray;
        this.initVal = initVal;
        this.constExp=constExp;
    }
    public ConstExp getConstExp(){
        return constExp;
    }

    public int getDimension(LinkSymbolTable linkSymbolTable){
        if(constExp!=null){
            return constExp.calculate(linkSymbolTable);
        }else {
            return 0;
        }
    }
    public Token getIdent() {
        return ident;
    }

    public boolean isArray() {
        return isArray;
    }
    public InitVal getInitVal(){
        return initVal;
    }
}
