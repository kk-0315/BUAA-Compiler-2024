package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class MainFuncDef extends ParseUnit {
    private Block block;

    public MainFuncDef(String name, ArrayList<ParseUnit> subUnits,
                       Block block) {
        super(name, subUnits);
        this.block = block;
    }

    public void checkG() {
        if (!block.isHasReturn()) {
            ErrorHandler.getInstance().addError(new Error('g', block.getRBrace().getLineNum()));
        }
    }
    public Block getBlock(){
        return block;
    }

}
