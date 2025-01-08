package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class BlockItem extends ParseUnit {

    public BlockItem(String name, ArrayList<ParseUnit> subUnits) {
        super(name, subUnits);
    }

    public void checkM() {
        if (this instanceof BreakStmt) {
            ErrorHandler.getInstance().addError(new Error('m', ((BreakStmt) this).getBreakTK().getLineNum()));
        } else if (this instanceof ContinueStmt) {
            ErrorHandler.getInstance().addError(new Error('m', ((ContinueStmt) this).getContinueTK().getLineNum()));
        } else if (this instanceof BlockStmt) {
            this.checkM();
        }
    }
}
