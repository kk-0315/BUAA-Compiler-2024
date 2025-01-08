package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class FuncType extends ParseUnit {
    private Token funcType;

    public FuncType(String name, ArrayList<ParseUnit> subUnits,
                    Token funcType) {
        super(name, subUnits);
        this.funcType = funcType;
    }

    public Token getFuncType() {
        return funcType;
    }
}
