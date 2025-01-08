package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import frontend.symbols.SymbolTable;

import java.util.ArrayList;

public class GetCharStmt extends Stmt {
    private LVal lVal;

    public GetCharStmt(String name, ArrayList<ParseUnit> subUnits,
                       LVal lVal) {
        super(name, subUnits);
        this.lVal = lVal;
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
}
