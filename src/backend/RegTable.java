package backend;

import backend.instructions.Lw;
import backend.instructions.MipsInstruction;
import backend.instructions.Sw;
import backend.symbols.MipsSymbol;
import backend.symbols.MipsSymbolTable;
import midend.IrValue;
import midend.value.function.IrFunction;

import java.util.*;

public class RegTable {
    private MipsSymbolTable mipsSymbolTable;
    private HashMap<Integer, MipsSymbol> regs;
    private HashMap<Integer,Boolean> hasValues;
    private int theOldestTReg=8; //在t0-t9之间循环，当没有可用临时寄存器时，将最旧寄存器中的内容写回内存
    private HashMap<IrValue,Integer> val2Reg;
//    private int theOldestSReg=16;
    public RegTable(HashMap<IrValue,Integer> val2Reg){
        this.val2Reg=val2Reg;
        this.hasValues = new HashMap<>();
        this.regs = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            if (isSReg(i) || isTReg(i) || isAReg(i) || isVReg(i) || isRaReg(i)) {
                this.hasValues.put(i, false);
            } else {
                // 不可以被分配的寄存器
                this.hasValues.put(i, true);
            }
        }
        //this.sRegStack= new LinkedList<>();

    }
    public void setUsed(int regIndex){
        if(hasValues.get(regIndex)){
            regs.get(regIndex).setUsed(true);
        }
    }


    public boolean needSave(int i){
        if(hasValues.get(i)&&regs.get(i).isTemp()&&!regs.get(i).isUsed()||hasValues.get(i)&&!regs.get(i).isTemp()){
            return true;
        }
        return false;
    }

    public boolean isFree(int i){
        if(hasValues.get(i)){
            return false;
        }
        return true;
    }
    public void addSymbol(MipsSymbol mipsSymbol){
        this.regs.put(mipsSymbol.getRegIndex(),mipsSymbol);
        this.hasValues.put(mipsSymbol.getRegIndex(),true);
    }
    public void setMipsSymbolTable(MipsSymbolTable mipsSymbolTable){
        this.mipsSymbolTable=mipsSymbolTable;
    }
    public boolean isSReg(int i){
        if (16 <= i && i <= 23) { // s0-s7
            return true;
        } else {
            return false;
        }
    }
    public boolean isTReg(int i){
        if (8 <= i && i <= 15 || 24<=i && i<=25) { // t0-t9
            return true;
        } else {
            return false;
        }
    }
    public boolean isAReg(int i){
        if (4 <= i && i <= 7 ) { // a0-a3
            return true;
        } else {
            return false;
        }
    }
    public boolean isVReg(int i){
        if (2 <= i && i <= 3 ) { // v0 v1
            return true;
        } else {
            return false;
        }
    }
    public boolean isRaReg(int i){
        if (i==31 ) { // ra
            return true;
        } else {
            return false;
        }
    }
    public int searchFreeReg(boolean isTemp){
        if(isTemp){ //寻找临时寄存器t0-t9
            for(int i=0;i<32;i++){
                if(isTReg(i)){
                    if(isFree(i)){
                        return i;
                    }else { //临时变量已经被使用
                        MipsSymbol mipsSymbol=regs.get(i);
                        if(mipsSymbol.isTemp()&&mipsSymbol.isUsed()){
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }
    public void updateOldestTReg(int usedRegIndex){
        if(usedRegIndex==theOldestTReg){
            if (this.theOldestTReg < 15) {
                this.theOldestTReg++;
            } else if (this.theOldestTReg == 15) {
                this.theOldestTReg = 24;  // 跳到 t8
            } else if (this.theOldestTReg == 25) {
                this.theOldestTReg = 8;   // 回到 t0
            } else {
                this.theOldestTReg++;  // 正常递增
            }
        }
    }
//    public void updateOldestSReg(int usedRegIndex){
//        if(usedRegIndex==theOldestSReg){
//            if (this.theOldestSReg < 23) {
//                this.theOldestSReg++;
//            } else if (this.theOldestSReg == 23) {
//                this.theOldestSReg = 16;  // 跳到 S0
//            }
//        }
//    }
    public void readBack(int regIndex,MipsSymbol mipsSymbol,ArrayList<MipsInstruction> instructions){
        Lw lw=new Lw(regIndex,mipsSymbol.getBase(),mipsSymbol.getOffset());
        instructions.add(lw);

    }
    public void writeBack(MipsSymbol mipsSymbol,ArrayList<MipsInstruction> mipsInstructions){ //临时寄存器不够用写回

        mipsInstructions.add(new Sw(mipsSymbol.getRegIndex(),mipsSymbol.getBase(),mipsSymbol.getOffset()));
        hasValues.put(mipsSymbol.getRegIndex(),false);
        mipsSymbol.setRegIndex(-1);

    }

    public void allocRamFor(MipsSymbol mipsSymbol){
        mipsSymbol.setBase(30);
        mipsSymbol.setOffset(mipsSymbolTable.getFpOffset());
        mipsSymbolTable.addFpOffset(4);
        mipsSymbol.setHasRam(true);
    }

    public boolean hasValue(int regIndex){
        return hasValues.get(regIndex);
    }
//    public int distributeRegForSymbol(IrValue irValue){
//        return val2Reg.get(irValue);
//    }
    public int distributeRegForSymbol(MipsSymbol mipsSymbol, ArrayList<MipsInstruction> mipsInstructions){ //分配临时寄存器
        boolean isTemp=mipsSymbol.isTemp();
        if(!isTemp){
            return -1;
        }
        int freeReg=searchFreeReg(isTemp);
        if(freeReg!=-1){


            mipsSymbol.setRegIndex(freeReg);
            regs.put(freeReg,mipsSymbol);
            hasValues.put(freeReg,true);

            updateOldestTReg(freeReg);

            return freeReg;
        }else {

            if(hasValues.get(theOldestTReg)&&regs.get(theOldestTReg).getRegIndex()!=-1&&!regs.get(theOldestTReg).isUsed()){
                if(!regs.get(theOldestTReg).hasRam()){
                    allocRamFor(regs.get(theOldestTReg));
                }
                writeBack(regs.get(theOldestTReg),mipsInstructions);
            }


            mipsSymbol.setRegIndex(theOldestTReg);
            regs.put(theOldestTReg,mipsSymbol);
            hasValues.put(theOldestTReg,true);


            int ptr=theOldestTReg;
            updateOldestTReg(theOldestTReg);
            return ptr;

        }

    }

}
