package frontend.parser.specificUnit;

import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrBinaryInstruction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

import java.util.ArrayList;

public class EqExp extends ParseUnit {
    private ArrayList<RelExp> relExps;
    private ArrayList<Word> ops;

    public EqExp(String name, ArrayList<ParseUnit> subUnits,
                 ArrayList<RelExp> relExps,ArrayList<Word> ops) {
        super(name, subUnits);
        this.relExps = relExps;
        this.ops=ops;
    }
    public ArrayList<RelExp> getRelExps(){
        return relExps;
    }
    public ArrayList<Word> getOps(){
        return ops;
    }


}
