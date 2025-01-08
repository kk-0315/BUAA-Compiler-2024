package frontend.lexer;
public class Preprocess {
    private StringBuilder context;

    public Preprocess(StringBuilder context) {
        this.context = context;
    }

    public StringBuilder process() {
        char ch;
        int len = context.length();
        int curPos = 0;
        int status = 0;
        while (curPos < len) {
            ch = context.charAt(curPos);
            switch (status) {
                case 0:
                    if (ch == '/') status = 1;
                    else if (ch == '"') status = 5;
                    else status = 0;
                    break;
                case 1:
                    if (ch == '/') {
                        status = 2;
                        context.replace(curPos - 1, curPos + 1, "  ");
                    } else if (ch == '*') {
                        status = 3;
                        context.replace(curPos - 1, curPos + 1, "  ");
                    } else status = 0;
                    break;
                case 2:
                    if (ch == '\n') status = 0; //行号问题
                    else context.setCharAt(curPos, ' ');
                    break;
                case 3:
                    if (ch == '*') {
                        status = 4;
                        context.setCharAt(curPos, ' ');
                    } else if (ch == '\n') { //行号问题
                        status = 3;
                    } else {
                        status = 3;
                        context.setCharAt(curPos, ' ');
                    }
                    break;
                case 4:
                    if (ch == '*') {
                        status = 4;
                        context.setCharAt(curPos, ' ');
                    } else if (ch == '/') {
                        status = 0;
                        context.setCharAt(curPos, ' ');
                    } else if (ch == '\n') { //行号问题
                        status = 3;

                    } else {
                        status = 3;
                        context.setCharAt(curPos, ' ');
                    }
                    break;
                case 5:
                    if(ch=='"'){
                        status=0;
                    }
                    break;
            }

            curPos++;
        }
        return context;
    }
}
