package backend.functions;

import backend.MipsModule;
import backend.basicblocks.MipsBasicBlock;
import backend.symbols.MipsSymbolTable;

import java.util.ArrayList;

public class MipsFunction {
    private String name;
    private ArrayList<MipsBasicBlock> mipsBasicBlocks;
    private boolean isMain;
    private MipsSymbolTable mipsSymbolTable;//函数体所在符号表
    private MipsModule mipsModule;
    public MipsFunction(String name,boolean isMain,MipsModule mipsModule){
        this.name=name;
        this.isMain=isMain;
        this.mipsBasicBlocks=new ArrayList<>();
        this.mipsModule=mipsModule;
    }
    public MipsModule getMipsModule(){
        return mipsModule;
    }
    public void addMipsBasicBlock(MipsBasicBlock mipsBasicBlock){
        this.mipsBasicBlocks.add(mipsBasicBlock);
    }
    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(getName()).append(':').append('\n');
        for(MipsBasicBlock mipsBasicBlock:mipsBasicBlocks){
            sb.append(mipsBasicBlock.toString());
        }
        return sb.toString();
    }

}
