package midend.value.basicBlock;

public class IrBasicBlockCnt {
    private int cnt;
    public IrBasicBlockCnt(){
        cnt=0;
    }
    public int getCnt(){
        return cnt++;
    }
    public String getName(){
        int cnt=getCnt();
        return "Block"+cnt;
    }
}
