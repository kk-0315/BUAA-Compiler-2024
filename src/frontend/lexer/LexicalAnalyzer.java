package frontend.lexer;
import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.Token;
import frontend.ErrorHandler.Error;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LexicalAnalyzer {
    private String text;
    private int curPos;
    private ArrayList<Token> tokens;
    private int lineNum;
    //private ArrayList<Error> errors;

    public LexicalAnalyzer(String text){
        this.text=text;
        this.curPos=-1;
        this.tokens=new ArrayList<>();
        this.lineNum=1;
        //this.errors=new ArrayList<>();
    }
    public char getSym(){
//        if(text.charAt(curPos)=='\n'){
//            lineNum++;
//        }
        this.curPos++;
        if (curPos<text.length())return this.text.charAt(this.curPos);
        else return '\0';

    }
    public boolean is_blank(char ch){
        if (ch==' '||ch=='\t') return true;
        else return false;
    }
    public void retract(){
        this.curPos--;
//        if(text.charAt(curPos)=='\n'){
//            lineNum--;
//        }
    }
    public boolean isValidAscii(char ch){
        if (ch>=32&&ch<=126) return true;
        else  return false;
    }
    public boolean isNoneDigitAlpha(char ch){
        if(ch=='_'||ch>='a'&&ch<='z'||ch>='A'&&ch<='Z') return true;
        else return false;
    }
    public Word reserver(String s){
        if(Objects.equals(s, "main")) return Word.MAINTK;
        else if(Objects.equals(s, "const")) return Word.CONSTTK;
        else if(Objects.equals(s, "int")) return Word.INTTK;
        else if(Objects.equals(s, "char")) return Word.CHARTK;
        else if(Objects.equals(s, "break")) return Word.BREAKTK;
        else if(Objects.equals(s, "continue")) return Word.CONTINUETK;
        else if(Objects.equals(s, "if")) return Word.IFTK;
        else if(Objects.equals(s, "else")) return Word.ELSETK;
        else if(Objects.equals(s, "for")) return Word.FORTK;
        else if(Objects.equals(s, "getint")) return Word.GETINTTK;
        else if(Objects.equals(s, "getchar")) return Word.GETCHARTK;
        else if(Objects.equals(s, "printf")) return Word.PRINTFTK;
        else if(Objects.equals(s, "return")) return Word.RETURNTK;
        else if(Objects.equals(s, "void")) return Word.VOIDTK;
        else return Word.IDENFR;
    }
    public void parse()  {
        Token curToken;

        char ch;
        char chNext;
        while (curPos<text.length()){
            ch=getSym();
            if (ch=='\0') return;
            while (is_blank(ch)){
                ch=getSym();
            }
            if(ch=='+') tokens.add(new Token(Word.PLUS,"+",lineNum));
            else if (ch=='-') tokens.add(new Token(Word.MINU,"-",lineNum));
            else if(ch=='*') tokens.add(new Token(Word.MULT,"*",lineNum));
            else if(ch==',') tokens.add(new Token(Word.COMMA,",",lineNum));
            else if(ch==';') tokens.add(new Token(Word.SEMICN,";",lineNum));
            else if (ch=='(') tokens.add(new Token(Word.LPARENT,"(",lineNum));
            else if(ch==')') tokens.add(new Token(Word.RPARENT,")",lineNum));
            else if(ch=='%') tokens.add(new Token(Word.MOD,"%",lineNum));
            else if(ch=='[') tokens.add(new Token(Word.LBRACK,"[",lineNum));
            else if(ch==']') tokens.add(new Token(Word.RBRACK,"]",lineNum));
            else if(ch=='{') tokens.add(new Token(Word.LBRACE,"{",lineNum));
            else if(ch=='}') tokens.add(new Token(Word.RBRACE,"}",lineNum));
            else if(ch=='/') tokens.add(new Token(Word.DIV,"/",lineNum));


            else if(ch=='<'){
                chNext=getSym();
                if(chNext=='=') tokens.add(new Token(Word.LEQ,"<=",lineNum));
                else{
                    retract();
                    tokens.add(new Token(Word.LSS,"<",lineNum));
                }
            }
            else if(ch=='>'){
                chNext=getSym();
                if(chNext=='=') tokens.add(new Token(Word.GEQ,">=",lineNum));
                else{
                    retract();
                    tokens.add(new Token(Word.GRE,">",lineNum));
                }
            }
            else if(ch=='='){
                chNext=getSym();
                if(chNext=='=') tokens.add(new Token(Word.EQL,"==",lineNum));
                else {
                    retract();
                    tokens.add(new Token(Word.ASSIGN,"=",lineNum));
                }
            }
            else if(ch=='!'){
                chNext=getSym();
                if(chNext=='=') tokens.add(new Token(Word.NEQ,"!=",lineNum));
                else {
                    retract();
                    tokens.add(new Token(Word.NOT,"!",lineNum));
                }
            }
            else if(ch=='&'){
                chNext=getSym();
                if(chNext=='&') tokens.add(new Token(Word.AND,"&&",lineNum));
                else{
                    tokens.add(new Token(Word.AND,"&",lineNum));
                    retract();
                    //throw new LexicalException(new Error('a',lineNum));
                    ErrorHandler.getInstance().addError(new Error('a',lineNum));
                }
            }
            else if(ch=='|'){
                chNext=getSym();
                if(chNext=='|') tokens.add(new Token(Word.OR,"||",lineNum));
                else{
                    tokens.add(new Token(Word.OR,"|",lineNum));
                    retract();
                    //throw new LexicalException(new Error('a',lineNum));
                    ErrorHandler.getInstance().addError(new Error('a',lineNum));
                }
            }
            else if(Character.isDigit(ch)){
                StringBuilder sb=new StringBuilder();

                while (Character.isDigit(ch)){
                    sb.append(ch);
                    ch=getSym();
                }
                retract();
                tokens.add(new Token(Word.INTCON,sb.toString(),lineNum));
            }
            else if(isNoneDigitAlpha(ch)){
                StringBuilder sb=new StringBuilder();
                while (isNoneDigitAlpha(ch)||Character.isDigit(ch)){
                    sb.append(ch);
                    ch=getSym();
                }
                retract();
                tokens.add(new Token(reserver(sb.toString()),sb.toString(),lineNum));

            }
            //竞速能过 词法语法不能过
//            case 'n':
//                tokens.add(new Token(Word.CHRCON,"'\n'",lineNum));
//                break;
//            case '"':
//                tokens.add(new Token(Word.CHRCON,"'\"'",lineNum));
//                break;
//            case '\'':
//                tokens.add(new Token(Word.CHRCON, "'''",lineNum));
//                break;
//            case '\\':
//                tokens.add(new Token(Word.CHRCON,"'\\\\'",lineNum));
//                break;
//            case 'a':
//                tokens.add(new Token(Word.CHRCON,"'\u0007'",lineNum));
//                break;
//            case 'b':
//                tokens.add(new Token(Word.CHRCON,"'\b'",lineNum));
//                break;
//            case 't':
//                tokens.add(new Token(Word.CHRCON,"'\t'",lineNum));
//                break;
//            case 'v':
//                tokens.add(new Token(Word.CHRCON, "'\u000B'",lineNum));
//                break;
//            case 'f':
//                tokens.add(new Token(Word.CHRCON,"'\f'",lineNum));
//                break;
//            case '0':
//                tokens.add(new Token(Word.CHRCON,"'\0'",lineNum));
//                break;
            else if(ch=='\''){
                ch=getSym();
                if(ch=='\\'){
                    chNext=getSym();
                    switch (chNext){
                        case 'n':
                            tokens.add(new Token(Word.CHRCON,"'\\n'",lineNum));
                            break;
                        case '"':
                            tokens.add(new Token(Word.CHRCON,"'\\\"'",lineNum));
                            break;
                        case '\'':
                            tokens.add(new Token(Word.CHRCON, "'\\''",lineNum));
                            break;
                        case '\\':
                            tokens.add(new Token(Word.CHRCON,"'\\\\'",lineNum));
                            break;
                        case 'a':
                            tokens.add(new Token(Word.CHRCON,"'\\a'",lineNum));
                            break;
                        case 'b':
                            tokens.add(new Token(Word.CHRCON,"'\\b'",lineNum));
                            break;
                        case 't':
                            tokens.add(new Token(Word.CHRCON,"'\\t'",lineNum));
                            break;
                        case 'v':
                            tokens.add(new Token(Word.CHRCON, "'\\v'",lineNum));
                            break;
                        case 'f':
                            tokens.add(new Token(Word.CHRCON,"'\\f'",lineNum));
                            break;
                        case '0':
                            tokens.add(new Token(Word.CHRCON,"'\\0'",lineNum));
                            break;
                        default:
                            break;
                    }
                }else {
                    tokens.add(new Token(Word.CHRCON,"'"+String.valueOf(ch)+"'",lineNum));
                }
                ch=getSym();
            }else if(ch=='"'){
                StringBuilder sb=new StringBuilder();
                sb.append('"');
                chNext=getSym();
                while (chNext!='"'){
                    sb.append(chNext);
                    chNext=getSym();
                }
                sb.append('"');
                tokens.add(new Token(Word.STRCON,sb.toString(),lineNum));
            }else {
                if (ch=='\n') lineNum++;


            }

        }
    }

    public ArrayList<Token> getTokens(){
        return tokens;
    }
//    public ArrayList<Error> getErrors(){
//        return errors;
//    }

}
