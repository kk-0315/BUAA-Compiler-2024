package Optimize;

import midend.IrModule;
import midend.IrValue;
import midend.value.basicBlock.IrBasicBlock;
import midend.value.function.IrFunction;

import java.util.HashMap;
import java.util.HashSet;

public class ActiveVarAnalyze {
    private IrModule irModule;
    private HashMap<IrBasicBlock, HashSet<IrValue>> in;
    private HashMap<IrBasicBlock,HashSet<IrValue>> out;
    public ActiveVarAnalyze(IrModule irModule){
        this.irModule=irModule;
        this.in=new HashMap<>();
        this.out=new HashMap<>();
    }
    public void init(IrFunction irFunction){
        this.in=new HashMap<>();
        this.out=new HashMap<>();
        for(IrBasicBlock irBasicBlock:irFunction.getIrBasicBlocks()){
            in.put(irBasicBlock,new HashSet<>());
            out.put(irBasicBlock,new HashSet<>());
        }
    }
    public void run(){
        for(IrFunction irFunction:irModule.getIrFunctions()){
            init(irFunction);
            for(IrBasicBlock irBasicBlock:irFunction.getIrBasicBlocks()) {
                irBasicBlock.genDefUse();
            }
            genInOut(irFunction);
        }
    }
    public void genInOut(IrFunction irFunction){
        Boolean isChange=true;
        while (isChange){
            isChange=false;
            //out依赖于后继块的in，因此在计算out前其后继块的in必须首先被算出，因此考虑反向遍历block
            for(int i=irFunction.getIrBasicBlocks().size()-1;i>=0;i--){
                IrBasicBlock B = irFunction.getIrBasicBlocks().get(i);
                HashSet<IrValue> sIn =new HashSet<>();
                for(IrBasicBlock S:B.getSucBlocks()){
                    sIn.addAll(in.get(S));
                }
                out.put(B,sIn);

                HashSet<IrValue> tmpIn=new HashSet<>();
                tmpIn.addAll(B.getUses());
                HashSet<IrValue> newIn=new HashSet<>();
                newIn.addAll(out.get(B));
                newIn.removeAll(B.getDefs());
                tmpIn.addAll(newIn);
                HashSet<IrValue> oldIn=in.get(B);
                if(!tmpIn.equals(oldIn)){
                    isChange=true;
                    in.put(B,tmpIn);
                }

                B.setIn(in.get(B));
                B.setOut(out.get(B));
            }
        }
    }
}
