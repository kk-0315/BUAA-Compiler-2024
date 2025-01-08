package frontend.lexer;
import frontend.parser.ParseUnit;

import java.util.ArrayList;
import java.util.HashMap;

public class Token extends ParseUnit {
    private Word word;
    private String context;
    private int lineNum;
    public Token(Word word,String context,int lineNum){
        super(context,new ArrayList<>());
        this.word=word;
        this.context=context;
        this.lineNum=lineNum;
    }

    @Override
    public String toString(){
        return word.toString()+" "+context;
    }
    public Word getWord(){
        return word;
    }
    public boolean isType(Word word){
        return this.word.equals(word);
    }
    public int getLineNum(){
        return lineNum;
    }
    public String getContext(){
        return context;
    }

}
