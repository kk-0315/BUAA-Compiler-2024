package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;

import java.util.ArrayList;

public class ExpStmt extends Stmt {
    private Exp exp;

    public ExpStmt(String name, ArrayList<ParseUnit> subUnits,
                   Exp exp) {
        super(name, subUnits);
        this.exp = exp;
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        if(exp!=null){
            return exp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
        }else {
            return null;
        }
    }
}
