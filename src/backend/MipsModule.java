package backend;

import backend.functions.MipsFunction;
import backend.instructions.Asciiz;
import backend.instructions.J;
import backend.instructions.Li;
import backend.instructions.MipsInstruction;
import backend.symbols.MipsSymbol;

import java.util.ArrayList;

public class MipsModule {
    //加载全局变量到内存的指令
    private ArrayList<MipsInstruction> globalVarInstructions;
    private ArrayList<MipsFunction> functions;
    private Li li;
    private J jMain;
    private ArrayList<Asciiz> asciizs;
    public MipsModule(){
        this.globalVarInstructions=new ArrayList<>();
        this.functions=new ArrayList<>();
        this.li=new Li(30,0x10040000);
        this.jMain=new J("main");
        this.asciizs=new ArrayList<>();
    }
    public void addAsciiz(Asciiz asciiz){
        this.asciizs.add(asciiz);
    }
    public void addGlobalVarInstructions(MipsInstruction mipsInstruction){
        this.globalVarInstructions.add(mipsInstruction);
    }
    public void addFunction(MipsFunction mipsFunction){
        this.functions.add(mipsFunction);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();

        sb.append(".text\n");
        sb.append(li.toString());
        for(MipsInstruction mipsInstruction:globalVarInstructions){
            sb.append(mipsInstruction.toString());
        }
        sb.append('\n');
        sb.append(jMain.toString());
        //function
        for(MipsFunction mipsFunction:functions){
            sb.append(mipsFunction.toString());
        }
        sb.append('\n');
        return sb.toString();


    }

}
