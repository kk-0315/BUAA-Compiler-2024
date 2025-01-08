package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;

import java.util.ArrayList;

public class Character extends ParseUnit {
    private Token charConst;

    public Character(String name, ArrayList<ParseUnit> subUnits,
                     Token charConst) {
        super(name, subUnits);
        this.charConst = charConst;
    }
    public int calculate(LinkSymbolTable linkSymbolTable) {
        String context = charConst.getContext();
        char currentChar = context.charAt(1);
        if (currentChar == '\\' && context.length() > 2) { // 检查是否是转义字符
            char nextChar = context.charAt(2);
            switch (nextChar) {
                case 'n':
                    return '\n';
                case 't':
                    return '\t';
                case 'r':
                    return '\r';
                case '\\':
                    return '\\';
                case '\'':
                    return '\'';
                case '\"':
                    return '\"';
                case '0':
                    return '\0';
                default:
                    // 如果是未知转义字符，返回其 ASCII 值
                    return nextChar;
            }
        } else {
            return currentChar; // 普通字符直接返回
        }
    }

    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        int num=this.calculate(linkSymbolTable);
        return new IrValue(IrValueType.I8,String.valueOf(num));
    }
}
