package midend.value.function;

import frontend.lexer.Word;
import frontend.parser.specificUnit.Block;
import frontend.parser.specificUnit.FuncDef;
import frontend.parser.specificUnit.FuncFParam;
import frontend.parser.specificUnit.MainFuncDef;
import midend.IrModule;
import midend.IrValue;
import midend.symbols.IrSymbolFunc;
import midend.symbols.IrSymbolVar;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.basicBlock.IrBasicBlockCnt;
import midend.value.basicBlock.IrBasicBlockGenerator;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrStore;
import midend.value.instructions.terminal.IrRet;

import java.util.ArrayList;

public class IrFunctionGenerator {
    private LinkSymbolTable linkSymbolTable;
    private FuncDef funcDef;
    private MainFuncDef mainFuncDef;
    private IrFunctionCnt irFunctionCnt;
    private IrBasicBlockCnt irBasicBlockCnt;
    private IrSymbolFunc irSymbolFunc;
    public IrFunctionGenerator(LinkSymbolTable linkSymbolTable,FuncDef funcDef){
        this.linkSymbolTable=linkSymbolTable;
        this.funcDef=funcDef;
        this.irFunctionCnt=new IrFunctionCnt();
        this.irBasicBlockCnt=new IrBasicBlockCnt();

    }
    public IrFunctionGenerator(LinkSymbolTable linkSymbolTable,MainFuncDef mainFuncDef){
        this.linkSymbolTable=linkSymbolTable;
        this.mainFuncDef=mainFuncDef;
        this.irFunctionCnt=new IrFunctionCnt();
        this.irBasicBlockCnt=new IrBasicBlockCnt();

    }
    public IrFunctionCnt getIrFunctionCnt(){
        return irFunctionCnt;
    }
    //填写符号表，生成函数
    public IrFunction generateMainFuncDefFunction(){
        IrValueType retType=IrValueType.I32;
        String name="@main";
        IrFunction irFunction=new IrFunction(retType,name);
        irSymbolFunc=new IrSymbolFunc(name,irFunction);
        linkSymbolTable.getParent().addSymbol(irSymbolFunc);
        Block block=mainFuncDef.getBlock();
        IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,irFunction,linkSymbolTable,block,irFunctionCnt,null,null,null,null);
        ArrayList<IrBasicBlock> irBasicBlocks =irBasicBlockGenerator.generateIrBasicBlock();
        irFunction.addAllBasicBlocks(irBasicBlocks);
        irFunction.setIrFunctionCnt(irFunctionCnt);
        irFunction.setIrBasicBlockCnt(irBasicBlockCnt);
        return irFunction;

    }
    public IrFunction generateFuncDefFunction(){
        IrValueType retType=funcDef.getRetType();
        String name="@"+funcDef.getIdent().getContext();
        IrFunction irFunction=new IrFunction(retType,name);
        irSymbolFunc=new IrSymbolFunc(name,irFunction);
        linkSymbolTable.getParent().addSymbol(irSymbolFunc);
        int index=0;
        IrBasicBlock paramBlock=new IrBasicBlock(irFunction,irBasicBlockCnt.getName());
        if(funcDef.getFuncFParams()!=null){
            for(FuncFParam funcFParam:funcDef.getFuncFParams().getFuncFParams()){

                int cnt=irFunctionCnt.getCnt();
                String varName="%_LocalVariable"+cnt;
                IrValueType irValueType=funcFParam.getBType().isType(Word.INTTK)?IrValueType.I32:IrValueType.I8;
                IrValueType finalType=null;
                if(funcFParam.isArray()){
                    if(irValueType.equals(IrValueType.I32)) finalType=IrValueType.I32_ARR;
                    else finalType=IrValueType.I8_ARR;
                }else {
                    finalType=irValueType;
                }
                //IrValue irValue=new IrValue(finalType,varName);
                IrParam irParam=new IrParam(finalType,varName,index);
                //irValue.setIsParam(true);
                IrSymbolVar irSymbolVar=new IrSymbolVar(funcFParam.getIdent().getContext(),false,funcFParam.isArray(),irParam);
                linkSymbolTable.addSymbol(irSymbolVar);
                if(irParam.getIrValueType().equals(IrValueType.I32)||irParam.getIrValueType().equals(IrValueType.I8)){
                    cnt=irFunctionCnt.getCnt();
                    String nameParam="%_LocalPtr"+cnt;
                    IrAlloc irAlloc=new IrAlloc(irParam.getIrValueType());
                    irAlloc.setName(nameParam);
                    paramBlock.addInstruction(irAlloc);
                    paramBlock.addInstruction(new IrStore(irParam,irAlloc,null));
                    irSymbolVar.setIrValue(irAlloc);
                }

                irFunction.addIrParam(irParam); //参数转为IrParam，设置在函数里

                index++;
            }
        }
        Block block=funcDef.getBlock();



        IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,irFunction,linkSymbolTable,block,irFunctionCnt,null,null,null,null);

        ArrayList<IrBasicBlock> irBasicBlocks=irBasicBlockGenerator.generateIrBasicBlock();
        irFunction.addIrBasicBlock(paramBlock);
        irFunction.addAllBasicBlocks(irBasicBlocks);
        if(irFunction.getIrValueType().equals(IrValueType.VOID)){
            IrBasicBlock irBasicBlock=new IrBasicBlock(irFunction,irBasicBlockCnt.getName());
            irBasicBlock.addInstruction(new IrRet(IrValueType.VOID, IrInstructionType.Ret));
            irFunction.addIrBasicBlock(irBasicBlock);
        }
        irFunction.setIrFunctionCnt(irFunctionCnt);
        irFunction.setIrBasicBlockCnt(irBasicBlockCnt);
        return irFunction;
    }



}
