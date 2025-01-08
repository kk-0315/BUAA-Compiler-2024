package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import frontend.symbols.SymbolTable;
import frontend.symbols.VarSymbol;
import midend.type.IrValueType;

import java.util.ArrayList;

public class VarDecl extends BlockItem {
    private Token BType;
    private ArrayList<VarDef> varDefs;

    public VarDecl(String name, ArrayList<ParseUnit> subUnits,
                   Token BType, ArrayList<VarDef> varDefs) {
        super(name, subUnits);
        this.BType = BType;
        this.varDefs = varDefs;
    }

    public void addSymbol(SymbolTable symbolTable, int area) {
        for (int i = 0; i < varDefs.size(); i++) {
            Token ident = varDefs.get(i).getIdent();
            boolean isArray = varDefs.get(i).isArray();
            VarSymbol varSymbol = new VarSymbol(ident, false, BType, isArray);
            symbolTable.addSymbol(area, varSymbol);
        }
    }
    public ArrayList<VarDef> getVarDefs(){
        return varDefs;
    }
    public Word getType(){
        if(BType.isType(Word.INTTK)){
            return Word.INTTK;
        }else {
            return Word.CHARTK;
        }
    }
}
