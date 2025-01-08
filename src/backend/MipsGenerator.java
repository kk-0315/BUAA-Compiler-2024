package backend;

import backend.functions.MipsFunctionGenerator;
import backend.instructions.Li;
import backend.instructions.MipsInstruction;
import backend.instructions.Sw;
import backend.symbols.MipsSymbol;
import backend.symbols.MipsSymbolTable;
import frontend.parser.specificUnit.ConstInitVal;
import midend.IrModule;
import midend.value.constant.IrConstant;
import midend.value.constant.IrConstantArray;
import midend.value.constant.IrConstantInt;
import midend.value.function.IrFunction;
import midend.value.globalVar.IrGlobalVar;

import java.util.ArrayList;
import java.util.HashMap;

public class MipsGenerator {
    private IrModule irModule;
    private MipsSymbolTable mipsSymbolTable; //全局不需要符号表，因为不存在寄存器分配
    public MipsGenerator(IrModule irModule){
        this.irModule=irModule;
    }
    public MipsModule generateMipsModule(){
        MipsModule mipsModule=new MipsModule();

        //全局变量
        ArrayList<IrGlobalVar> irGlobalVars=irModule.getIrGlobalVars();
        HashMap<String, MipsSymbol> globalVars=new HashMap<>();
        int gpOffset=0;
        for(IrGlobalVar irGlobalVar:irGlobalVars){

            //全局变量默认初始化为0，此时不需要指令
            IrConstant constant=irGlobalVar.getInitVal();
            if(constant instanceof IrConstantInt){
                MipsSymbol mipsSymbol=new MipsSymbol(irGlobalVar.getName(),28,gpOffset,false,irGlobalVar.getDimensionNum());

                if(((IrConstantInt)constant).getInitVal()!=0){
                    Li li=new Li(25,((IrConstantInt)constant).getInitVal());
                    mipsModule.addGlobalVarInstructions(li);
                    Sw sw=new Sw(25,28,gpOffset);
                    mipsModule.addGlobalVarInstructions(sw);
                }
                gpOffset+=4;
                globalVars.put(mipsSymbol.getName(),mipsSymbol);
            }else {
                ArrayList<Integer> inits=((IrConstantArray)constant).getInitVals();
                MipsSymbol mipsSymbol=new MipsSymbol(irGlobalVar.getName(),28,gpOffset,false,irGlobalVar.getDimensionNum());
                for(Integer value:inits){
                    if(value!=0){
                        Li li=new Li(25,value);
                        mipsModule.addGlobalVarInstructions(li);
                        Sw sw=new Sw(25,28,gpOffset);
                        mipsModule.addGlobalVarInstructions(sw);
                    }
                    gpOffset+=4;

                }

                globalVars.put(mipsSymbol.getName(),mipsSymbol);
            }
        }


        ArrayList<IrFunction> functions=irModule.getIrFunctions();
        for(IrFunction irFunction:functions){
            RegTable regTable=new RegTable(irFunction.getVal2Reg());
            MipsSymbolTable mipsSymbolTable1=new MipsSymbolTable(regTable,irFunction.getVal2Reg());
            regTable.setMipsSymbolTable(mipsSymbolTable1);
            MipsFunctionGenerator mipsFunctionGenerator=new MipsFunctionGenerator(irFunction,mipsSymbolTable1,globalVars,mipsModule);
            mipsModule.addFunction(mipsFunctionGenerator.generateMipsFunction());
        }


        return mipsModule;
    }
}
