package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import frontend.symbols.SymbolTable;

import java.util.ArrayList;

public class ForStmt extends Stmt {
    private LVal lVal;
    private Exp exp;

    public ForStmt(String name, ArrayList<ParseUnit> subUnits,
                   LVal lVal, Exp exp) {
        super(name, subUnits);
        this.lVal = lVal;
        this.exp = exp;
    }

    public void check(SymbolTable symbolTable) {
        Token ident = lVal.getIdent();
        if (symbolTable.isSymbolConst(ident.getContext())) {
            ErrorHandler.getInstance().addError(new Error('h', lVal.getIdent().getLineNum()));
        }
    }
    public LVal getlVal(){
        return lVal;
    }
    public Exp getExp(){
        return exp;
    }
}
