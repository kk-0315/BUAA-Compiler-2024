package frontend.parser.specificUnit;

import frontend.ErrorHandler.Error;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.IrInstructionGenerator;
import midend.value.instructions.terminal.IrCall;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintfStmt extends Stmt {
    private Token printfTK;
    private Token stringConst;
    private ArrayList<Exp> args;

    public PrintfStmt(String name, ArrayList<ParseUnit> subUnits,
                      Token stringConst, ArrayList<Exp> args, Token printfTK) {
        super(name, subUnits);
        this.printfTK = printfTK;
        this.stringConst = stringConst;
        this.args = args;

    }

    public void checkL() {
        String regex = "%(d|c)";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 使用正则表达式查找匹配项
        Matcher matcher = pattern.matcher(stringConst.getContext());
        int matchCount = 0;

        // 遍历所有匹配项并计数
        while (matcher.find()) {
            matchCount++;
        }
        if (matchCount != args.size()) {
            ErrorHandler.getInstance().addError(new Error('l', printfTK.getLineNum()));
        }
    }
    public IrValue generateMidCode(ArrayList<IrInstruction> instructions, LinkSymbolTable linkSymbolTable, IrFunctionCnt irFunctionCnt,boolean isWritten){
        ArrayList<IrValue> irValues=new ArrayList<>();
        for(Exp exp:args){
            IrValue irValue=exp.generateMidCode(instructions,linkSymbolTable,irFunctionCnt,isWritten);
            irValues.add(irValue);
//            IrCall irCall=null;
//
//            if(irValue.getIrValueType().equals(IrValueType.I32)){
//                irCall=new IrCall("@putint",irValue);
//                irCall.setIrValueType(IrValueType.I32);
//            }else {
//                irCall =new IrCall("@putchar",irValue);
//                irCall.setIrValueType(IrValueType.I32);
//            }
//            instructions.add(irCall);
        }
        if(stringConst!=null){
            IrCall irCall=null;
            int cnt=0;
            for(int i=1;i<stringConst.getContext().length()-1;i++){
                if(stringConst.getContext().charAt(i)=='\\'){
                    IrValue irValue=new IrValue(IrValueType.I32,String.valueOf((int)('\n')));
                    irCall=new IrCall("@putchar",irValue);
                    i+=1;
                }else if(stringConst.getContext().charAt(i)=='%'&&(stringConst.getContext().charAt(i+1)=='d'||stringConst.getContext().charAt(i+1)=='c')){
                    IrValue irValue=irValues.get(cnt);
                    if(stringConst.getContext().charAt(i+1)=='d'){
                        irCall=new IrCall("@putint",irValue);
                    }else if(stringConst.getContext().charAt(i+1)=='c'){
                        irCall=new IrCall("@putchar", irValue);
                    }

                    i+=1;
                    cnt+=1;
                }
                else {
                    IrValue irValue=new IrValue(IrValueType.I32,String.valueOf((int)stringConst.getContext().charAt(i)));
                    irCall=new IrCall("@putchar",irValue);
                }

                instructions.add(irCall);
            }
        }
        return null;
    }
}
