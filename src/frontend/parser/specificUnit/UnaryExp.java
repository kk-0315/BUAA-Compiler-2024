package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.ParseUnit;
import frontend.symbols.FuncSymbol;
import frontend.symbols.Symbol;
import frontend.symbols.SymbolTable;
import midend.IrValue;
import midend.symbols.IrSymbolFunc;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunction;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrBinaryInstruction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionType;
import midend.value.instructions.terminal.IrCall;

import java.util.ArrayList;

public class UnaryExp extends ParseUnit {
    private PrimaryExp primaryExp;
    private Token ident;
    private FuncRParams funcRParams;
    private UnaryOp unaryOp;
    private UnaryExp unaryExp;
    private SymbolTable symbolTable;

    public UnaryExp(String name, ArrayList<ParseUnit> subUnits,
                    PrimaryExp primaryExp, Token ident, FuncRParams funcRParams,
                    UnaryOp unaryOp, UnaryExp unaryExp, SymbolTable symbolTable) {
        super(name, subUnits);
        this.primaryExp = primaryExp;
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        this.symbolTable = symbolTable;
    }

    public Param getParam() {
        if (primaryExp != null) {
            return primaryExp.getParam();
        } else if (ident != null) {
            Symbol symbol = symbolTable.getSymbol(ident.getContext());
            Word returnType = ((FuncSymbol) symbol).getReturnType();
            return new Param(returnType, false);
        } else {
            return unaryExp.getParam();
        }
    }

    public void checkC(SymbolTable symbolTable) {
        if (ident != null ) {
            if (symbolTable.getSymbol(ident.getContext()) == null) {
                ErrorHandler.getInstance().addError(new Error('c', ident.getLineNum()));
            }
        }
    }

    public void checkDE(SymbolTable symbolTable) {
        if (ident != null) {
            Symbol symbol = symbolTable.getSymbol(ident.getContext());
            if (symbol instanceof FuncSymbol) {
                ((FuncSymbol) symbol).checkParams(funcRParams, ident);
            }
        }
    }
    public int calculate(LinkSymbolTable linkSymbolTable){
        int result=0;
        if(primaryExp!=null){
            result=primaryExp.calculate(linkSymbolTable);
        }else {
            if(unaryOp.getOpType().equals(Word.PLUS)){
                result+=unaryExp.calculate(linkSymbolTable);
            }else {
                result-=unaryExp.calculate(linkSymbolTable);
            }
        }
        return result;
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        if(primaryExp!=null){

            return primaryExp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
        }else if(ident!=null){
            IrSymbolFunc irSymbolFunc= (IrSymbolFunc) linkSymbolTable.getIrSymbol("@"+ident.getContext());
            IrFunction irFunction= (IrFunction) irSymbolFunc.getIrValue();
            ArrayList<IrValue> rParams=new ArrayList<>();
            if(funcRParams!=null){
                //这个地方的生成有个问题，如果是数字或者char类型，是直接用还是要多一个store符号
                //目前这个做法是直接用，也可以用store，加一个isParam的参数即可
                rParams=funcRParams.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
            }
            IrCall irCall=null;
            if(!irFunction.getIrValueType().equals(IrValueType.VOID)){
                int cnt=irFunctionCnt.getCnt();
                String name="%_LocalVariable"+cnt;
                irCall=new IrCall(irFunction.getIrValueType(),name,rParams.size()+1, IrInstructionType.Call,irFunction,rParams);
            }else {
                irCall=new IrCall(irFunction.getIrValueType(),"",rParams.size()+1,IrInstructionType.Call,irFunction,rParams);
            }
            instructions.add(irCall);
            return irCall;
        }else {
            IrBinaryInstruction irBinaryInstruction=null;
            if(unaryOp.getOpType().equals(Word.PLUS)){
                return unaryExp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
            }else if(unaryOp.getOpType().equals(Word.MINU)){
                IrValue left=new IrValue(IrValueType.I32,"-1");
                int cnt=irFunctionCnt.getCnt();
                String name="%_LocalVariable"+cnt;
                IrValue right=unaryExp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
                irBinaryInstruction=new IrBinaryInstruction(IrValueType.I32,name,IrInstructionType.MUL,left,right);
                instructions.add(irBinaryInstruction);
                return irBinaryInstruction;
            }else if(unaryOp.getOpType().equals(Word.NOT)){
                int cnt=irFunctionCnt.getCnt();
                String name="%_LocalVariable"+cnt;
                IrValue left=unaryExp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
                irBinaryInstruction=new IrBinaryInstruction(IrValueType.I32,name,IrInstructionType.Not,left,null);
                instructions.add(irBinaryInstruction);
                return irBinaryInstruction;
            }
        }
        return null;
    }

}
