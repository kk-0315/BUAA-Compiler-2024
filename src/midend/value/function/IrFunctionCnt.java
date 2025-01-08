package midend.value.function;

import midend.value.globalVar.IrGlobalVarCnt;

public class IrFunctionCnt {

    private int cnt;
    public IrFunctionCnt(){
        cnt=0;
    }
    public int getCnt(){
        return cnt++;
    }

}
