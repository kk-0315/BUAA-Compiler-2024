package midend.value.instructions;

import frontend.lexer.Word;
import frontend.parser.specificUnit.*;
import midend.IrValue;
import midend.symbols.IrSymbolVar;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunctionCnt;
import midend.value.function.IrFunctionGenerator;
import midend.value.function.IrParam;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrStore;
import midend.value.instructions.terminal.IrCall;
import midend.value.instructions.terminal.IrGoto;
import midend.value.instructions.terminal.IrRet;

import java.util.ArrayList;

import static midend.type.IrValueType.*;

public class IrInstructionGenerator {
    private LinkSymbolTable linkSymbolTable;
    private BlockItem blockItem;
    private ArrayList<IrInstruction> instructions;
    private IrFunctionCnt irFunctionCnt;
    private IrLabel endLabelForLoop;

    private IrLabel nextStepLabel;
    private IrBasicBlock endLabelForLoopBlock;
    private IrBasicBlock nextStepLabelBlock;

    //For循环
    public IrInstructionGenerator(LinkSymbolTable linkSymbolTable,BlockItem blockItem,IrFunctionCnt irFunctionCnt,IrLabel nextStepLabel,IrLabel endLabelForLoop,IrBasicBlock nextStepLabelBlock,IrBasicBlock endLabelForLoopBlock){
        this.linkSymbolTable=linkSymbolTable;
        this.blockItem=blockItem;
        this.irFunctionCnt=irFunctionCnt;
        this.instructions=new ArrayList<>();
        this.endLabelForLoop=endLabelForLoop;
        this.nextStepLabel=nextStepLabel;
        this.endLabelForLoopBlock=endLabelForLoopBlock;
        this.nextStepLabelBlock=nextStepLabelBlock;
    }
    public ArrayList<IrInstruction> generateIrInstructions(){
        //TODO: 指令生成，生成过程中要把blockItem中的变量也加入符号表
        if(blockItem instanceof ReturnStmt){
            generateIrInstructionFromRet();
        }else if(blockItem instanceof ConstDecl){
            generateIrInstructionFromConstDecl();
        }else if(blockItem instanceof VarDecl){
            generateIrInstructionFromVarDecl();
        }else if(blockItem instanceof AssignStmt){
            generateIrInstructionFromAssignStmt();
        }else if(blockItem instanceof ExpStmt){
            generateIrInstructionFromExpStmt();
        }else if(blockItem instanceof GetIntStmt){
            generateIrInstructionFromGetIntStmt();
        }else if(blockItem instanceof  GetCharStmt){
            generateIrInstructionFromGetCharStmt();
        }else if(blockItem instanceof PrintfStmt){
            generateIrInstructionFromPrintfStmt();
        }else if(blockItem instanceof BreakStmt){
            generateIrInstructionFromBreakStmt();
        }else if(blockItem instanceof ContinueStmt){
            generateIrInstructionFromContinueStmt();
        }

        return instructions;
    }
    public void generateIrInstructionFromContinueStmt(){
        IrGoto irGoto=new IrGoto(nextStepLabel);
        irGoto.setGotoBB(nextStepLabelBlock);
        instructions.add(irGoto);
    }
    public void generateIrInstructionFromBreakStmt(){
        if(endLabelForLoop!=null){
            IrGoto irGoto=new IrGoto(endLabelForLoop);
            irGoto.setGotoBB(endLabelForLoopBlock);
            instructions.add(irGoto);
        }
    }
    public void generateIrInstructionFromPrintfStmt(){
        IrValue irValue=((PrintfStmt)blockItem).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,false);
    }
    public void generateIrInstructionFromGetCharStmt(){
        int cnt= irFunctionCnt.getCnt();
        String name="%_LocalVariable"+cnt;
        IrCall irCall=new IrCall(I8,name,"@getchar");
        instructions.add(irCall);
        IrValue storeAddress=((GetCharStmt)blockItem).getlVal().generateMidCode(instructions,linkSymbolTable,irFunctionCnt,true);
        IrValue offset=storeAddress.getDimensionIndex(instructions,irFunctionCnt);
        IrStore irStore=new IrStore(irCall,storeAddress,offset);
        instructions.add(irStore);
    }
    public void generateIrInstructionFromGetIntStmt(){
        int cnt= irFunctionCnt.getCnt();
        String name="%_LocalVariable"+cnt;
        IrCall irCall=new IrCall(I32,name,"@getint");
        instructions.add(irCall);
        IrValue storeAddress=((GetIntStmt)blockItem).getlVal().generateMidCode(instructions,linkSymbolTable,irFunctionCnt,true);
        IrValue offset=storeAddress.getDimensionIndex(instructions,irFunctionCnt);
        IrStore irStore=new IrStore(irCall,storeAddress,offset);
        instructions.add(irStore);
    }
    public void generateIrInstructionFromExpStmt(){
        IrValue irValue=((ExpStmt)blockItem).generateMidCode(instructions,linkSymbolTable,irFunctionCnt,false);

    }
    public void generateIrInstructionFromAssignStmt(){
        IrValue storeValue=((AssignStmt)blockItem).getExp().generateMidCode(instructions,linkSymbolTable,irFunctionCnt,false);
        IrValue storeAddress=((AssignStmt)blockItem).getlVal().generateMidCode(instructions,linkSymbolTable,irFunctionCnt,true);
        IrValue offset=storeAddress.getDimensionIndex(instructions,irFunctionCnt);
        IrStore irStore=new IrStore(storeValue,storeAddress,offset);
        instructions.add(irStore);
    }
    public void generateIrInstructionFromVarDecl() {
        ArrayList<VarDef> varDefs=((VarDecl)blockItem).getVarDefs();
        Word word=((VarDecl)blockItem).getType();
        for(VarDef varDef:varDefs){
            IrValueType irValueType=null;
            if(word.equals(Word.INTTK)&&varDef.isArray()){
                irValueType=IrValueType.I32_ARR;
            }else if(word.equals(Word.INTTK)&&!varDef.isArray()){
                irValueType=I32;
            }else if(word.equals(Word.CHARTK)&&varDef.isArray()){
                irValueType=IrValueType.I8_ARR;
            }else {
                irValueType=I8;
            }
            IrSymbolVar irSymbolVar=new IrSymbolVar(varDef.getIdent().getContext(),false,varDef.isArray(),null);
            irSymbolVar.setDimension(varDef.getDimension(linkSymbolTable));
            //irSymbolVar.setInitVal(instructions,linkSymbolTable,irFunctionCnt,false,varDef.getInitVal());

            IrAlloc irAlloc=null;
            irAlloc=new IrAlloc(irValueType);
            irAlloc.setDimensionNum(varDef.getDimension(linkSymbolTable));
            int cnt= irFunctionCnt.getCnt();
            String name="%_LocalPtr"+cnt;
            irAlloc.setName(name);
            irSymbolVar.setIrValue(irAlloc);
            linkSymbolTable.addSymbol(irSymbolVar);

            instructions.add(irAlloc);


            //irSymbolVar.setInitVal(instructions,linkSymbolTable,irFunctionCnt,false,varDef.getInitVal());

            //加入赋初值的指令
            if(varDef.getInitVal()!=null){
                irSymbolVar.setAssigned(true);
                // 生成二元指令
                ArrayList<IrValue> initVals=varDef.getInitVal().generateMidCode(instructions,linkSymbolTable,irFunctionCnt,false);
                //生成赋值指令
                if(varDef.getDimension(linkSymbolTable)==0){
                    IrStore irStore=new IrStore(initVals.get(0),irAlloc,null);
                    instructions.add(irStore);
                }else {
                    for(int i=0;i<initVals.size();i++){
                        IrValue offset=new IrValue(I32,String.valueOf(i));
                        IrStore irStore=new IrStore(initVals.get(i),irAlloc,offset);
                        instructions.add(irStore);
                    }
                    if(irAlloc.getDimensionNum()>initVals.size()&&word.equals(Word.CHARTK)){
                        for(int i=initVals.size();i<irAlloc.getDimensionNum();i++){
                            IrValue irValue1=new IrValue(I8,String.valueOf((int)'\0'));
                            IrValue offset=new IrValue(I32,String.valueOf(i));
                            IrStore irStore=new IrStore(irValue1,irAlloc,offset);
                            instructions.add(irStore);
                        }
                    }
                }


            }
        }
    }

    //常量定义直接存进符号表，而变量需要alloc一个地址空间
//    public void generateIrInstructionFromConstDecl() {
//        ArrayList<ConstDef> constDefs=((ConstDecl)blockItem).getConstDefs();
//        Word word=((ConstDecl)blockItem).getType();
//        for(ConstDef constDef:constDefs){
//            IrValueType irValueType=null;
//            if(word.equals(Word.INTTK)&&constDef.isArray()){
//                irValueType=IrValueType.I32_ARR;
//            }else if(word.equals(Word.INTTK)&&!constDef.isArray()){
//                irValueType=I32;
//            }else if(word.equals(Word.CHARTK)&&constDef.isArray()){
//                irValueType=IrValueType.I8_ARR;
//            }else {
//                irValueType=I8;
//            }
//            IrSymbolVar irSymbolVar=new IrSymbolVar(constDef.getIdent().getContext(),true,constDef.isArray(),null);
//            irSymbolVar.setInitVal(constDef.getConstInitVal(),linkSymbolTable);
//            IrValue irValue=null;
//            if(constDef.isArray()){
//                irValue=new IrValue(irValueType,String.valueOf(irSymbolVar.getInitvals()));
//            }else {
//                irValue=new IrValue(irValueType,String.valueOf(irSymbolVar.getInitVal()));
//            }
//            irSymbolVar.setIrValue(irValue);
//            linkSymbolTable.addSymbol(irSymbolVar);
//        }
//    }
    public void generateIrInstructionFromConstDecl() {
        ArrayList<ConstDef> constDefs = ((ConstDecl) blockItem).getConstDefs();
        Word word = ((ConstDecl) blockItem).getType();
        for (ConstDef constDef : constDefs) {
            IrValueType irValueType = null;
            if (word.equals(Word.INTTK) && constDef.isArray()) {
                irValueType = IrValueType.I32_ARR;
            } else if (word.equals(Word.INTTK) && !constDef.isArray()) {
                irValueType = I32;
            } else if (word.equals(Word.CHARTK) && constDef.isArray()) {
                irValueType = IrValueType.I8_ARR;
            } else {
                irValueType = I8;
            }

            // 创建 IrSymbolVar 并设置初始值
            IrSymbolVar irSymbolVar = new IrSymbolVar(constDef.getIdent().getContext(), true, constDef.isArray(), null);
            irSymbolVar.setInitVal(constDef.getConstInitVal(), linkSymbolTable);

            // 创建 IrValue
            IrAlloc irAlloc = new IrAlloc(irValueType);
            if (constDef.isArray()) {
                irAlloc.setDimensionNum(constDef.getConstExp().calculate(linkSymbolTable));
            }
            int cnt = irFunctionCnt.getCnt();
            String name = "%_ConstPtr" + cnt;
            irAlloc.setName(name);
            irSymbolVar.setIrValue(irAlloc);



            // 添加到符号表
            linkSymbolTable.addSymbol(irSymbolVar);

            // 生成分配指令
            instructions.add(irAlloc);

            irSymbolVar.setAssigned(true);
            // 生成赋初值的指令
            //irSymbolVar.setInitVal(constDef.getConstInitVal(),linkSymbolTable);
            ArrayList<Integer> initVals=constDef.getConstInitVal().calculate(linkSymbolTable);
            if(!constDef.isArray()){
                IrValue irValue1=new IrValue(word.equals(Word.INTTK)?I32:I8,String.valueOf(initVals.get(0)));
                IrStore irStore=new IrStore(irValue1,irAlloc,null);
                instructions.add(irStore);
            }else{
                for(int i=0;i<initVals.size();i++){
                    IrValue irValue1=new IrValue(word.equals(Word.INTTK)?I32:I8,String.valueOf(initVals.get(i)));
                    IrValue offset=new IrValue(I32,String.valueOf(i));
                    IrStore irStore=new IrStore(irValue1,irAlloc,offset);
                    instructions.add(irStore);
                }
                if(irAlloc.getDimensionNum()>initVals.size()&&word.equals(Word.CHARTK)){
                    for(int i=initVals.size();i<irAlloc.getDimensionNum();i++){
                        IrValue irValue1=new IrValue(I8,String.valueOf((int)'\0'));
                        IrValue offset=new IrValue(I32,String.valueOf(i));
                        IrStore irStore=new IrStore(irValue1,irAlloc,offset);
                        instructions.add(irStore);
                    }
                }else if(irAlloc.getDimensionNum()>initVals.size()&&word.equals(Word.INTTK)){
                    for(int i=initVals.size();i<irAlloc.getDimensionNum();i++){
                        IrValue irValue1=new IrValue(I32,"0");
                        IrValue offset=new IrValue(I32,String.valueOf(i));
                        IrStore irStore=new IrStore(irValue1,irAlloc,offset);
                        instructions.add(irStore);
                    }
                }
            }
        }
    }



    public void generateIrInstructionFromRet() {
        IrValue retVal=null;
        IrRet irRet=null;
        if(((ReturnStmt)blockItem).getReturnVal()==null){
            irRet=new IrRet(VOID,IrInstructionType.Ret);
        }else {
            retVal=((ReturnStmt)blockItem).getReturnVal().generateMidCode(instructions,linkSymbolTable,irFunctionCnt,false);

            if(retVal!=null){
                irRet=new IrRet(retVal.getIrValueType(),IrInstructionType.Ret,retVal);
            }else {
                irRet=new IrRet(IrValueType.VOID,IrInstructionType.Ret);
            }
        }

        instructions.add(irRet);
    }

    public ArrayList<IrInstruction> getInstructions(){
        return instructions;
    }
}
