package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;

import java.util.ArrayList;

public class InitVal extends ParseUnit {
    private ArrayList<Exp> exps;
    private Token stringConst;

    public InitVal(String name, ArrayList<ParseUnit> subUnits,
                   ArrayList<Exp> exps, Token stringConst) {
        super(name, subUnits);
        this.exps = exps;
        this.stringConst = stringConst;
    }
    public ArrayList<Integer> calculate(LinkSymbolTable linkSymbolTable) {
        ArrayList<Integer> results = new ArrayList<>();
        if (stringConst != null) {
            //String context = stringConst.getContext();
            for(int i=1;i<stringConst.getContext().length()-1;i++){
                char currentChar = stringConst.getContext().charAt(i);
                if (currentChar == '\\') { // 检查是否是转义字符
                    if (i + 1 < stringConst.getContext().length()-1) { // 确保转义字符后有内容
                        char nextChar = stringConst.getContext().charAt(i + 1);
                        if(nextChar=='n'){
                            results.add((int) '\n');
                            i++;
                        }else {
                            results.add((int) currentChar);
                        }


                    }else {
                        results.add((int) currentChar);
                    }
                } else {
                    results.add((int) currentChar);
                }

            }
            results.add((int) '\0');
        } else {
            for (Exp exp : exps) {
                results.add(exp.calculate(linkSymbolTable));
            }
        }
        return results;
    }

    public ArrayList<IrValue> generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt, boolean isWritten) {
        ArrayList<IrValue> results = new ArrayList<>();
        if (stringConst != null) {
            String context = stringConst.getContext();
            for (int i = 1; i < context.length() - 1; i++) { // 跳过前后的引号
                char currentChar = context.charAt(i);
                if (currentChar == '\\') { // 检查是否是转义字符
                    if (i + 1 < context.length()-1) { // 确保转义字符后有内容
                        char nextChar = context.charAt(i + 1);
                        if(nextChar=='n'){
                            results.add(new IrValue(IrValueType.I8, String.valueOf((int) '\n')));
                            i++;
                        }else {
                            results.add(new IrValue(IrValueType.I8, String.valueOf((int) currentChar)));
                        }

                    }else {
                        results.add(new IrValue(IrValueType.I8, String.valueOf((int) currentChar)));
                    }
                } else {
                    results.add(new IrValue(IrValueType.I8, String.valueOf((int) currentChar)));
                }
            }
            results.add(new IrValue(IrValueType.I8,String.valueOf((int)'\0')));
        } else {
            for (Exp exp : exps) {
                results.add(exp.generateMidCode(instructions, linkSymbolTable, irFunctionCnt, false));
            }
        }
        return results;
    }



}
