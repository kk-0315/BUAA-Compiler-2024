package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.instructions.IrLabel;
import midend.value.instructions.terminal.IrGoto;

import java.util.ArrayList;

public class LOrExp extends ParseUnit {
    private ArrayList<LAndExp> lAndExps;

    public LOrExp(String name, ArrayList<ParseUnit> subUnits,
                  ArrayList<LAndExp> lAndExps) {
        super(name, subUnits);
        this.lAndExps = lAndExps;
    }
    public ArrayList<LAndExp> getlAndExps(){
        return lAndExps;
    }
}
