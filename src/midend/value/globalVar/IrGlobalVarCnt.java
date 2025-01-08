package midend.value.globalVar;

import frontend.ErrorHandler.ErrorHandler;

public class IrGlobalVarCnt {
    private static  IrGlobalVarCnt instance;
    private int cnt;
    private IrGlobalVarCnt(){
        cnt=0;
    }
    public int getCnt(){
        return cnt++;
    }
    public static IrGlobalVarCnt getInstance() {
        if (instance == null) {
            instance = new IrGlobalVarCnt();
        }
        return instance;
    }
}
