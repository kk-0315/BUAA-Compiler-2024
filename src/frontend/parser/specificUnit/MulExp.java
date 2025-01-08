package frontend.parser.specificUnit;

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

public class MulExp extends ParseUnit {
    private ArrayList<UnaryExp> unaryExps;
    private ArrayList<Word> ops;

    public MulExp(String name, ArrayList<ParseUnit> subUnits,
                  ArrayList<UnaryExp> unaryExps,ArrayList<Word> ops) {
        super(name, subUnits);
        this.unaryExps = unaryExps;
        this.ops=ops;
    }

    public Param getParam() {
        if (unaryExps.size() > 1) {
            return new Param(Word.INTTK, false);
        } else {
            return unaryExps.get(0).getParam();
        }
    }
    public int calculate(LinkSymbolTable linkSymbolTable){
        int result=0;
        for(int i=0;i<unaryExps.size();i++){
            if(i==0){
                result=unaryExps.get(0).calculate(linkSymbolTable);
            }else {
                if(ops.get(i-1).equals(Word.MULT)){
                    result*=unaryExps.get(i).calculate(linkSymbolTable);
                }else if(ops.get(i-1).equals(Word.DIV)){
                    result/=unaryExps.get(i).calculate(linkSymbolTable);
                }else {
                    result%=unaryExps.get(i).calculate(linkSymbolTable);
                }
            }
        }
        return result;
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        IrValue left = null, rignt = null;
        left = unaryExps.get(0).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);

        IrBinaryInstruction tmp = null;
        for (int i = 1; i < unaryExps.size(); i++) {
            int cnt= irFunctionCnt.getCnt();
            String name="%_LocalVariable"+cnt;
            rignt = unaryExps.get(i).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
            if (ops.get(i - 1).equals(Word.MULT)) {
                tmp = new IrBinaryInstruction(IrValueType.I32,name, IrInstructionType.MUL, left, rignt);
            } else if(ops.get(i - 1).equals(Word.DIV)){
                tmp = new IrBinaryInstruction(IrValueType.I32, name,IrInstructionType.DIV, left, rignt);
            }else {
                tmp = new IrBinaryInstruction(IrValueType.I32,name, IrInstructionType.MOD, left, rignt);
            }
            left = tmp;
            instructions.add(tmp);
        }

        return left;
    }
}
