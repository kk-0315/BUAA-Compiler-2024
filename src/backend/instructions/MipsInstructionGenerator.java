package backend.instructions;

import backend.RegTable;
import backend.basicblocks.MipsBasicBlock;
import backend.symbols.MipsSymbol;
import backend.symbols.MipsSymbolTable;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.instructions.*;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrLoad;
import midend.value.instructions.memory.IrMove;
import midend.value.instructions.memory.IrStore;
import midend.value.instructions.terminal.IrBr;
import midend.value.instructions.terminal.IrCall;
import midend.value.instructions.terminal.IrGoto;
import midend.value.instructions.terminal.IrRet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MipsInstructionGenerator {
    private IrInstruction irInstruction;
    private MipsSymbolTable mipsSymbolTable; //指令所在符号表
    private MipsBasicBlock parentBasicBlock; //父基本块
    private ArrayList<MipsInstruction> mipsInstructions;


    public MipsInstructionGenerator(IrInstruction irInstruction, MipsSymbolTable mipsSymbolTable, MipsBasicBlock parentBasicBlock) {
        this.irInstruction = irInstruction;
        this.mipsSymbolTable = mipsSymbolTable;
        this.parentBasicBlock = parentBasicBlock;
        this.mipsInstructions = new ArrayList<>();
    }

    public ArrayList<MipsInstruction> generateMipsInstruction() {
        if (irInstruction instanceof IrRet) {
            generateMipsInstructionFromIrRet();
        } else if (irInstruction instanceof IrAlloc) {
            generateMipsInstructionFromIrAlloc();
        } else if (irInstruction instanceof IrLoad) {
            generateMipsInstructionFromIrLoad();
        } else if (irInstruction instanceof IrStore) {
            generateMipsInstructionFromIrStore();
        } else if (irInstruction instanceof IrCall) {
            generateMipsInstructionFromIrCall();
        }else if(irInstruction instanceof IrBinaryInstruction){
            generateMipsInstructionFromIrBinaryInstruction();
        }else if(irInstruction instanceof IrGoto){
            generateMipsInstructionFromIrGoto();
        }else if(irInstruction instanceof IrBr){
            generateMipsInstructionFromIrBr();
        }else if(irInstruction instanceof IrLabel){
            generateMipsInstructionFromIrLabel();
        }else if(irInstruction instanceof IrMove){
            generateMipsInstructionFromIrMove();
        }
        //TODO:更多的指令
        return mipsInstructions;
    }
    public void generateMipsInstructionFromIrMove(){
        IrMove irMove=((IrMove) irInstruction);
        if(!mipsSymbolTable.hasSymbol(irMove.getOperandFromIndex(1).getName())){
            mipsSymbolTable.addSymbol(irMove.getOperandFromIndex(1).getName(),new MipsSymbol(irMove.getOperandFromIndex(1).getName(),irMove.getOperandFromIndex(1).getDimensionNum()));

        }
        int destReg=readBack(26,irMove.getOperandFromIndex(1)); //k0

        if(isConst(irMove.getOperandFromIndex(0).getName())){
            mipsInstructions.add(new Li(destReg,Integer.parseInt(irMove.getOperandFromIndex(0).getName())));
        }else if(mipsSymbolTable.getRegofVal(irMove.getOperandFromIndex(0).getName())!=-1){
            mipsInstructions.add(new Move(destReg,mipsSymbolTable.getRegofVal(irMove.getOperandFromIndex(0).getName())));
        }else {
            MipsSymbol mipsSymbol=mipsSymbolTable.getMipsSymbol(irMove.getOperandFromIndex(0).getName());
            if(!mipsSymbol.hasRam()){
                mipsSymbolTable.getRegTable().allocRamFor(mipsSymbol);
            }
            mipsInstructions.add(new Lw(destReg,mipsSymbol.getBase(),mipsSymbol.getOffset()));

        }

        writeBack(destReg,irMove.getOperandFromIndex(1));
    }
    public void generateMipsInstructionFromIrLabel(){
        IrLabel irLabel=((IrLabel) irInstruction);
        mipsInstructions.add(new Label(irLabel.getName().substring(1)));
    }
    public void calculateBr(String gotoLabel,IrInstructionType irInstructionType,int op1Reg,int op2Reg){
        if(irInstructionType.equals(IrInstructionType.Beq)){
            mipsInstructions.add(new Beq(op1Reg,op2Reg,gotoLabel));
        }else {
            mipsInstructions.add(new Bne(op1Reg,op2Reg,gotoLabel));
        }
    }
    public void generateMipsInstructionFromIrBr(){
        IrBr irBr=((IrBr) irInstruction);
        IrInstructionType irInstructionType=irBr.getIrInstructionType();
        IrValue op1=irBr.getOperand1();
        IrValue op2=irBr.getOperand2();

        MipsSymbol op1Symbol=null;
        MipsSymbol op2Symbol=null;
        int op1Reg=26;
        int op2Reg=27;

        if(isConst(op1.getName())){
            if(isConst(op2.getName())){
                mipsInstructions.add(new Li(op1Reg,Integer.parseInt(op1.getName())));
                mipsInstructions.add(new Li(op2Reg,Integer.parseInt(op2.getName())));
                //均为常数

            }else {
                //op1为常数，op2不为

                mipsInstructions.add(new Li(op1Reg,Integer.parseInt(op1.getName())));
                op2Reg=readBack(op2Reg,op2);


            }
        }else {
            if(isConst(op2.getName())){
                //op1不为常数，op2为
                op1Reg=readBack(op1Reg,op1);
                mipsInstructions.add(new Li(op2Reg,Integer.parseInt(op2.getName())));
            }else {
                //均不为常数
                op1Reg=readBack(op1Reg,op1);
                op2Reg=readBack(op2Reg,op2);
            }
        }
        calculateBr(irBr.getOperandFromIndex(2).getName().substring(1),irInstructionType,op1Reg,op2Reg);

    }
    public void generateMipsInstructionFromIrGoto(){
        IrGoto irGoto=((IrGoto) irInstruction);
        IrLabel irLabel= (IrLabel) irGoto.getOperand1();
        mipsInstructions.add(new J(irLabel.getName().substring(1)));
    }
    public void calculateBinary(int destReg,IrInstructionType irInstructionType,int op1Reg,int op2Reg){
        switch (irInstructionType){
            case ADD:
                mipsInstructions.add(new Add(destReg,op1Reg,op2Reg));
                break;
            case SUB:
                mipsInstructions.add(new Sub(destReg,op1Reg,op2Reg));
                break;
            case DIV:
                mipsInstructions.add(new Div(destReg,op1Reg,op2Reg));
                break;
            case MUL:
                mipsInstructions.add(new Mul(destReg,op1Reg,op2Reg));
                break;
            case MOD:
                mipsInstructions.add(new Div(-1,op1Reg,op2Reg));
                mipsInstructions.add(new Mfhi(destReg));
                break;
            case Lt:
                mipsInstructions.add(new Slt(destReg,op1Reg,op2Reg));
                break;
            case Le:
                mipsInstructions.add(new Sle(destReg,op1Reg,op2Reg));
                break;
            case Gt:
                mipsInstructions.add(new Sgt(destReg,op1Reg,op2Reg));
                break;
            case Ge:
                mipsInstructions.add(new Sge(destReg,op1Reg,op2Reg));
                break;
            case Eq:
                mipsInstructions.add(new Seq(destReg,op1Reg,op2Reg));
                break;
            case Ne:
                mipsInstructions.add(new Sne(destReg,op1Reg,op2Reg));
                break;
            case Not:
                break;
            case Sll:
                mipsInstructions.add(new Sllv(destReg,op1Reg,op2Reg));
                break;
            case Sra:
                mipsInstructions.add(new Srav(destReg,op1Reg,op2Reg));
                break;
        }
    }

    public void generateMipsInstructionFromIrBinaryInstruction(){
        IrBinaryInstruction irBinaryInstruction=((IrBinaryInstruction) irInstruction);
        IrValue op1=irBinaryInstruction.getOperand1();
        IrValue op2=irBinaryInstruction.getOperand2(); //NOT的时候为空
        MipsSymbol left=new MipsSymbol(irBinaryInstruction.getName(),irBinaryInstruction.getDimensionNum());
        mipsSymbolTable.addSymbol(left.getName(),left);
        int leftReg=readBack(26,irBinaryInstruction);
        IrInstructionType irInstructionType=irBinaryInstruction.getIrInstructionType();
        MipsSymbol op1Symbol=null;
        MipsSymbol op2Symbol=null;
        int op1Reg=26;
        int op2Reg=27;
        if(op2==null){
            if(isConst(op1.getName())){

                mipsInstructions.add(new Li(op1Reg,Integer.parseInt(op1.getName())));
            }else {
                op1Reg=readBack(op1Reg,op1);
            }

            mipsInstructions.add(new Li(3,0));
            mipsInstructions.add(new Seq(leftReg,op1Reg,3));


        }else {

            if(isConst(op1.getName())){
                if(isConst(op2.getName())){

                    mipsInstructions.add(new Li(op1Reg,Integer.parseInt(op1.getName())));
                    mipsInstructions.add(new Li(op2Reg,Integer.parseInt(op2.getName())));
                    //均为常数

                }else {
                    //op1为常数，op2不为

                    op2Reg=readBack(op2Reg,op2);
                    mipsInstructions.add(new Li(op1Reg,Integer.parseInt(op1.getName())));

                }
            }else {
                if(isConst(op2.getName())){
                    //op1不为常数，op2为
                    op1Reg=readBack(op1Reg,op1);

                    mipsInstructions.add(new Li(op2Reg,Integer.parseInt(op2.getName())));
                }else {
                    //均不为常数
                    op1Reg=readBack(op1Reg,op1);
                    op2Reg=readBack(op2Reg,op2);
                }
            }
            calculateBinary(leftReg,irInstructionType,op1Reg,op2Reg);
        }

        writeBack(leftReg,irBinaryInstruction);


    }
    public int readBack(int destReg,IrValue irValue){
        if(mipsSymbolTable.getRegofVal(irValue.getName())!=-1){
            destReg=mipsSymbolTable.getRegofVal(irValue.getName());
        }else {
            MipsSymbol op2Symbol=mipsSymbolTable.getMipsSymbol(irValue.getName());
            if(!op2Symbol.hasRam()){
                mipsSymbolTable.getRegTable().allocRamFor(op2Symbol);
            }else {
                mipsInstructions.add(new Lw(destReg,op2Symbol.getBase(),op2Symbol.getOffset()));
            }
        }
        return destReg;
    }
    public int readBack(int destReg,IrValue irValue,ArrayList<MipsInstruction> instructions){
        if(mipsSymbolTable.getRegofVal(irValue.getName())!=-1){
            destReg=mipsSymbolTable.getRegofVal(irValue.getName());
        }else {
            MipsSymbol op2Symbol=mipsSymbolTable.getMipsSymbol(irValue.getName());
            if(!op2Symbol.hasRam()){
                mipsSymbolTable.getRegTable().allocRamFor(op2Symbol);
            }else {
                instructions.add(new Lw(destReg,op2Symbol.getBase(),op2Symbol.getOffset()));
            }
        }
        return destReg;
    }
    public void writeBack(int srcReg,IrValue irValue){
        if(mipsSymbolTable.getRegofVal(irValue.getName())==-1){
            MipsSymbol destSymbol=null;
            if(mipsSymbolTable.hasSymbol(irValue.getName())){
                destSymbol=mipsSymbolTable.getMipsSymbol(irValue.getName());
            }else {
                destSymbol=new MipsSymbol(irValue.getName(),0);

            }
            if(!destSymbol.hasRam()){
                mipsSymbolTable.getRegTable().allocRamFor(destSymbol);
            }
            mipsInstructions.add(new Sw(srcReg,destSymbol.getBase(),destSymbol.getOffset()));
        }
    }
    public void generateMipsInstructionFromIrCall() {
        IrCall irCall = ((IrCall) irInstruction);

        if (irCall.getFunctionName().equals("@putint") || irCall.getFunctionName().equals("@putchar")) {
            IrValue printValue = irCall.getOperandFromIndex(1);
            if (isConst(printValue.getName())) {
                mipsInstructions.add(new Li(4, Integer.parseInt(printValue.getName())));
            } else {
                int reg=readBack(26,printValue);
                mipsInstructions.add(new Move(4, reg));
            }

            if (irCall.getFunctionName().equals("@putint")) {
                mipsInstructions.add(new Li(2, 1));
                mipsInstructions.add(new Syscall());
            } else {
                mipsInstructions.add(new Li(2, 11));
                mipsInstructions.add(new Syscall());
            }
        } else if (irCall.getFunctionName().equals("@getint") || irCall.getFunctionName().equals("@getchar")) {
            MipsSymbol mipsSymbol = new MipsSymbol(irCall.getName(), irCall.getDimensionNum()); //创建临时变量
            mipsSymbolTable.addSymbol(mipsSymbol.getName(), mipsSymbol);
            int leftReg = readBack(26,irCall);

            Li li = null;
            if (irCall.getFunctionName().equals("@getint")) {
                li = new Li(2, 5);

            } else {
                li = new Li(2, 12);
            }
            mipsInstructions.add(li);
            mipsInstructions.add(new Syscall());
//            if(irCall.getFunctionName().equals("@getchar")){
//                mipsInstructions.add(new Andi(2,2,0xFF));
//            }
            mipsInstructions.add(new Move(leftReg, 2));
            writeBack(leftReg,irCall);
        } else {
            ArrayList<IrValue> irParams = irCall.getrParams();
            int newSpOffset = 0;
            HashMap<Integer,Integer> reg2Offset=new HashMap<>();
            //2.将所有有值的寄存器保存到$sp上,保存现场
            ArrayList<MipsInstruction> tmpInst=new ArrayList<>();
            int spOffset = 0;
            for (int i = 2; i < 32; i++) {
                if (mipsSymbolTable.isAllocated(i) || i == 31) {
                    tmpInst.add(new Sw(i, 29, spOffset));
                    reg2Offset.put(i,spOffset);
                    spOffset += 4;
                }
            }
            tmpInst.add(new Sw(30,29,spOffset));
            spOffset+=4;

            //先保存现场


            ArrayList<MipsInstruction> tmpInst1=new ArrayList<>();

            for (int i = 0; i < irParams.size(); i++) {
                IrValue param = irParams.get(i);
                if (mipsSymbolTable.hasSymbol(param.getName())) {
                    int paramReg=readBack(26,param,tmpInst1);
                    if(reg2Offset.containsKey(paramReg)){
                        if (i < 3) {
                            if(irParams.size()>=3){
                                tmpInst1.add(new Lw(5 + i, 29,reg2Offset.get(paramReg)+(irParams.size()-3)*4)); //直接传递
                            }else {
                                tmpInst1.add(new Lw(5 + i, 29,reg2Offset.get(paramReg))); //直接传递
                            }
                        } else {

                            tmpInst1.add(new Lw(26,29,reg2Offset.get(paramReg)+(irParams.size()-3)*4));
                            //tmpInst1.add(new Addi(29,29,-4));
                            tmpInst1.add(new Sw(26, 29, newSpOffset));
                        }
                    }else {
                        if (i < 3) {
                            tmpInst1.add(new Move(5 + i, paramReg)); //直接传递
                        } else {
                            //tmpInst1.add(new Addi(29,29,-4));
                            tmpInst1.add(new Sw(paramReg, 29, newSpOffset));
                        }
                    }

                } else { //常数参数
                    if (i < 3) {
                        tmpInst1.add(new Li(i + 5, Integer.parseInt(param.getName())));
                    } else {
                        //tmpInst1.add(new Addi(29,29,-4));
                        tmpInst1.add(new Li(26, Integer.parseInt(param.getName())));

                        tmpInst1.add(new Sw(26, 29, newSpOffset));
                    }
                }
                if (i >= 3) {

                    newSpOffset+=4;

                }
            }


            mipsInstructions.add(new Addi(29,29,-1*spOffset));
            mipsInstructions.addAll(tmpInst);
            if(!tmpInst1.isEmpty()){ //再传参
                mipsInstructions.add(new Addi(29,29,-newSpOffset));
                mipsInstructions.addAll(tmpInst1);
            }
            mipsInstructions.add(new Addi(30,30,mipsSymbolTable.getFpOffset()));


            //7.函数调用
            mipsInstructions.add(new Jal(irCall.getFunctionName().substring(1)));

            //8.恢复现场


            int retSpOffSet=spOffset; //copy一份，用于恢复sp

            spOffset-=4;
            mipsInstructions.add(new Lw(30,29,spOffset+newSpOffset));

            for (int i = 31; i >= 2; i--) {
                if ((mipsSymbolTable.isAllocated(i)|| i == 31)) {
                    spOffset -= 4;
                    mipsInstructions.add(new Lw(i, 29, spOffset+newSpOffset));
                }
            }

            mipsInstructions.add(new Addi(29, 29, retSpOffSet+newSpOffset));

            //9.处理返回值
            if (!irCall.getIrValueType().equals(IrValueType.VOID)) {
                mipsSymbolTable.addSymbol(irCall.getName(), new MipsSymbol(irCall.getName(),irCall.getDimensionNum()));
                if(mipsSymbolTable.getRegofVal(irCall.getName())!=-1){
                    mipsInstructions.add(new Move(mipsSymbolTable.getRegofVal(irCall.getName()),2));
                }else {
                    writeBack(2,irCall);
                }

            }
        }
    }

    public void generateMipsInstructionFromIrStore() {
        IrStore irStore = ((IrStore) irInstruction);
        IrValue storeValue = irStore.getStoreValue();
        IrValue storeAddress = irStore.getStoreAddress();
        IrValue offset = irStore.getOffset();
        int leftReg = 26, rightReg = 27;
        //将要存的值取到leftReg里
        if (isConst(storeValue.getName())) {

            mipsInstructions.add(new Li(leftReg, Integer.parseInt(storeValue.getName())));
        } else {
            leftReg = readBack(leftReg,storeValue);
        }
//        if (storeValue.getIrValueType() == IrValueType.I32 && (storeAddress.getIrValueType() == IrValueType.I8||storeAddress.getIrValueType()==IrValueType.I8_ARR)) {
//            mipsInstructions.add(new Andi(leftReg, leftReg, 0xFF));// 截断高位，保留最低 8 位
//        }
        //开始写回
        MipsSymbol rightSymbol = mipsSymbolTable.getMipsSymbol(storeAddress.getName());

        if (offset == null) {
            mipsInstructions.add(new Sw(leftReg,rightSymbol.getBase(),rightSymbol.getOffset()));

        } else {
            int offsetReg = 27; //k1
            MipsSymbol temp = null;


            if (isConst(offset.getName())) {
                mipsInstructions.add(new Li(offsetReg, Integer.parseInt(offset.getName())));
            } else {
                offsetReg=readBack(offsetReg,offset);

            }
            mipsInstructions.add(new Sll(3, offsetReg, 2));

            if(rightSymbol.isParam()){
                int tmpReg=readBack(27,storeAddress);
                mipsInstructions.add(new Add(3,3,tmpReg));

            }else {
                mipsInstructions.add(new Add(3, 3, rightSymbol.getBase()));
                mipsInstructions.add(new Addi(3, 3, rightSymbol.getOffset()));
            }
            mipsInstructions.add(new Sw(leftReg, 3, 0));
        }



    }

    public boolean isConst(String name) {
        if (name.startsWith("@") || name.startsWith("%")) {
            return false;
        }
        return true;
    }

    public void generateMipsInstructionFromIrLoad() {
        IrLoad irLoad = ((IrLoad) irInstruction);
        MipsSymbol leftSymbol = new MipsSymbol(irLoad.getName(),irLoad.getDimensionNum()); //临时变量
        mipsSymbolTable.addSymbol(leftSymbol.getName(), leftSymbol);
        IrValue value = irLoad.getOperandFromIndex(0);
        MipsSymbol rightSymbol = mipsSymbolTable.getMipsSymbol(value.getName());
        int leftReg = readBack(26,irLoad);
        if (irLoad.getOffset() == null) {
            if (irLoad.getIrValueType().toString().contains("ARR")) {
                if(rightSymbol.isParam()){
                    //leftReg=readBack(26,value);
                    if(mipsSymbolTable.getRegofVal(value.getName())!=-1){
                        mipsInstructions.add(new Move(leftReg,mipsSymbolTable.getRegofVal(value.getName())));
                    }else {
                        MipsSymbol mipsSymbol=mipsSymbolTable.getMipsSymbol(value.getName());
                        if(!mipsSymbol.hasRam()){
                            mipsSymbolTable.getRegTable().allocRamFor(mipsSymbol);
                        }else {
                            mipsInstructions.add(new Lw(leftReg,mipsSymbol.getBase(),mipsSymbol.getOffset()));
                        }
                    }
                }else {
                    mipsInstructions.add(new Addi(leftReg,rightSymbol.getBase(),rightSymbol.getOffset()));
                }
            } else {
                mipsInstructions.add(new Lw(leftReg,rightSymbol.getBase(),rightSymbol.getOffset()));
            }

        } else {
            int offsetReg = 27;
            IrValue offset = irLoad.getOffset();

            if (isConst(offset.getName())) {
                Li li = new Li(offsetReg, Integer.parseInt(offset.getName()));
                mipsInstructions.add(li);
            } else {
                offsetReg=readBack(offsetReg,offset);
            }

            mipsInstructions.add(new Sll(3, offsetReg, 2));

            if(rightSymbol.isParam()){
                //有问题
                int tmpReg=readBack(27,value);

                //mipsInstructions.add(new Addi(tmpReg, rightSymbol.getBase(), rightSymbol.getOffset()));
                //mipsInstructions.add(new Lw(tmpReg,tmpReg,0));
                mipsInstructions.add(new Add(3,3,tmpReg));

            }else {

                mipsInstructions.add(new Add(3, 3, rightSymbol.getBase()));
                mipsInstructions.add(new Addi(3, 3, rightSymbol.getOffset()));
            }



            mipsInstructions.add(new Lw(leftReg, 3, 0));

        }
        writeBack(leftReg,irLoad);


    }

    //将变量加入符号表，但先不分配寄存器,需要分配内存
    public void generateMipsInstructionFromIrAlloc() {
        IrAlloc irAlloc = ((IrAlloc) irInstruction);
        MipsSymbol mipsSymbol = new MipsSymbol(irAlloc.getName(),irAlloc.getDimensionNum());
        mipsSymbol.setOffset(mipsSymbolTable.getFpOffset());
        if (mipsSymbol.getDimensionNum() == 0) {
            mipsSymbolTable.addFpOffset(4);
        } else {
            mipsSymbolTable.addFpOffset(mipsSymbol.getDimensionNum() * 4);
        }
        mipsSymbol.setHasRam(true);
        mipsSymbolTable.addSymbol(mipsSymbol.getName(), mipsSymbol);

    }

    public void generateMipsInstructionFromIrRet() {
        IrRet irRet = ((IrRet) irInstruction);
        if (parentBasicBlock.getParentFunction().getName().equals("main")) {
            mipsInstructions.add(new Li(2, 10));
            mipsInstructions.add(new Syscall());
        } else {
            if (irRet.getOpNum() == 0) { //VOID
                mipsInstructions.add(new Jr(31));
            } else {
                IrValue irValue = irRet.getOperandFromIndex(0);
                int reg=26;
                if (isConst(irValue.getName())) {
                    mipsInstructions.add(new Li(2, Integer.parseInt(irValue.getName())));
                } else {
                    //直接找到存储该变量的寄存器
                    reg = readBack(reg,irValue);
                    mipsInstructions.add(new Move(2, reg));
                }
                mipsInstructions.add(new Jr(31));
            }
        }
    }

}
