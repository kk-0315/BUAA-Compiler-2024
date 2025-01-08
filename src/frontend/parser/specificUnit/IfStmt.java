package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;
import midend.symbols.LinkSymbolTable;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.IrLabel;

import java.util.ArrayList;

public class IfStmt extends Stmt {
    private Cond cond;
    private Stmt thenStmt; //条件为真的语句
    private Stmt elseStmt; //条件为假的语句

    public IfStmt(String name, ArrayList<ParseUnit> subUnits,
                  Cond cond,
                  Stmt thenStmt, Stmt elseStmt) {
        super(name, subUnits);
        this.cond = cond;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }

    public void checkF() {
        if (thenStmt != null) {
            thenStmt.checkF();
        }
        if (elseStmt != null) {
            elseStmt.checkF();
        }


    }
    public Stmt getThenStmt(){
        return thenStmt;
    }
    public Stmt getElseStmt(){
        return elseStmt;
    }
    public Cond getCond(){
        return cond;
    }



}
