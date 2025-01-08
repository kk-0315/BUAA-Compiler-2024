package Optimize;

import midend.IrModule;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.terminal.IrBr;
import midend.value.instructions.terminal.IrGoto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Function;

public class SimplifyBB {
    private IrModule irModule;
    private ArrayList<IrBasicBlock> irBasicBlocks;

    public SimplifyBB(IrModule irModule) {
        this.irModule = irModule;
    }
    public void run(){
        for (IrFunction function : irModule.getIrFunctions()) {
            deleteNullBB(function);
            deleteNoneSense(function);
            deleteDeadBB(function);

        }
    }
    public void deleteNoneSense(IrFunction irFunction){
        for(IrBasicBlock irBasicBlock:irFunction.getIrBasicBlocks()){
            Boolean isEnd=false;
            Iterator<IrInstruction> iterator=irBasicBlock.getInstructions().iterator();
            while (iterator.hasNext()){
                IrInstruction irInstruction=iterator.next();
                if(isEnd){
                    iterator.remove();
                    continue;
                }
                if(irInstruction instanceof IrBr || irInstruction instanceof IrGoto){
                    isEnd=true;
                }

            }

        }
    }
    public void deleteNullBB(IrFunction irFunction){
        ArrayList<IrBasicBlock> irBasicBlocks=irFunction.getIrBasicBlocks();
        for (int i = irBasicBlocks.size() - 1; i >= 0; i--) {
            IrBasicBlock irBasicBlock = irBasicBlocks.get(i);

            // 检查 IrBasicBlock 是否为空（即没有指令）
            if (irBasicBlock.getInstructions().isEmpty()) {
                // 如果是空的，删除该块
                irBasicBlock.setIsDeleted(true);
                irBasicBlocks.remove(i);

            }
        }
    }
    public void deleteDeadBB(IrFunction irFunction){
        irBasicBlocks=irFunction.getIrBasicBlocks();
        HashSet<IrBasicBlock> vis=new HashSet<>();
        dfs(irBasicBlocks.get(0),vis);
        Iterator<IrBasicBlock> iterator = irFunction.getIrBasicBlocks().iterator();
        while (iterator.hasNext()) {
            IrBasicBlock bb = iterator.next();

            if (! vis.contains(bb)) {
                iterator.remove();
                bb.setIsDeleted(true);
            }
        }

    }
    private void dfs(IrBasicBlock entry, HashSet<IrBasicBlock>vis) {
        vis.add(entry);
        IrInstruction irInstruction=entry.getLastInstr();
        if(!(irInstruction instanceof IrBr ||irInstruction instanceof IrGoto)){
            int index=irBasicBlocks.indexOf(entry);
            if(index!=-1&&index<irBasicBlocks.size()-1){
                IrBasicBlock nextBlock=irBasicBlocks.get(index+1);
                if(! vis.contains(nextBlock)) dfs(nextBlock,vis);
            }
        }else if (irInstruction instanceof IrBr) {
            IrBasicBlock gotoBB=((IrBr)(irInstruction)).getGotoBB();
            if (! vis.contains(gotoBB)) dfs(gotoBB, vis);
            int index=irBasicBlocks.indexOf(entry);
            if(index!=-1&&index<irBasicBlocks.size()-1){
                IrBasicBlock nextBlock=irBasicBlocks.get(index+1);
                if(! vis.contains(nextBlock))dfs(nextBlock,vis);
            }
        }
        else if (irInstruction instanceof IrGoto) {
            IrBasicBlock gotoBB = ((IrGoto) irInstruction).getGotoBB();
            if(!vis.contains(gotoBB)) dfs(gotoBB,vis);
        }

    }
}
