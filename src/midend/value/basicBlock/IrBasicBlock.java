package midend.value.basicBlock;

import midend.IrUser;
import midend.IrValue;
import midend.type.IrValueType;
import midend.value.function.IrFunction;
import midend.value.function.IrFunctionCnt;
import midend.value.function.IrParam;
import midend.value.globalVar.IrGlobalVar;
import midend.value.instructions.*;
import midend.value.instructions.memory.IrAlloc;
import midend.value.instructions.memory.IrLoad;
import midend.value.instructions.terminal.IrBr;
import midend.value.instructions.terminal.IrCall;
import midend.value.instructions.terminal.IrGoto;

import java.util.ArrayList;
import java.util.HashSet;

public class IrBasicBlock extends IrValue {
    private ArrayList<IrInstruction> instructions;
    private IrFunction parentFunction;
    private ArrayList<IrBasicBlock> preBlocks;
    private ArrayList<IrBasicBlock> sucBlocks;
    private ArrayList<IrBasicBlock> domBlocks;
    private IrBasicBlock directDomParentBlock;
    private ArrayList<IrBasicBlock> directDomChildrenBlocks;
    private ArrayList<IrBasicBlock> DFs;

    private HashSet<IrValue> defs;
    private HashSet<IrValue> uses;
    private HashSet<IrValue> in;
    private HashSet<IrValue> out;
    private Boolean isDeleted=false;


    public IrBasicBlock(IrFunction parentFunction,String name) {
        super(IrValueType.LABEL,name);
        this.instructions=new ArrayList<>();
        this.parentFunction=parentFunction;
    }
    public void setIsDeleted(Boolean isDeleted){
        this.isDeleted=isDeleted;
    }
    public Boolean getIsDeleted(){
        return isDeleted;
    }
    public HashSet<IrValue> getIn(){
        return in;
    }
    public HashSet<IrValue> getOut(){
        return out;
    }
    public void setIn(HashSet<IrValue> in){
        this.in=in;
    }
    public void setOut(HashSet<IrValue> out){
        this.out=out;
    }

    public void genDefUse(){
        this.defs=new HashSet<>();
        this.uses=new HashSet<>();

        for(IrInstruction irInstruction:instructions){
            if(irInstruction instanceof Phi){
                for(IrValue operand:irInstruction.getOperands()){
                    if(operand instanceof IrInstruction||operand instanceof IrParam|| operand instanceof IrGlobalVar){
                        uses.add(operand);
                    }
                }
                if(!uses.contains(irInstruction)){
                    defs.add(irInstruction);
                }
            }else if(! (irInstruction instanceof IrLabel)){
                //先使用后定义
                for(IrValue operand:irInstruction.getOperands()){
                    if(!defs.contains(operand)&&((operand instanceof IrInstruction&&!(operand instanceof IrLabel))||operand instanceof IrParam|| operand instanceof IrGlobalVar)){
                        uses.add(operand);
                    }
                }
                //定义新的量，且先定义后使用
                if(!uses.contains(irInstruction)&&isDefInstr(irInstruction)){
                    defs.add(irInstruction);
                }
            }
        }

    }
    public Boolean isDefInstr(IrInstruction irInstruction){
        if(irInstruction instanceof IrAlloc&&(irInstruction.getIrValueType().equals(IrValueType.I8)||irInstruction.getIrValueType().equals(IrValueType.I32)) ||irInstruction instanceof IrLoad ||irInstruction instanceof IrCall&&!(((IrCall) irInstruction).getFunctionName().equals("@putint")||((IrCall) irInstruction).getFunctionName().equals("@putchar")) ||irInstruction instanceof IrBinaryInstruction ||irInstruction instanceof Phi){
            return true;
        }
        return false;
    }

    public HashSet<IrValue> getDefs(){
        return defs;
    }
    public HashSet<IrValue> getUses(){
        return uses;
    }
    public ArrayList<IrBasicBlock> getPreBlocks(){
        return preBlocks;
    }
    public void setDFs(ArrayList<IrBasicBlock> dFs){
        this.DFs=dFs;
    }
    public IrFunction getParentFunction(){
        return parentFunction;
    }
    public ArrayList<IrBasicBlock> getDFs(){
        return DFs;
    }
    public IrBasicBlock getDirectDomParentBlock(){
        return directDomParentBlock;
    }
    public ArrayList<IrBasicBlock> getDirectDomChildrenBlocks(){
        return directDomChildrenBlocks;
    }
    public ArrayList<IrBasicBlock> getSucBlocks(){
        return sucBlocks;
    }
    public void setDirectDomParentBlock(IrBasicBlock parentBlock){
        this.directDomParentBlock=parentBlock;
    }
    public void setDirectDomChildrenBlocks(ArrayList<IrBasicBlock> childrenBlocks){
        this.directDomChildrenBlocks=childrenBlocks;
    }
    public ArrayList<IrBasicBlock> getDomBlocks(){
        return domBlocks;
    }
    public void setDomBlocks(ArrayList<IrBasicBlock> domBlocks){
        this.domBlocks=domBlocks;
    }
    public void setPreBlocks(ArrayList<IrBasicBlock> preBlocks){
        this.preBlocks=preBlocks;
    }
    public void setSucBlocks(ArrayList<IrBasicBlock> sucBlocks){
        this.sucBlocks=sucBlocks;
    }


    public IrInstruction getLastInstr(){
        if(instructions.isEmpty()){
            return null;
        }else {
            return instructions.get(instructions.size()-1);
        }
    }
    public boolean isEmpty(){
        return instructions.isEmpty();
    }
    public ArrayList<IrInstruction> getInstructions(){
        return instructions;
    }
    public void addAllInstructions(ArrayList<IrInstruction> instructions){
        this.instructions.addAll(instructions);
        for(IrInstruction irInstruction:instructions){
            irInstruction.setParentBlock(this);
        }
    }
    public void createNewSucBlocks(){
        this.sucBlocks=new ArrayList<>();
    }
    public void createNewPreBlocks(){
        this.preBlocks=new ArrayList<>();
    }
    public IrLabel getLabelOrCreate(){
        if(instructions.isEmpty()){
            IrLabel irLabel=new IrLabel("%Label"+ IrLabelCnt.getInstance().getCnt());
            this.addInstruction(irLabel);
            return irLabel;
        } else if(instructions.get(0) instanceof IrLabel){
            return (IrLabel) instructions.get(0);
        }else {
            IrLabel irLabel=new IrLabel("%Label"+ IrLabelCnt.getInstance().getCnt());
            instructions.add(0,irLabel);
            irLabel.setParentBlock(this);
            return irLabel;
        }
    }
    public void addInstruction(IrInstruction irInstruction){
        instructions.add(irInstruction);
        irInstruction.setParentBlock(this);
    }
    public void add2FirstInstrExceptLabel(IrInstruction irInstruction){
        boolean seted=false;
        for(int i=0;i<instructions.size();i++){
            if(instructions.get(i) instanceof IrLabel) {
                continue;
            }else {
                instructions.add(i,irInstruction);
                irInstruction.setParentBlock(this);
                seted=true;
                break;
            }
        }
        if(!seted){
            instructions.add(irInstruction);
        }

    }
    public IrInstruction getFirstInstrExceptLabel(){
        for(int i=0;i<instructions.size();i++){
            if(instructions.get(i) instanceof IrLabel) {
                continue;
            }else {
                return instructions.get(i);
            }
        }
        return null;
    }

    public void add2LastInstrBeforeBr(IrInstruction irInstruction){ //插入最后一条跳转语句之前
        for(int i=instructions.size()-1;i>=0;i--){
            if(instructions.get(i) instanceof IrBr || instructions.get(i) instanceof IrGoto ) {
                continue;
            }else {
                instructions.add(i+1,irInstruction);
                return;
            }
        }
        instructions.add(0, irInstruction);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        //sb.append(getName()).append(": \n");
        for(IrInstruction irInstruction:instructions){
            sb.append(irInstruction.toString());
        }
        return sb.toString();
    }
}
