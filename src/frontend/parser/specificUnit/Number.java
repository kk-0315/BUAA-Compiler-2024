package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;

import java.util.ArrayList;

public class Number extends ParseUnit {
    private Token intConst;

    public Number(String name, ArrayList<ParseUnit> subUnits,
                  Token intConst) {
        super(name, subUnits);
        this.intConst = intConst;
    }
    public int calculate(LinkSymbolTable linkSymbolTable){
        return Integer.parseInt(intConst.getContext());
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt){
        //TODO:调试一下
        int num=this.calculate(linkSymbolTable);
        return new IrValue(IrValueType.I32,String.valueOf(num));
    }
}
