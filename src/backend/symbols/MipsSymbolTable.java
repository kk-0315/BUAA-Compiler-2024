package backend.symbols;

import backend.RegTable;
import backend.instructions.Lw;
import backend.instructions.MipsInstruction;
import midend.IrValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MipsSymbolTable {
    private RegTable regTable;
    private HashMap<String,MipsSymbol> mipsSymbols;
    private int fpOffset; // 表明当前已经使用的内存的顶部相对fp的偏移
    private HashMap<IrValue,Integer> val2Reg;
    public MipsSymbolTable(RegTable regTable,HashMap<IrValue,Integer> val2Reg) {
        this.regTable = regTable;
        this.val2Reg=val2Reg;
        this.mipsSymbols = new HashMap<>();
        this.fpOffset=0;
    }

    public void setFpOffset(int fpOffset){
        this.fpOffset=fpOffset;
    }
    public void setMipsSymbols(HashMap<String,MipsSymbol> mipsSymbols){
        this.mipsSymbols=mipsSymbols;
    }

    public boolean hasSymbol(String name){
        return mipsSymbols.containsKey(name);
    }
    public void addSymbol(String name,MipsSymbol mipsSymbol){
        this.mipsSymbols.put(name,mipsSymbol);
        if(mipsSymbol.isTemp()){ //临时寄存器加入寄存器表
            regTable.addSymbol(mipsSymbol);
        }
    }

    public void addFpOffset(int fpOffset){
        this.fpOffset+=fpOffset;
    }
    public int getFpOffset(){
        return fpOffset;
    }
    public MipsSymbol getMipsSymbol(String name){
        if(mipsSymbols.containsKey(name)){
            return mipsSymbols.get(name);
        }
        return null;
    }
    public int getRegIndex(String name, ArrayList<MipsInstruction> mipsInstructions){ //你小子
        if(mipsSymbols.containsKey(name)){
            MipsSymbol mipsSymbol=mipsSymbols.get(name);
            if(mipsSymbol.getRegIndex()==-1&&mipsSymbol.hasRam()){
                MipsSymbol tmp=new MipsSymbol("tmp",true);
                int reg=regTable.distributeRegForSymbol(tmp,mipsInstructions);
                mipsInstructions.add(new Lw(reg,mipsSymbol.getBase(),mipsSymbol.getOffset()));
                return reg;
            }else if(mipsSymbol.getRegIndex()!=-1){
                return mipsSymbol.getRegIndex();
            }
        }
        return -1;
    }
    public void addRegOfVal(IrValue irValue,int reg){
        val2Reg.put(irValue,reg);
    }
    public int getRegofVal(String name){
        for(IrValue irValue:val2Reg.keySet()){
            if(irValue.getName().equals(name)){
                return val2Reg.get(irValue);
            }
        }
        return -1;
    }
    public int getRegOfName(String name){
        for(IrValue irValue:val2Reg.keySet()){
            if(irValue.getName().equals(name)){
                return val2Reg.get(irValue);
            }
        }
        return -1;
    }
    public boolean isAllocated(int index){
        if(val2Reg==null) return false;
        else {
            for(int i:val2Reg.values()){
                if(i==index){
                    return true;
                }
            }
            return false;
        }
    }

    public RegTable getRegTable(){
        return regTable;
    }
}
