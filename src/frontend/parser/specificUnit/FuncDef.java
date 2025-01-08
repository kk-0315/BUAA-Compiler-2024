package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import frontend.parser.Parser;
import frontend.symbols.FuncSymbol;
import frontend.symbols.Symbol;
import frontend.symbols.SymbolTable;
import midend.type.IrValueType;

import java.util.ArrayList;

public class FuncDef extends ParseUnit {
    private FuncType funcType;
    private Token ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef(String name, ArrayList<ParseUnit> subUnits,
                   FuncType funcType, Token ident, FuncFParams funcFParams, Block block) {
        super(name, subUnits);
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;

    }

    public Token getIdent() {
        return ident;
    }

    public void checkF(SymbolTable symbolTable) {
        Symbol symbol = symbolTable.getSymbol(ident.getContext());
        boolean needReturn = ((FuncSymbol) symbol).needReturn();
        if (!needReturn) {
            block.checkF();
        }
    }

    public void checkG() {
        if (!funcType.getFuncType().isType(Word.VOIDTK) &&
                !block.isHasReturn()) {
            ErrorHandler.getInstance().addError(new Error('g', block.getRBrace().getLineNum()));
        }
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void addSymbol(SymbolTable symbolTable, int area) {
//        if(funcFParams != null){
//            funcFParams.addSymbol(symbolTable,area);
//        }
        FuncSymbol funcSymbol = new FuncSymbol(ident, funcType, funcFParams);
        symbolTable.addSymbol(1, funcSymbol);


    }
    public IrValueType getRetType(){
        if(funcType.getFuncType().isType(Word.VOIDTK)){
            return IrValueType.VOID;
        }else if(funcType.getFuncType().isType(Word.INTTK)){
            return IrValueType.I32;
        }else {
            return IrValueType.I8;
        }
    }
    public FuncFParams getFuncFParams(){
        return funcFParams;
    }
    public Block getBlock(){
        return block;
    }
}
