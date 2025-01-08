package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;
import midend.symbols.LinkSymbolTable;

import java.util.ArrayList;

public class ConstExp extends ParseUnit {
    private AddExp addExp;

    public ConstExp(String name, ArrayList<ParseUnit> subUnits,
                    AddExp addExp) {
        super(name, subUnits);
        this.addExp = addExp;
    }
    public int calculate(LinkSymbolTable linkSymbolTable){
        return addExp.calculate(linkSymbolTable);
    }
}
