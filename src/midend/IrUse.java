package midend;

public class IrUse {
    // Value在User中是第几个操作数，
    // 比如 add i32 %a,%b 中 %a 是第0个，记录这个关系的Use的operandRank就是0，%b是第1个
    private int operandRank;
    private IrUser irUser;
    private IrValue irValue;
    public IrUse(int operandRank,IrUser irUser,IrValue irValue){
        this.operandRank=operandRank;
        this.irUser=irUser;
        this.irValue=irValue;
    }
    public IrUser getIrUser(){
        return irUser;
    }
    public int getOperandRank(){
        return operandRank;
    }
    public void setIrValue(IrValue irValue){
        this.irValue=irValue;
    }
    public IrValue getIrValue(){
        return irValue;
    }
}
