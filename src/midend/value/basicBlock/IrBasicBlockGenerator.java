package midend.value.basicBlock;

import frontend.lexer.Word;
import frontend.parser.specificUnit.*;
import midend.IrValue;
import midend.symbols.LinkSymbolTable;
import midend.type.IrValueType;
import midend.value.function.IrFunction;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.*;
import midend.value.instructions.memory.IrStore;
import midend.value.instructions.terminal.IrBr;
import midend.value.instructions.terminal.IrGoto;

import java.util.ArrayList;

public class IrBasicBlockGenerator {
    private LinkSymbolTable linkSymbolTable;
    private IrFunctionCnt functionCnt;
    private IrBasicBlockCnt irBasicBlockCnt;
    private IrFunction parentFunction;
    private Block block; //三种会形成基本块的语句
    private ForLoopStmt forLoopStmt; //三种会形成基本块的语句
    private IfStmt ifStmt; //三种会形成基本块的语句
    private ArrayList<BlockItem> blockItems;
    private ArrayList<IrBasicBlock> basicBlocks = new ArrayList<>();
    private IrLabel nextStepForLoop;
    private IrLabel endLabelForLoop;
    private IrBasicBlock nextStepForLoopBlock;
    private IrBasicBlock endLabelForLoopBlock;
    public IrBasicBlockGenerator(IrBasicBlockCnt irBasicBlockCnt,IrFunction parentFunction,LinkSymbolTable linkSymbolTable, Block block, IrFunctionCnt functionCnt,IrLabel nextStepForLoop,IrLabel endLabelForLoop,IrBasicBlock nextStepForLoopBlock,IrBasicBlock endLabelForLoopBlock) {
        this.linkSymbolTable = linkSymbolTable;
        this.functionCnt = functionCnt;
        this.block = block;
        this.blockItems = this.block.getBlockItems();
        this.nextStepForLoop=nextStepForLoop;
        this.endLabelForLoop=endLabelForLoop;
        this.nextStepForLoopBlock=nextStepForLoopBlock;
        this.endLabelForLoopBlock=endLabelForLoopBlock;
        this.parentFunction=parentFunction;
        this.irBasicBlockCnt=irBasicBlockCnt;
    }
    public IrBasicBlockGenerator(IrBasicBlockCnt irBasicBlockCnt,IrFunction parentFunction,LinkSymbolTable linkSymbolTable, ForLoopStmt forLoopStmt, IrFunctionCnt functionCnt) {
        this.linkSymbolTable = linkSymbolTable;
        this.functionCnt = functionCnt;
        this.forLoopStmt = forLoopStmt;
        this.parentFunction=parentFunction;
        this.irBasicBlockCnt=irBasicBlockCnt;
    }
    public IrBasicBlockGenerator(IrBasicBlockCnt irBasicBlockCnt,IrFunction parentFunction,LinkSymbolTable linkSymbolTable, IfStmt ifStmt, IrFunctionCnt functionCnt,IrLabel nextStepForLoop,IrLabel endLabelForLoop,IrBasicBlock nextStepForLoopBlock,IrBasicBlock endLabelForLoopBlock) {
        this.linkSymbolTable = linkSymbolTable;
        this.functionCnt = functionCnt;
        this.ifStmt = ifStmt;
        this.nextStepForLoop=nextStepForLoop;
        this.endLabelForLoop=endLabelForLoop;
        this.nextStepForLoopBlock=nextStepForLoopBlock;
        this.endLabelForLoopBlock=endLabelForLoopBlock;
        this.parentFunction=parentFunction;
        this.irBasicBlockCnt=irBasicBlockCnt;
    }
    public ArrayList<IrBasicBlock> generateIrBasicBlock() {
        if(block!=null){
            return generateIrBasicBlockFromBlock();
        }else if(ifStmt!=null){
            return generateIrBasicBlockFromIfStmt();
        }else {
            return generateIrBasicBlockFromForLoopStmt();
        }
    }
    public void generateBasicBlockFromEqExp(EqExp eqExp,IrLabel destLabel,IrBasicBlock nextLandBlock){
        IrBasicBlock irBasicBlock=new IrBasicBlock(parentFunction,irBasicBlockCnt.getName());
        ArrayList<IrInstruction> instructions1=new ArrayList<>();
        IrValue left=null,right=null;
        left=eqExp.getRelExps().get(0).generateMidCode(instructions1,linkSymbolTable,functionCnt,false);
        IrBr irBr=null;
        if(eqExp.getRelExps().size()==1){
            right=new IrValue(IrValueType.I32,String.valueOf(0));
            irBr=new IrBr(IrInstructionType.Beq,left,right,destLabel);

        }else if(eqExp.getRelExps().size()==2){
            right=eqExp.getRelExps().get(1).generateMidCode(instructions1,linkSymbolTable,functionCnt,false);
            if(eqExp.getOps().get(0).equals(Word.EQL)){
                irBr=new IrBr(IrInstructionType.Bne,left,right,destLabel);
            }else {
                irBr=new IrBr(IrInstructionType.Beq,left,right,destLabel);
            }

        }else {
            IrBinaryInstruction tmp=null;
            for(int i=1;i<eqExp.getRelExps().size()-1;i++){
                int cnt=functionCnt.getCnt();
                String name="%_LocalVariable"+cnt;
                right=eqExp.getRelExps().get(i).generateMidCode(instructions1,linkSymbolTable,functionCnt,false);
                if(eqExp.getOps().get(i-1).equals(Word.EQL)){
                    tmp=new IrBinaryInstruction(IrValueType.I32,name,IrInstructionType.Eq,left,right);

                }else {
                    tmp=new IrBinaryInstruction(IrValueType.I32,name,IrInstructionType.Ne,left,right);
                }
                left=tmp;
                instructions1.add(tmp);
            }
            right=eqExp.getRelExps().get(eqExp.getRelExps().size()-1).generateMidCode(instructions1,linkSymbolTable,functionCnt,false);
            if(eqExp.getOps().get(0).equals(Word.EQL)){
                irBr=new IrBr(IrInstructionType.Bne,left,right,destLabel);
            }else {
                irBr=new IrBr(IrInstructionType.Beq,left,right,destLabel);
            }

        }
        irBasicBlock.addAllInstructions(instructions1);
        irBr.setGotoBB(nextLandBlock);
        irBasicBlock.addInstruction(irBr);

        this.basicBlocks.add(irBasicBlock);
    }
    public IrLabel generateIrBasicBlockFromLAndExp(LAndExp lAndExp,IrLabel ifLabel,IrBasicBlock nextLandBlock,IrBasicBlock ifBlock){
        IrLabel irLabel=null;
        int cnt=IrLabelCnt.getInstance().getCnt();
        String name="%Label"+cnt;
        irLabel=new IrLabel(name);
        for(EqExp eqExp:lAndExp.getEqExps()){

            generateBasicBlockFromEqExp(eqExp,irLabel,nextLandBlock);

        }


        IrGoto irGoto=new IrGoto(ifLabel);
        irGoto.setGotoBB(ifBlock);
        IrBasicBlock gotoBlock=new IrBasicBlock(parentFunction,irBasicBlockCnt.getName());
        gotoBlock.addInstruction(irGoto);
        this.basicBlocks.add(gotoBlock);
        return irLabel;
    }
    public void generateBasicBlockFromLorExp(IrLabel destLabel,IrLabel ifLabel,LOrExp lOrExp,IrBasicBlock ifBlock,IrBasicBlock destBlock){


        for(LAndExp lAndExp:lOrExp.getlAndExps()){
            IrBasicBlock nextLandBlock=new IrBasicBlock(parentFunction,irBasicBlockCnt.getName());
            //不满足就继续继续判断下一个,满足则跳if
            IrLabel irLabel=generateIrBasicBlockFromLAndExp(lAndExp,ifLabel,nextLandBlock,ifBlock);
            if(irLabel!=null){
                //nextLandBlock.setName("NEXT-LANDLABEL");
                nextLandBlock.addInstruction(irLabel);
                this.basicBlocks.add(nextLandBlock);
            }
        }

        IrBasicBlock irBasicBlock=new IrBasicBlock(parentFunction,irBasicBlockCnt.getName());
        IrGoto irGoto=new IrGoto(destLabel);
        irGoto.setGotoBB(destBlock);
        irBasicBlock.addInstruction(irGoto);
        this.basicBlocks.add(irBasicBlock);

    }
    public ArrayList<IrBasicBlock> generateIrBasicBlockFromIfStmt(){

        //If-BLock

        int cnt= IrLabelCnt.getInstance().getCnt();
        String name="%Label"+cnt;
        IrLabel ifBlockLabel=new IrLabel(name);

        //Else-Block
        IrLabel elseBlockLabel=null;
        if(ifStmt.getElseStmt()!=null){
            cnt=IrLabelCnt.getInstance().getCnt();
            String name1="%Label"+cnt;
            elseBlockLabel=new IrLabel(name1);
        }

        //End-Label
        cnt=IrLabelCnt.getInstance().getCnt();
        String name2="%Label"+cnt;
        IrLabel endLabel=new IrLabel(name2);


        //处理if语句块
        IrBasicBlock ifBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
        //else语句块
        IrBasicBlock elseBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
        //end语句块
        IrBasicBlock endBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
        //处理Cond语句块
        if(elseBlockLabel!=null){
            generateBasicBlockFromLorExp(elseBlockLabel,ifBlockLabel,ifStmt.getCond().getlOrExp(),ifBlock,elseBlock);
        }else {
            generateBasicBlockFromLorExp(endLabel,ifBlockLabel,ifStmt.getCond().getlOrExp(),ifBlock,endBlock);
        }


        //ifBlock.setName("IF-BLOCK");
        ifBlock.addInstruction(ifBlockLabel);

        Stmt thenStmt=ifStmt.getThenStmt();
        LinkSymbolTable newLinkSymbolTable=new LinkSymbolTable(linkSymbolTable);

        if(thenStmt instanceof BlockStmt){
            this.basicBlocks.add(ifBlock);
            IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable,((BlockStmt)thenStmt).getBlock(),functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
            this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
            IrGoto irGoto=new IrGoto(endLabel);
            irGoto.setGotoBB(endBlock);
            IrBasicBlock irBasicBlock=new IrBasicBlock(parentFunction,irBasicBlockCnt.getName());
            irBasicBlock.addInstruction(irGoto);
            this.basicBlocks.add(irBasicBlock);
        }else if(thenStmt instanceof IfStmt){
            this.basicBlocks.add(ifBlock);
            IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable,(IfStmt) thenStmt,functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
            this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
            IrGoto irGoto=new IrGoto(endLabel);
            irGoto.setGotoBB(endBlock);
            IrBasicBlock irBasicBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
            irBasicBlock.addInstruction(irGoto);
            this.basicBlocks.add(irBasicBlock);
        }else if(thenStmt instanceof ForLoopStmt){
            this.basicBlocks.add(ifBlock);
            IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable,(ForLoopStmt) thenStmt,functionCnt);
            this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
            IrGoto irGoto=new IrGoto(endLabel);
            irGoto.setGotoBB(endBlock);
            IrBasicBlock irBasicBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
            irBasicBlock.addInstruction(irGoto);
            this.basicBlocks.add(irBasicBlock);
        }else {

            IrInstructionGenerator irInstructionBuilder = new IrInstructionGenerator(
                    linkSymbolTable, thenStmt,this.functionCnt, nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
            ArrayList<IrInstruction> instructions = irInstructionBuilder.generateIrInstructions();
            if (instructions != null) {
                ifBlock.addAllInstructions(instructions);
            }
            IrGoto irGoto=new IrGoto(endLabel);
            irGoto.setGotoBB(endBlock);
            ifBlock.addInstruction(irGoto);
            this.basicBlocks.add(ifBlock);


        }

        //处理else
        if(elseBlockLabel!=null){

            //elseBlock.setName("ELSE-BLOCK");
            elseBlock.addInstruction(elseBlockLabel);

            Stmt elseStmt=ifStmt.getElseStmt();
            LinkSymbolTable newLinkSymbolTable1=new LinkSymbolTable(linkSymbolTable);
            if(elseStmt instanceof BlockStmt){
                this.basicBlocks.add(elseBlock);
                IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable1,((BlockStmt)elseStmt).getBlock(),functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
                this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
                //不用加入goto语句，因为else结束顺序到结束
            }else if(elseStmt instanceof IfStmt){
                this.basicBlocks.add(elseBlock);
                IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable1,(IfStmt) elseStmt,functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
                this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());


            }else if(elseStmt instanceof ForLoopStmt){
                this.basicBlocks.add(elseBlock);
                IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable1,(ForLoopStmt) elseStmt,functionCnt);
                this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());

            }else {
                IrInstructionGenerator irInstructionBuilder = new IrInstructionGenerator(
                        linkSymbolTable, elseStmt, this.functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
                ArrayList<IrInstruction> instructions = irInstructionBuilder.generateIrInstructions();
                if (instructions != null) {
                    elseBlock.addAllInstructions(instructions);
                }
                this.basicBlocks.add(elseBlock);
            }
        }

        //endBlock.setName("END-LABEL");
        endBlock.addInstruction(endLabel);
        this.basicBlocks.add(endBlock);
        return this.basicBlocks;



    }
    public ArrayList<IrBasicBlock> generateIrBasicBlockFromForLoopStmt(){

        //cond-label
        int cnt=IrLabelCnt.getInstance().getCnt();
        String name="%Label"+cnt;
        IrLabel condLabel=new IrLabel(name);

        //if-block-label
        cnt=IrLabelCnt.getInstance().getCnt();
        name="%Label"+cnt;
        IrLabel ifBlockLabel=new IrLabel(name);

        IrLabel updateLabel=null;
        if(forLoopStmt.getUpdateStmt()!=null){
            cnt=IrLabelCnt.getInstance().getCnt();
            name="%Label"+cnt;
            updateLabel=new IrLabel(name);

        }

        //end-label
        cnt=IrLabelCnt.getInstance().getCnt();
        name="%Label"+cnt;
        IrLabel endLabel=new IrLabel(name);
        endLabelForLoop=endLabel;
        //cond-block
        IrBasicBlock condBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());

        //if-block处理
        IrBasicBlock ifBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
        //endBlock
        IrBasicBlock endBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
        //updateBlock
        IrBasicBlock updateBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());

        endLabelForLoopBlock=endBlock;


        if(updateLabel!=null){
            nextStepForLoop=updateLabel;
            nextStepForLoopBlock=updateBlock;
        }else {
            nextStepForLoop=condLabel;
            nextStepForLoopBlock=condBlock;
        }
        //初始化
        if(forLoopStmt.getInitStmt()!=null){
            IrBasicBlock initBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
            ArrayList<IrInstruction> instructions=new ArrayList<>();
            LVal lVal=forLoopStmt.getInitStmt().getlVal();
            Exp exp=forLoopStmt.getInitStmt().getExp();
            IrValue storeValue=exp.generateMidCode(instructions,linkSymbolTable,functionCnt,false);
            IrValue storeAddress=lVal.generateMidCode(instructions,linkSymbolTable,functionCnt,true);
            IrValue offset=storeAddress.getDimensionIndex(instructions,functionCnt);
            IrStore irStore=new IrStore(storeValue,storeAddress,offset);
            instructions.add(irStore);
            initBlock.addAllInstructions(instructions);
            this.basicBlocks.add(initBlock);
        }


        //condBlock.setName("Cond-Block");
        condBlock.addInstruction(condLabel);
        this.basicBlocks.add(condBlock);
        //cond处理
        if(forLoopStmt.getLoopCond()!=null){
            generateBasicBlockFromLorExp(endLabel,ifBlockLabel,forLoopStmt.getLoopCond().getlOrExp(),ifBlock,endBlock);
        }


        //ifBlock.setName("For-If-Block");
        ifBlock.addInstruction(ifBlockLabel);

        Stmt loopStmt=forLoopStmt.getLoopStmt();
        LinkSymbolTable linkSymbolTable1=new LinkSymbolTable(linkSymbolTable);
        if(loopStmt instanceof BlockStmt){
            this.basicBlocks.add(ifBlock);
            IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,linkSymbolTable1,((BlockStmt)loopStmt).getBlock(),functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
            this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());

        }else if(loopStmt instanceof IfStmt){
            this.basicBlocks.add(ifBlock);
            IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,linkSymbolTable1,((IfStmt)loopStmt),functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
            this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
        }else if(loopStmt instanceof ForLoopStmt){
            this.basicBlocks.add(ifBlock);
            IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,linkSymbolTable1,((ForLoopStmt)loopStmt),functionCnt);
            this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
        }else {

            IrInstructionGenerator irInstructionBuilder = new IrInstructionGenerator(
                    linkSymbolTable, loopStmt, this.functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
            ArrayList<IrInstruction> instructions = irInstructionBuilder.generateIrInstructions();
            if (instructions != null) {
                ifBlock.addAllInstructions(instructions);
            }
            this.basicBlocks.add(ifBlock);
        }


        //update-block
        if(forLoopStmt.getUpdateStmt()!=null){

            //updateBlock.setName("Update-Block");
            updateBlock.addInstruction(updateLabel);
            ArrayList<IrInstruction> instructions=new ArrayList<>();
            LVal lVal=forLoopStmt.getUpdateStmt().getlVal();
            Exp exp=forLoopStmt.getUpdateStmt().getExp();
            IrValue storeValue=exp.generateMidCode(instructions,linkSymbolTable,functionCnt,false);
            IrValue storeAddress=lVal.generateMidCode(instructions,linkSymbolTable,functionCnt,true);
            IrValue offset=storeAddress.getDimensionIndex(instructions,functionCnt);
            IrStore irStore=new IrStore(storeValue,storeAddress,offset);
            instructions.add(irStore);
            updateBlock.addAllInstructions(instructions);
            this.basicBlocks.add(updateBlock);
        }

        IrBasicBlock gotoBlock=new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
        //gotoBlock.setName("Goto-Block");
        IrGoto irGoto=new IrGoto(condLabel);
        irGoto.setGotoBB(condBlock);
        gotoBlock.addInstruction(irGoto);
        this.basicBlocks.add(gotoBlock);


        //加入end-label

        //endBlock.setName("End-Block");
        endBlock.addInstruction(endLabel);
        this.basicBlocks.add(endBlock);


        return this.basicBlocks;
    }
    public ArrayList<IrBasicBlock> generateIrBasicBlockFromBlock() {

        for (BlockItem item : this.blockItems) {
            LinkSymbolTable newLinkSymbolTable=new LinkSymbolTable(this.linkSymbolTable);
            if(item instanceof ForLoopStmt){
                IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable,((ForLoopStmt)(item)),functionCnt);
                this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
            }else if(item instanceof IfStmt){
                IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable,((IfStmt)(item)),functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
                this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());
            }else if(item instanceof BlockStmt){
                IrBasicBlockGenerator irBasicBlockGenerator=new IrBasicBlockGenerator(irBasicBlockCnt,parentFunction,newLinkSymbolTable,((BlockStmt)item).getBlock(),functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
                this.basicBlocks.addAll(irBasicBlockGenerator.generateIrBasicBlock());

            }else {
                IrBasicBlock basicBlock = new IrBasicBlock(parentFunction, irBasicBlockCnt.getName());
                IrInstructionGenerator irInstructionBuilder = new IrInstructionGenerator(
                        linkSymbolTable, item, this.functionCnt,nextStepForLoop,endLabelForLoop,nextStepForLoopBlock,endLabelForLoopBlock);
                ArrayList<IrInstruction> instructions = irInstructionBuilder.generateIrInstructions();
                if (instructions != null) {
                    basicBlock.addAllInstructions(instructions);
                }
                this.basicBlocks.add(basicBlock);
            }

        }


        return this.basicBlocks;
    }
}
