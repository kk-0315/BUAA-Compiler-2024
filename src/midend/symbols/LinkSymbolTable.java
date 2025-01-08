package midend.symbols;

import frontend.symbols.Symbol;

import java.util.HashMap;

public class LinkSymbolTable {
    private HashMap<String , IrSymbol> symbols;
    private LinkSymbolTable parent;
    private int cycleDepth;
    public LinkSymbolTable() {
        this.symbols = new HashMap<>();
        this.parent = null;
        this.cycleDepth = 0;
    }

    public LinkSymbolTable(LinkSymbolTable parent) {
        this.symbols = new HashMap<>();
        this.parent = parent;
        this.cycleDepth = parent.getCycleDepth();
    }
    public int getCycleDepth(){
        return cycleDepth;
    }
    public void addSymbol(IrSymbol symbol){
        symbols.put(symbol.getName(),symbol);
    }
    public LinkSymbolTable getParent(){
        return parent;
    }
    public IrSymbol getIrSymbol(String name){
        if(symbols.containsKey(name)){
            return symbols.get(name);
        }else if(this.parent!=null){
            return this.parent.getIrSymbol(name);
        }else {
            return null;
        }
    }
}
