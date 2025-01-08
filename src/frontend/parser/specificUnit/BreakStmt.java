package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class BreakStmt extends Stmt {
    private Token breakTK;
    private boolean isFromLoop;

    public BreakStmt(String name, ArrayList<ParseUnit> subUnits,
                     Token breakTK, boolean isFromLoop) {
        super(name, subUnits);
        this.breakTK = breakTK;
        this.isFromLoop = isFromLoop;
    }

    public Token getBreakTK() {
        return breakTK;
    }

    public void checkM() {
        if (!isFromLoop) {
            ErrorHandler.getInstance().addError(new Error('m', breakTK.getLineNum()));
        }
    }
}
