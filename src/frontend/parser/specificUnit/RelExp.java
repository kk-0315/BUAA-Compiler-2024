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

public class RelExp extends ParseUnit {
    private ArrayList<AddExp> addExps;
    private ArrayList<Word> ops;

    public RelExp(String name, ArrayList<ParseUnit> subUnits,
                  ArrayList<AddExp> addExps,ArrayList<Word> ops) {
        super(name, subUnits);
        this.addExps = addExps;
        this.ops=ops;
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        IrValue left=null,right=null;
        left=addExps.get(0).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
        IrBinaryInstruction tmp=null;
        for(int i=1;i<addExps.size();i++){
            int cnt=irFunctionCnt.getCnt();
            String name="%_LocalVariable"+cnt;
            right=addExps.get(i).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
            if(ops.get(i-1).equals(Word.LSS)){
                tmp=new IrBinaryInstruction(IrValueType.I32,name, IrInstructionType.Lt,left,right);
            }else if(ops.get(i-1).equals(Word.LEQ)){
                tmp=new IrBinaryInstruction(IrValueType.I32,name, IrInstructionType.Le,left,right);
            }else if(ops.get(i-1).equals(Word.GRE)){
                tmp=new IrBinaryInstruction(IrValueType.I32,name, IrInstructionType.Gt,left,right);
            }else if(ops.get(i-1).equals(Word.GEQ)){
                tmp=new IrBinaryInstruction(IrValueType.I32,name, IrInstructionType.Ge,left,right);
            }
            left=tmp;
            instructions.add(tmp);
        }
        return left;
    }
}
