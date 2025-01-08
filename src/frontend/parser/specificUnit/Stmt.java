package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class Stmt extends BlockItem {
    public Stmt(String name, ArrayList<ParseUnit> subUnits) {
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

    public void checkF() {
        if (this instanceof ReturnStmt && !((ReturnStmt) this).isReturnValNull()) {
            ErrorHandler.getInstance().addError(new Error('f', ((ReturnStmt) this).getReturnTK().getLineNum()));
        } else if (this instanceof IfStmt) {
            this.checkF();
        } else if (this instanceof ForLoopStmt) {
            this.checkF();
        } else if (this instanceof BlockStmt) {
            this.checkF();
        }
    }

}
