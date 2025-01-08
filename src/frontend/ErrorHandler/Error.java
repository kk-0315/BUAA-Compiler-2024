package frontend.ErrorHandler;
public class Error {
    private char errorType;
    private int lineNum;

    public Error(char errorType,int lineNum){
        this.errorType=errorType;
        this.lineNum=lineNum;
    }
    public int getLineNum() {
        return lineNum;
    }
    public char getErrorType(){
        return errorType;
    }

    @Override
    public String toString(){
        return String.format("%d %c",lineNum,errorType);
    }


}
