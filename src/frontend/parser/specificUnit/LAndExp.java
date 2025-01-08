package frontend.parser.specificUnit;

import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.instructions.IrLabel;
import midend.value.instructions.terminal.IrGoto;

import java.util.ArrayList;

public class LAndExp extends ParseUnit {
    private ArrayList<EqExp> eqExps;

    public LAndExp(String name, ArrayList<ParseUnit> subUnits,
                   ArrayList<EqExp> eqExps) {
        super(name, subUnits);
        this.eqExps = eqExps;
    }
    public ArrayList<EqExp> getEqExps(){
        return eqExps;
    }

}
