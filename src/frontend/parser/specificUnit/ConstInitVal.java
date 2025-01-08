package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import midend.symbols.LinkSymbolTable;

import java.util.ArrayList;

public class ConstInitVal extends ParseUnit {
    private ArrayList<ConstExp> constExps;
    private Token stringConst;

    public ConstInitVal(String name, ArrayList<ParseUnit> subUnits,
                        ArrayList<ConstExp> constExps, Token stringConst) {
        super(name, subUnits);
        this.constExps = constExps;
        this.stringConst = stringConst;
    }


    public ArrayList<Integer> calculate(LinkSymbolTable linkSymbolTable){
        ArrayList<Integer> results=new ArrayList<>();
        if(stringConst!=null){
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
        }else {
            for(ConstExp constExp:constExps){
                results.add(constExp.calculate(linkSymbolTable));
            }
        }
        return results;
    }
}
