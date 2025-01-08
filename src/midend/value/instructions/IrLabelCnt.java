package midend.value.instructions;

import midend.value.globalVar.IrGlobalVarCnt;

public class IrLabelCnt {
    private static IrLabelCnt instance;
    private int cnt;
    private IrLabelCnt(){
        cnt=0;
    }
    public int getCnt(){
        return cnt++;
    }
    public static IrLabelCnt getInstance() {
        if (instance == null) {
            instance = new IrLabelCnt();
        }
        return instance;
    }
}
