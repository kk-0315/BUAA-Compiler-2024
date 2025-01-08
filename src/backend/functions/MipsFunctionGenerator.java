package backend.functions;

import backend.MipsModule;
import backend.basicblocks.MipsBasicBlock;
import backend.basicblocks.MipsBasicBlockGenerator;
import backend.instructions.Lw;
import backend.instructions.MipsInstruction;
import backend.instructions.Move;
import backend.instructions.Sw;
import backend.symbols.MipsSymbol;
import backend.symbols.MipsSymbolTable;
import frontend.parser.specificUnit.AddExp;
import midend.IrValue;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.function.IrFunctionCnt;
import midend.value.function.IrParam;
import midend.value.instructions.IrInstruction;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsFunctionGenerator {
    private IrFunction irFunction;
    private MipsSymbolTable mipsSymbolTable;//函数体所在符号表
    private ArrayList<MipsInstruction> paramTransferInstructions;
    private MipsModule mipsModule;
    public MipsFunctionGenerator(IrFunction irFunction, MipsSymbolTable mipsSymbolTable, HashMap<String,MipsSymbol> globalVars, MipsModule mipsModule){
        this.irFunction=irFunction;
        this.mipsSymbolTable=mipsSymbolTable;
        this.paramTransferInstructions=new ArrayList<>();
        init(globalVars);
        this.mipsModule=mipsModule;
    }

    //全局变量base $gp 28
    //局部变量base $fp 30
    public void init(HashMap<String,MipsSymbol> globalVars){

        for(MipsSymbol mipsSymbol:globalVars.values()){
            mipsSymbolTable.addSymbol(mipsSymbol.getName(),mipsSymbol);
        }

        ArrayList<IrParam> irParams=irFunction.getIrParams();
        MipsSymbol mipsSymbol=null;
        for(int index=0;index<irParams.size();index++){
            IrParam irParam=irFunction.getIndexParam(index);
            mipsSymbol=new MipsSymbol(irParam.getName(),irParam.getDimensionNum()); //创建局部变量

            if(index>=3){
                if(!mipsSymbol.hasRam()){
                    mipsSymbolTable.getRegTable().allocRamFor(mipsSymbol);
                }
                int reg=26;
                paramTransferInstructions.add(new Lw(reg,29,(index-3)*4));
                paramTransferInstructions.add(new Sw(reg,mipsSymbol.getBase(),mipsSymbol.getOffset()));
            }else {
                mipsSymbolTable.addRegOfVal(irParam,index+5);
            }

            mipsSymbolTable.addSymbol(mipsSymbol.getName(),mipsSymbol);
            mipsSymbol.setParam(true);
        }
    }
    public MipsFunction generateMipsFunction(){
        boolean isMain=irFunction.getName().equals("@main");
        MipsFunction mipsFunction=new MipsFunction(irFunction.getName().substring(1),isMain,mipsModule);
        MipsBasicBlock mipsBasicBlock=new MipsBasicBlock(mipsFunction);
        mipsBasicBlock.addAllMipsInstructions(paramTransferInstructions);
        mipsFunction.addMipsBasicBlock(mipsBasicBlock);
        ArrayList<IrBasicBlock> irBasicBlocks=irFunction.getIrBasicBlocks();
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            MipsBasicBlockGenerator mipsBasicBlockGenerator=new MipsBasicBlockGenerator(mipsSymbolTable,irBasicBlock,mipsFunction);
            mipsFunction.addMipsBasicBlock(mipsBasicBlockGenerator.generateMipsBasicBlock());
        }
        return mipsFunction;
    }

}
