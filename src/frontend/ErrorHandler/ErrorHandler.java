package frontend.ErrorHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ErrorHandler {
    private static ErrorHandler instance;
    private ArrayList<Error> errorList;

    private ErrorHandler() {
        errorList = new ArrayList<>();
    }

    public static ErrorHandler getInstance() {
        if (instance == null) {
            instance = new ErrorHandler();
        }
        return instance;
    }
    public int getErrorNum(){
        return errorList.size();
    }
    public boolean hasError(Error error){
        for(int i=0;i<errorList.size();i++){
            if(errorList.get(i).getErrorType()==error.getErrorType() && errorList.get(i).getLineNum()==error.getLineNum()){
                return true;
            }
        }
        return false;
    }
    public void addError(Error error) {
        if(!hasError(error)){
            errorList.add(error);
        }
    }
    public void delLastError(){
        if(!errorList.isEmpty()){
            errorList.remove(errorList.size()-1);
        }
    }
    public ArrayList<Error> getErrors() {
        return errorList;
    }
    public void printErrors() {
        for (Error error : errorList) {
            System.out.println(error);
        }
    }
    public void sortErrorsByLine() {
        errorList.sort(Comparator.comparingInt(Error::getLineNum));
    }
}
