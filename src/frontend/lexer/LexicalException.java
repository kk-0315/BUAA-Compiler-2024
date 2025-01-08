package frontend.lexer;
public class LexicalException extends Exception {
    private int lineNum;
    private Error error;

    public LexicalException(Error error) {

        this.error = error;
    }

    @Override
    public String toString(){
        return error.toString();
    }


}
