package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class ReturnStmt extends Stmt {
    private Token returnTK; //保存行号
    private Exp returnVal;

    public ReturnStmt(String name, ArrayList<ParseUnit> subUnits,
                      Token returnTK, Exp returnVal) {
        super(name, subUnits);
        this.returnTK = returnTK;
        this.returnVal = returnVal;
    }

    public Token getReturnTK() {
        return returnTK;
    }

    public boolean isReturnValNull() {
        return returnVal == null;
    }
    public Exp getReturnVal(){
        return returnVal;
    }
}
