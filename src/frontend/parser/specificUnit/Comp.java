package frontend.parser.specificUnit;

import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import frontend.parser.Parser;

import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public class Comp extends ParseUnit {
    private ArrayList<ConstDecl> constDels;
    private ArrayList<VarDecl> varDecls;
    private ArrayList<FuncDef> funcDefs;
    private MainFuncDef mainFuncDef;

    public Comp(String name, ArrayList<ParseUnit> subUnits,
                ArrayList<ConstDecl> constDels, ArrayList<VarDecl> varDecls,
                ArrayList<FuncDef> funcDefs, MainFuncDef mainFuncDef) {
        super(name, subUnits);
        this.constDels = constDels;
        this.varDecls = varDecls;
        this.funcDefs = funcDefs;
        this.mainFuncDef = mainFuncDef;
    }
    public ArrayList<ConstDecl> getConstDels(){
        return constDels;
    }
    public ArrayList<VarDecl> getVarDecls(){return varDecls;}
    public MainFuncDef getMainFuncDef(){
        return mainFuncDef;
    }
    public ArrayList<FuncDef> getFuncDefs(){
        return funcDefs;
    }


}
