package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrBinaryInstruction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;

import java.util.ArrayList;

public class AddExp extends ParseUnit {
    private ArrayList<MulExp> mulExps;
    private ArrayList<Word> ops;

    public AddExp(String name, ArrayList<ParseUnit> subUnits,
                  ArrayList<MulExp> mulExps, ArrayList<Word> ops) {
        super(name, subUnits);
        this.mulExps = mulExps;
        this.ops = ops;
    }

    public Param getParam() {
        if (mulExps.size() > 1) {
            return new Param(Word.INTTK, false);
        } else {
            return mulExps.get(0).getParam();
        }
    }

    public int calculate(LinkSymbolTable linkSymbolTable) {
        int result = 0;
        for (int i = 0; i < mulExps.size(); i++) {
            if (i == 0) {
                result = mulExps.get(0).calculate(linkSymbolTable);
            } else {
                if (ops.get(i - 1).equals(Word.PLUS)) {
                    result += mulExps.get(i).calculate(linkSymbolTable);
                } else {
                    result -= mulExps.get(i).calculate(linkSymbolTable);
                }
            }
        }
        return result;
    }

    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten) {
        IrValue left = null, rignt = null;
        left = mulExps.get(0).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);

        IrBinaryInstruction tmp = null;
        for (int i = 1; i < mulExps.size(); i++) {

            rignt = mulExps.get(i).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
            int cnt=irFunctionCnt.getCnt();
            String name="%_LocalVariable"+cnt;
            if (ops.get(i - 1).equals(Word.PLUS)) {
                tmp = new IrBinaryInstruction(IrValueType.I32, name,IrInstructionType.ADD, left, rignt);
            } else {
                tmp = new IrBinaryInstruction(IrValueType.I32,name, IrInstructionType.SUB, left, rignt);
            }
            left = tmp;
            instructions.add(tmp);
        }

        return left;
    }

}
