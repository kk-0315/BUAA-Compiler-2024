package frontend.symbols;

import frontend.lexer.Token;

public interface Symbol {

    String getName();

    String toString();

    Token getIdent();
}
