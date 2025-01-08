package Optimize;

import midend.IrModule;
import midend.IrUse;
import midend.IrValue;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.instructions.IrBinaryInstruction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.Phi;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrLoad;

import java.util.HashSet;
import java.util.Iterator;

public class DeadCodeKiller {
    private IrModule irModule;
    public DeadCodeKiller(IrModule irModule){
        this.irModule=irModule;
    }
    public void run(){
        for(IrFunction irFunction:irModule.getIrFunctions()){
            HashSet<IrInstruction> deadInstr=new HashSet<>();
            HashSet<IrInstruction> process=new HashSet<>();
            for(IrBasicBlock irBasicBlock:irFunction.getIrBasicBlocks()){
                Iterator<IrInstruction> iterator= irBasicBlock.getInstructions().iterator();
                while (iterator.hasNext()){
                    IrInstruction irInstruction=iterator.next();
//                    if(deleteDFS(irInstruction,deadInstr,process)){
//                        iterator.remove();
//                    }
                    if(canBeRemoved(irInstruction)){
                        iterator.remove();
                    }
                }
            }
        }
    }
    public Boolean canBeRemoved(IrInstruction irInstruction){
        if (irInstruction instanceof IrAlloc || irInstruction instanceof IrLoad || irInstruction instanceof IrBinaryInstruction || irInstruction instanceof Phi) {
            if(irInstruction.getIrUses().isEmpty()){
                return true;
            }
        }
        return false;
    }
//    public Boolean canBeDeleted(IrInstruction irInstruction){
//        if (irInstruction instanceof IrAlloc || irInstruction instanceof IrLoad || irInstruction instanceof IrBinaryInstruction || irInstruction instanceof Phi) {
////            if(irInstruction.getIrUses().isEmpty()){
////                return true;
////            }
//            return true;
//        }
//        return false;
//    }
//    public Boolean deleteDFS(IrInstruction irInstruction, HashSet<IrInstruction> deadInstr, HashSet<IrInstruction> process){
//        if(!canBeDeleted(irInstruction)){
//            return false;
//        }else if(deadInstr.contains(irInstruction)){
//            return true;
//        }else if(irInstruction.getIrUses().isEmpty()){
//            deadInstr.add(irInstruction);
//            return true;
//        }else if(process.contains(irInstruction)){ //正在递归处理
//            return false;
//        }else {
//            process.add(irInstruction);
//            Boolean isDead=true;
//            for(IrUse irUse:irInstruction.getIrUses()){
//                IrValue user=irUse.getIrUser();
//                isDead &= deleteDFS((IrInstruction) user,deadInstr,process);
//            }
//            if(isDead){
//                deadInstr.add(irInstruction);
//            }
//            return isDead;
//        }
//    }

}
