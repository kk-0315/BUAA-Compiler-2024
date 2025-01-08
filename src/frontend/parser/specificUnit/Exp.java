package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;

import java.util.ArrayList;

public class Exp extends ParseUnit {
    private AddExp addExp;

    public Exp(String name, ArrayList<ParseUnit> subUnits, AddExp addExp) {
        super(name, subUnits);
        this.addExp = addExp;
    }

    public Param getParam() {
        return addExp.getParam();
    }
    public int calculate(LinkSymbolTable linkSymbolTable){
        return addExp.calculate(linkSymbolTable);
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        return addExp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
    }
}
