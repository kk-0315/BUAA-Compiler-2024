package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import frontend.symbols.Symbol;
import frontend.symbols.SymbolTable;
import frontend.symbols.VarSymbol;
import midend.IrValue;
import midend.symbols.IrSymbol;
import midend.symbols.IrSymbolVar;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunctionCnt;
import midend.value.globalVar.IrGlobalVar;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.memory.IrLoad;

import java.util.ArrayList;

public class LVal extends ParseUnit {
    private SymbolTable symbolTable;
    private Token ident;
    private Exp exp;

    public LVal(String name, ArrayList<ParseUnit> subUnits,
                Token ident, Exp exp, SymbolTable symbolTable) {
        super(name, subUnits);
        this.ident = ident;
        this.exp = exp;
        this.symbolTable = symbolTable;
    }

    public Token getIdent() {
        return ident;
    }

    public void checkC(SymbolTable symbolTable) {
        if (symbolTable.getSymbol(ident.getContext()) == null) {
            ErrorHandler.getInstance().addError(new Error('c', ident.getLineNum()));
        }
    }

    public Param getParam() {
        Symbol symbol = symbolTable.getSymbol(ident.getContext());
        Word returnType = ((VarSymbol) symbol).getType().getWord();
        boolean isExpNull = (exp == null);
        Boolean isArray = ((VarSymbol) symbol).isArray() && isExpNull;

        return new Param(returnType, isArray);
    }

    public int calculate(LinkSymbolTable linkSymbolTable) {
        IrSymbol irSymbol = linkSymbolTable.getIrSymbol(ident.getContext());
        if (irSymbol != null) {
            if (((IrSymbolVar) irSymbol).isArray()) {
                return ((IrSymbolVar) irSymbol).getInitvals().get(exp.calculate(linkSymbolTable));
            } else {
                return ((IrSymbolVar) irSymbol).getInitVal();
            }
        }
        return 0;

    }

    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt, boolean isWritten) {
        IrSymbolVar irSymbolVar = (IrSymbolVar) linkSymbolTable.getIrSymbol(ident.getContext());
        IrValue retVal = irSymbolVar.getIrValue();


        if (exp != null) {
            IrValue offset = exp.generateMidCode(instructions, linkSymbolTable, irFunctionCnt, false);
            //retVal.setDimensionIndex(offset);

            if (isWritten) {

                IrValue clone=retVal.cloneForCall();
                clone.setDimensionIndex(offset);
                return clone;
            } else {


                int cnt = irFunctionCnt.getCnt();
                String name = "%_LocalVariable" + cnt;
                IrLoad irLoad = new IrLoad(retVal.getIrValueType().equals(IrValueType.I32_ARR) ? IrValueType.I32 : IrValueType.I8, name, IrInstructionType.Load, retVal);
                irLoad.setOffset(offset);
                irLoad.setDimensionNum(0);
                irLoad.setDimensionIndex(null);
                instructions.add(irLoad);
                return irLoad;
            }

        } else {
//            if(!(retVal instanceof IrGlobalVar)){
//                return retVal;
//            }
            //如果Lval是被写入的，我只需要返回其指针
            //如果Lval是需要读取的，我需要知道他的值，用一个IrLoad指令来加载到一个局部变量
            if (isWritten) {
                return retVal;
            } else {
                if (!((retVal.getName().startsWith("@")) || retVal.getName().startsWith("%"))) {
                    return retVal; //const 常量，直接返回
                } else {
                    int cnt = irFunctionCnt.getCnt();
                    String name = "%_LocalVariable" + cnt;
                    IrLoad irLoad = new IrLoad(retVal.getIrValueType(), name, IrInstructionType.Load, retVal);
                    irLoad.setOffset(retVal.getDimensionIndex(instructions,irFunctionCnt));
                    irLoad.setDimensionNum(retVal.getDimensionNum());
                    irLoad.setDimensionIndex(retVal.getDimensionIndex(instructions,irFunctionCnt));
                    instructions.add(irLoad);
//                    irLoad.setDimensionNum(retVal.getDimensionNum());
//                    irLoad.setDimensionIndex(retVal.getDimensionIndex());
                    return irLoad;
                }
            }
        }


    }
}
