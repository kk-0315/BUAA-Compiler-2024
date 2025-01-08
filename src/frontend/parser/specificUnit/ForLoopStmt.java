package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class ForLoopStmt extends Stmt {
    private ForStmt initStmt;
    private Cond loopCond;
    private ForStmt updateStmt;
    private Stmt loopStmt;

    public ForLoopStmt(String name, ArrayList<ParseUnit> subUnits,
                       ForStmt initStmt, Cond loopCond, ForStmt updateStmt, Stmt loopStmt) {
        super(name, subUnits);
        this.initStmt = initStmt;
        this.loopCond = loopCond;
        this.updateStmt = updateStmt;
        this.loopStmt = loopStmt;
    }

    public void checkF() {
        loopStmt.checkF();
    }
    public ForStmt getInitStmt(){
        return initStmt;
    }
    public Cond getLoopCond(){
        return loopCond;
    }
    public ForStmt getUpdateStmt(){
        return updateStmt;
    }
    public Stmt getLoopStmt(){
        return loopStmt;
    }

}
