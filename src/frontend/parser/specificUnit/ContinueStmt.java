package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class ContinueStmt extends Stmt {
    private Token continueTK;
    private boolean isFromLoop;

    public ContinueStmt(String name, ArrayList<ParseUnit> subUnits,
                        Token continueTK, boolean isFromLoop) {
        super(name, subUnits);
        this.continueTK = continueTK;
        this.isFromLoop = isFromLoop;
    }

    public Token getContinueTK() {
        return continueTK;
    }

    public void checkM() {
        if (!isFromLoop) {
            ErrorHandler.getInstance().addError(new Error('m', continueTK.getLineNum()));
        }
    }
}
