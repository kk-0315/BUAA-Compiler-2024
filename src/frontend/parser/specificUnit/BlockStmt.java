package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class BlockStmt extends Stmt {
    private Block block;

    public BlockStmt(String name, ArrayList<ParseUnit> subUnits,
                     Block block) {
        super(name, subUnits);
        this.block = block;
    }

    public void checkM() {
        block.checkM();
    }

    public void checkF() {
        block.checkF();
    }
    public Block getBlock(){
        return block;
    }
}
