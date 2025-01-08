package Optimize;


import backend.instructions.Move;
import midend.IrModule;
import midend.IrValue;
import midend.UndefinedValue;
import midend.type.IrValueType;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.function.IrFunctionCnt;
import midend.value.instructions.*;
import midend.value.instructions.memory.IrMove;
import midend.value.instructions.terminal.IrBr;
import midend.value.instructions.terminal.IrGoto;

import java.util.*;

public class EliminatePhi {
    private IrModule irModule;
    public EliminatePhi(IrModule irModule){
        this.irModule=irModule;
    }
    public void run(){
        for(IrFunction irFunction:irModule.getIrFunctions()){
            phi2PCopy(irFunction);
            parallelizePCopy(irFunction);
        }
    }
    public boolean isConst(String name) {
        if (name.startsWith("@") || name.startsWith("%")) {
            return false;
        }
        return true;
    }
    public void parallelizePCopy(IrFunction irFunction){
        Iterator<IrBasicBlock> iteratorBlock=irFunction.getIrBasicBlocks().iterator();
        HashMap<IrValue,Integer> val2Reg=irFunction.getVal2Reg();
        while (iteratorBlock.hasNext()){
            IrBasicBlock irBasicBlock=iteratorBlock.next();
            ArrayList<IrMove> toBeAdd=new ArrayList<>();
            Iterator<IrInstruction> iteratorInstr=irBasicBlock.getInstructions().iterator();
            while (iteratorInstr.hasNext()){

                IrInstruction irInstruction=iteratorInstr.next();
                if(irInstruction instanceof PCopy){
                    ArrayList<IrValue> fromValues=((PCopy) irInstruction).getFromValues();
                    ArrayList<IrValue> destValues=((PCopy) irInstruction).getDestValues();
                    ArrayList<IrValue> tmpSrc=new ArrayList<>();
                    ArrayList<IrValue> tmpDst=new ArrayList<>();


                    //move $v4, $v5
                    //move $v6, $v4




                    //注意有的变量可能不在寄存器中
                    for(int j=fromValues.size()-1;j>=0;j--){
                        Boolean needAdd=false;
                        IrValue fromValue=fromValues.get(j);
                        if(!isConst(fromValue.getName())){
                            for(int k=j-1;k>=0;k--){
                                if(Objects.equals(val2Reg.get(destValues.get(k)), val2Reg.get(fromValue))){
                                    needAdd=true;
                                    break;
                                }
//                                else if(val2Reg.get(fromValue)==null&&fromValue.equals(destValues.get(k))){
//                                    needAdd=true;
//                                    break;
//                                }

                            }
                        }
                        if(needAdd){
                            IrValue tmp=new IrValue(fromValue.getIrValueType(),fromValue.getName()+"_tmp");

                            for(int k=0;k<fromValues.size();k++){
                                if(fromValues.get(k).equals(fromValue)){
                                    ((PCopy) irInstruction).modifyFromValue(fromValues.get(k),tmp);
                                }
                            }
                            tmpSrc.add(fromValue);
                            tmpDst.add(tmp);

                        }

                    }




                    for(int i=tmpSrc.size()-1;i>=0;i--){
                        ((PCopy) irInstruction).add2First(tmpSrc.get(i),tmpDst.get(i));
                    }
                    //转化为move
                    for(int j=0;j<fromValues.size();j++){
                        IrMove irMove=new IrMove(fromValues.get(j),destValues.get(j));
                        //irBasicBlock.add2FirstInstrExceptLabel(irMove);

                        toBeAdd.add(irMove);
                    }

                    //删除Pcopy
                    iteratorInstr.remove();

                }

            }
            for(IrMove irMove:toBeAdd){
                irBasicBlock.add2LastInstrBeforeBr(irMove);
            }
        }
    }

    public void insert2newMidBlock(PCopy pCopy,IrBasicBlock preBlock,IrBasicBlock curBlock,IrFunction irFunction){
        String name=irFunction.getIrBasicBlockCnt().getName();
        IrBasicBlock midBlock=new IrBasicBlock(irFunction,name);
        //第一条指令大概率为label
        midBlock.addInstruction(pCopy);


        //更改一系列关系
        ArrayList<IrBasicBlock> irBasicBlocks=irFunction.getIrBasicBlocks();
        IrInstruction lastInstr=preBlock.getLastInstr();
        if((lastInstr instanceof IrBr)){
            IrBasicBlock gotoBlock=((IrBr) lastInstr).getGotoBB();
            if(gotoBlock.equals(curBlock)){
                ((IrBr) lastInstr).setAndChangeGotoBB(midBlock);
                irFunction.getIrBasicBlocks().add(irFunction.getIrBasicBlocks().indexOf(curBlock),midBlock); //curBlock之前
            }else {
                irFunction.getIrBasicBlocks().add(irFunction.getIrBasicBlocks().indexOf(preBlock)+1,midBlock); //preBlock之后
            }
            IrLabel irLabel=curBlock.getLabelOrCreate();
            midBlock.addInstruction(new IrGoto(irLabel));
        }else if(lastInstr instanceof IrGoto){
            IrBasicBlock gotoBlock=((IrGoto) lastInstr).getGotoBB();

            if(gotoBlock.equals(curBlock)){
                ((IrGoto) lastInstr).setAndChangeGotoBB(midBlock);
                irFunction.getIrBasicBlocks().add(irFunction.getIrBasicBlocks().indexOf(curBlock),midBlock); //curBlock之前
            }

            IrLabel irLabel=curBlock.getLabelOrCreate();
            midBlock.addInstruction(new IrGoto(irLabel));
        }else {
            irFunction.getIrBasicBlocks().add(irFunction.getIrBasicBlocks().indexOf(preBlock)+1,midBlock); //preBlock之后
        }

        //修改前驱和后继关系
        preBlock.getSucBlocks().add(preBlock.getSucBlocks().indexOf(curBlock),midBlock);
        preBlock.getSucBlocks().remove(curBlock);
        curBlock.getPreBlocks().add(curBlock.getPreBlocks().indexOf(preBlock),midBlock);
        curBlock.getPreBlocks().remove(preBlock);

        //为mid添加前驱和后继关系
        midBlock.createNewPreBlocks();
        midBlock.getPreBlocks().add(preBlock);
        midBlock.createNewSucBlocks();
        midBlock.getSucBlocks().add(curBlock);

    }
    public void insert2end(PCopy pCopy,IrBasicBlock preBlock){
        preBlock.add2LastInstrBeforeBr(pCopy);
    }
    public void phi2PCopy(IrFunction irFunction){
        Iterator<IrBasicBlock> iteratorBlock=new ArrayList<>(irFunction.getIrBasicBlocks()).iterator();
        while (iteratorBlock.hasNext()){
            IrBasicBlock irBasicBlock=iteratorBlock.next();


            if(irBasicBlock.getInstructions().isEmpty()) continue;
            if(irBasicBlock.getFirstInstrExceptLabel()==null) continue;
            //初始化
            ArrayList<PCopy> pCopies=new ArrayList<>();
            for(int i=0;i<irBasicBlock.getPreBlocks().size();i++){
                pCopies.add(new PCopy());
            }

            Iterator<IrInstruction> iterator=irBasicBlock.getInstructions().iterator();
            while (iterator.hasNext()){
                IrInstruction irInstruction=iterator.next();
                if(irInstruction instanceof Phi){

                    for(int i=0;i<irInstruction.getOperands().size();i++){ //phi的操作数同preBlocks的数量一致，可见mem2Reg
                        pCopies.get(i).addValue(irInstruction.getOperands().get(i),irInstruction); //有可能来自同一基本块的多个变量
                    }
                    iterator.remove();
                }
            }

            for(int i=0;i<irBasicBlock.getPreBlocks().size();i++){ //给每个前驱块建立一个pcopy
                if(!pCopies.get(i).isEmpty()){
                    if(irBasicBlock.getPreBlocks().get(i).getSucBlocks().size()>1){
                        insert2newMidBlock(pCopies.get(i),irBasicBlock.getPreBlocks().get(i),irBasicBlock,irFunction);
                    }else {
                        insert2end(pCopies.get(i),irBasicBlock.getPreBlocks().get(i));
                    }
                }
            }
        }
    }

}
