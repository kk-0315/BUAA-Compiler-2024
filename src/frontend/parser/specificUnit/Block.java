package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;

import java.util.ArrayList;

public class Block extends Stmt {
    private ArrayList<BlockItem> blockItems;
    private boolean hasReturn;
    private Token RBrace;

    public Block(String name, ArrayList<ParseUnit> subUnits,
                 ArrayList<BlockItem> blockItems, Token RBrace) {
        super(name, subUnits);
        this.blockItems = blockItems;
        hasReturn = checkReturn();
        this.RBrace = RBrace;
    }

    public void checkM() {
        for (int i = 0; i < blockItems.size(); i++) {
            blockItems.get(i).checkM();
        }
    }

    public void checkF() {
        for (int i = 0; i < blockItems.size(); i++) {
            if (blockItems.get(i) instanceof ReturnStmt && !((ReturnStmt) blockItems.get(i)).isReturnValNull()) {
                ErrorHandler.getInstance().addError(new Error('f', ((ReturnStmt) blockItems.get(i)).getReturnTK().getLineNum()));
            } else if (blockItems.get(i) instanceof IfStmt) {
                ((IfStmt) blockItems.get(i)).checkF();
            } else if (blockItems.get(i) instanceof ForLoopStmt) {
                ((ForLoopStmt) blockItems.get(i)).checkF();
            } else if (blockItems.get(i) instanceof BlockStmt) {
                ((BlockStmt) blockItems.get(i)).checkF();
            }
        }
    }
//    public ArrayList<Token> getReturn(){
//        ArrayList<Token> returns=new ArrayList<>();
//        for(int i=0;i<blockItems.size();i++){
//            if(blockItems.get(i) instanceof ReturnStmt){
//                returns.add();
//            }
//        }
//        return returns;
//    }

    public boolean checkReturn() {
        if (blockItems.isEmpty()) {
            return false;
        }
        return blockItems.get(blockItems.size() - 1) instanceof ReturnStmt;

    }

    public Token getRBrace() {
        return RBrace;
    }

    public boolean isHasReturn() {
        return hasReturn;
    }
    public ArrayList<BlockItem> getBlockItems(){
        return blockItems;
    }
}
