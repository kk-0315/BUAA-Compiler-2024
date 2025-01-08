package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import frontend.symbols.SymbolTable;
import frontend.symbols.VarSymbol;
import midend.type.IrValueType;

import java.util.ArrayList;

public class ConstDecl extends BlockItem {

    private Token BType;
    private ArrayList<ConstDef> constDefs;

    public ConstDecl(String name, ArrayList<ParseUnit> subUnits,
                     Token bType, ArrayList<ConstDef> constDefs) {
        super(name, subUnits);
        this.BType = bType;
        this.constDefs = constDefs;
    }

    public void addSymbol(SymbolTable symbolTable, int area) {

        for (int i = 0; i < constDefs.size(); i++) {
            //check(constDefs.get(i),symbolTable,area);
            Token ident = constDefs.get(i).getIdent();
            boolean isArray = constDefs.get(i).isArray();
            VarSymbol varSymbol = new VarSymbol(ident, true, BType, isArray);
            symbolTable.addSymbol(area, varSymbol);
        }
    }
    public ArrayList<ConstDef> getConstDefs(){
        return constDefs;
    }
    public Word getType(){
        if(BType.isType(Word.INTTK)){
            return Word.INTTK;
        }else {
            return Word.CHARTK;
        }
    }

}
