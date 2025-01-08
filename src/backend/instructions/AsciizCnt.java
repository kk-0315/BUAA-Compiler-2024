package backend.instructions;

import midend.value.globalVar.IrGlobalVarCnt;

public class AsciizCnt {
    private static AsciizCnt instance;
    private int cnt;
    private AsciizCnt(){
        cnt=0;
    }
    public int getCnt(){
        return cnt++;
    }
    public static AsciizCnt getInstance() {
        if (instance == null) {
            instance = new AsciizCnt();
        }
        return instance;
    }
}
