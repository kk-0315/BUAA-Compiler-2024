package frontend.parser.specificUnit;

import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.parser.Parser;

import java.util.Queue;

public class Param {
    private Word type;
    private boolean isArray;

    public Param(Word type, boolean isArray) {
        this.type = type;
        this.isArray = isArray;
    }

    public Word getType() {
        return type;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean equals(Param param) {
        if (!isArray && !param.isArray()) {
            if (type.equals(Word.VOIDTK) && param.getType().equals(Word.VOIDTK)) {
                return true;
            } else if (!type.equals(Word.VOIDTK) && !param.getType().equals(Word.VOIDTK)) {
                return true;
            }
        }
        return type.equals(param.getType()) && isArray == param.isArray();
    }


}
