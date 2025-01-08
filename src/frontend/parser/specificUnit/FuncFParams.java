package frontend.parser.specificUnit;

import frontend.parser.ParseUnit;
import frontend.symbols.SymbolTable;

import java.util.ArrayList;

public class FuncFParams extends ParseUnit {
    private ArrayList<FuncFParam> funcFParams;

    public FuncFParams(String name, ArrayList<ParseUnit> subUnits,
                       ArrayList<FuncFParam> funcFParams) {
        super(name, subUnits);
        this.funcFParams = funcFParams;
    }

    public void addSymbol(SymbolTable symbolTable, int area) {
        for (int i = 0; i < funcFParams.size(); i++) {
            funcFParams.get(i).addSymbol(symbolTable, area);
        }
    }
    public ArrayList<FuncFParam> getFuncFParams(){
        return funcFParams;
    }
    public int getFParamNum() {
        return funcFParams.size();
    }

    public ArrayList<Param> getParams() {
        ArrayList<Param> params = new ArrayList<>();
        for (int i = 0; i < funcFParams.size(); i++) {
            params.add(funcFParams.get(i).getParam());
        }
        return params;
    }
}
