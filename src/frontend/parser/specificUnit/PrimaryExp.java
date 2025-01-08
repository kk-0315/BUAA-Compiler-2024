package frontend.parser.specificUnit;

import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;

import java.util.ArrayList;

public class PrimaryExp extends ParseUnit {
    private Exp exp;
    private LVal lVal;
    private Number number;
    private Character character;

    public PrimaryExp(String name, ArrayList<ParseUnit> subUnits,
                      Exp exp, LVal lVal, Number number, Character character) {
        super(name, subUnits);
        this.exp = exp;
        this.lVal = lVal;
        this.number = number;
        this.character = character;
    }

    public Param getParam() {
        if (exp != null) {
            return exp.getParam();
        } else if (lVal != null) {
            return lVal.getParam();
        } else if (number != null) {
            return new Param(Word.INTTK, false);
        } else {
            return new Param(Word.CHARTK, false);
        }
    }
    public int calculate(LinkSymbolTable linkSymbolTable){

        if(exp!=null){
            return exp.calculate(linkSymbolTable);
        }else if(lVal!=null){
            return lVal.calculate(linkSymbolTable);
        }else if(number!=null){
            return number.calculate(linkSymbolTable);
        }else {
            return character.calculate(linkSymbolTable);
        }
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        if(exp!=null){
            return exp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);

        }else if(lVal!=null){
            return lVal.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
        }else if(number!=null){
            return number.generateMidCode(instructions,linkSymbolTable,irFunctionCnt);
        }else {
            return character.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
        }

    }
}
