package Optimize;

import midend.IrModule;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;
import midend.value.instructions.IrInstruction;
import midend.value.instructions.terminal.IrBr;
import midend.value.instructions.terminal.IrGoto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CFGBuilder {
    private IrModule irModule;
    private HashMap<IrBasicBlock, ArrayList<IrBasicBlock>> preBlocks;
    private HashMap<IrBasicBlock,ArrayList<IrBasicBlock>> sucBlocks;
    private HashMap<IrBasicBlock,ArrayList<IrBasicBlock>> domBlocks; //支配块包括自己
    private HashMap<IrBasicBlock,IrBasicBlock> directDomParent; //直接支配关系不包括本身
    private HashMap<IrBasicBlock,ArrayList<IrBasicBlock>> directDomChildren;
    private HashMap<IrBasicBlock,ArrayList<IrBasicBlock>> DFs;

    public CFGBuilder(IrModule irModule){
        this.irModule=irModule;
        this.preBlocks=new HashMap<>();
        this.sucBlocks=new HashMap<>();
        this.domBlocks=new HashMap<>();
        this.directDomParent=new HashMap<>();
        this.directDomChildren=new HashMap<>();
        this.DFs=new HashMap<>();
    }
    public void clearAll(){
        this.preBlocks=new HashMap<>();
        this.sucBlocks=new HashMap<>();
        this.domBlocks=new HashMap<>();
        this.directDomParent=new HashMap<>();
        this.directDomChildren=new HashMap<>();
        this.DFs=new HashMap<>();
    }
    public void run(){
        for(IrFunction irFunction:irModule.getIrFunctions()){
            clearAll();
            init(irFunction);
            genCFG(irFunction);
            genDom(irFunction);
            genDirectDom(irFunction);
            genDFs(irFunction);
        }
    }
    public void genDFs(IrFunction irFunction){
        ArrayList<IrBasicBlock> irBasicBlocks=irFunction.getIrBasicBlocks();
        for(Map.Entry<IrBasicBlock, ArrayList<IrBasicBlock>> entry : sucBlocks.entrySet()){
            IrBasicBlock a=entry.getKey();
            for(IrBasicBlock b:entry.getValue()){
                IrBasicBlock runner=a;
                while (runner != null &&(!runner.getDirectDomChildrenBlocks().contains(b))){
                    DFs.get(runner).add(b);
                    runner=runner.getDirectDomParentBlock();
                }
            }
        }
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            irBasicBlock.setDFs(DFs.get(irBasicBlock));
        }
    }
    public void genDirectDom(IrFunction irFunction){
        ArrayList<IrBasicBlock> irBasicBlocks=irFunction.getIrBasicBlocks();
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            ArrayList<IrBasicBlock> domBlocks=irBasicBlock.getDomBlocks();
            for(IrBasicBlock domee:domBlocks){
                if(isDirectDom(irBasicBlock,domee)){
                    directDomChildren.get(irBasicBlock).add(domee);
                    directDomParent.put(domee,irBasicBlock);
                }
            }
        }
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            irBasicBlock.setDirectDomParentBlock(directDomParent.get(irBasicBlock));
            irBasicBlock.setDirectDomChildrenBlocks(directDomChildren.get(irBasicBlock));
        }
    }
    public Boolean isDirectDom(IrBasicBlock domer,IrBasicBlock domee){
        //首先是严格支配关系
        if(domer.equals(domee)||!domer.getDomBlocks().contains(domee)){
            return false;
        }
        //其次根据定义来
        for(IrBasicBlock domeeBlock:domer.getDomBlocks()){
            if(domeeBlock.getDomBlocks().contains(domee)&&!domeeBlock.equals(domer)&&!domeeBlock.equals(domee)){
                return false;
            }
        }
        return true;
    }
    public void genDom(IrFunction irFunction){
        ArrayList<IrBasicBlock> irBasicBlocks=irFunction.getIrBasicBlocks();
        IrBasicBlock entryBlock=irBasicBlocks.get(0);
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            HashSet<IrBasicBlock> domSet= new HashSet<>();
            dfsSearchDomBlocks(entryBlock,irBasicBlock,domSet); //dfs搜索到达irBlock之前都会reach到哪些点，这些肯定不是被irBlock支配的，剩下的就是了
            for(IrBasicBlock irBasicBlock1:irBasicBlocks){
                if(!domSet.contains(irBasicBlock1)){
                    domBlocks.get(irBasicBlock).add(irBasicBlock1);
                }
            }
            irBasicBlock.setDomBlocks(domBlocks.get(irBasicBlock));

        }
    }
    public void dfsSearchDomBlocks(IrBasicBlock entryBlock,IrBasicBlock targetBlock,HashSet<IrBasicBlock> domSet){
        if(entryBlock.equals(targetBlock)){
            return ;
        }
        domSet.add(entryBlock);
        for(IrBasicBlock irBasicBlock:entryBlock.getSucBlocks()){
            if(!domSet.contains(irBasicBlock)){
                dfsSearchDomBlocks(irBasicBlock,targetBlock,domSet);
            }
        }
    }

    public void genCFG(IrFunction irFunction){
        ArrayList<IrBasicBlock> irBasicBlocks=irFunction.getIrBasicBlocks();
        //求出每个Block的前驱和后继block集合
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            IrInstruction irInstruction=irBasicBlock.getLastInstr();
            if(!(irInstruction instanceof IrBr ||irInstruction instanceof IrGoto)){
                int index=irBasicBlocks.indexOf(irBasicBlock);
                if(index!=-1&&index<irBasicBlocks.size()-1){
                    IrBasicBlock nextBlock=irBasicBlocks.get(index+1);
                    sucBlocks.get(irBasicBlock).add(nextBlock);
                    preBlocks.get(nextBlock).add(irBasicBlock);
                }
            }else if (irInstruction instanceof IrBr) {
                IrBasicBlock gotoBB=((IrBr)(irInstruction)).getGotoBB();
                sucBlocks.get(irBasicBlock).add(gotoBB);
                preBlocks.get(gotoBB).add(irBasicBlock);
                int index=irBasicBlocks.indexOf(irBasicBlock);
                if(index!=-1&&index<irBasicBlocks.size()-1){
                    IrBasicBlock nextBlock=irBasicBlocks.get(index+1);
                    sucBlocks.get(irBasicBlock).add(nextBlock);
                    preBlocks.get(nextBlock).add(irBasicBlock);
                }

            }
            else if (irInstruction instanceof IrGoto) {
                IrBasicBlock gotoBB = ((IrGoto) irInstruction).getGotoBB();
                sucBlocks.get(irBasicBlock).add(gotoBB);
                preBlocks.get(gotoBB).add(irBasicBlock);
            }

        }
        for(IrBasicBlock irBasicBlock:irBasicBlocks){
            irBasicBlock.setPreBlocks(preBlocks.get(irBasicBlock));
            irBasicBlock.setSucBlocks(sucBlocks.get(irBasicBlock));
        }
    }
    public void init(IrFunction irFunction){
        for(IrBasicBlock irBasicBlock:irFunction.getIrBasicBlocks()){
            preBlocks.put(irBasicBlock,new ArrayList<>());
            sucBlocks.put(irBasicBlock,new ArrayList<>());
            domBlocks.put(irBasicBlock,new ArrayList<>());
            directDomParent.put(irBasicBlock,null);
            directDomChildren.put(irBasicBlock,new ArrayList<>());
            DFs.put(irBasicBlock,new ArrayList<>());
        }
    }
}
