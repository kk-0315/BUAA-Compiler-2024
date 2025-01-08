package frontend.parser;

import frontend.ErrorHandler.ErrorHandler;
import frontend.lexer.LexicalAnalyzer;
import frontend.lexer.Token;
import frontend.lexer.Word;
import frontend.ErrorHandler.Error;
import frontend.parser.specificUnit.*;
import frontend.parser.specificUnit.Character;
import frontend.parser.specificUnit.Number;
import frontend.symbols.FuncSymbol;
import frontend.symbols.Symbol;
import frontend.symbols.SymbolTable;
import frontend.symbols.VarSymbol;

import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

    private Token curToken;
    private int pos;
    private ArrayList<Token> tokens;
    private int area;
    private int topArea;
    private static SymbolTable symbolTable;
    private static boolean isLoop = false;
    //private HashMap<Integer, SymbolTable> symbols; //某一层的符号表
    //private ArrayList<Error> errors;
    //private HashMap<Integer,SymbolTable> resultSymbols;

    public Parser(ArrayList<Token> tokens) {
        pos = -1;
        this.tokens = tokens;
        area = 0;
        topArea = 0;
        symbolTable = new SymbolTable();
        //symbols=new HashMap<>();
        //resultSymbols=new HashMap<>();
        //errors=new ArrayList<>();
    }

    public void addArea() {
        topArea++;

        symbolTable.enterNewArea(topArea);
        area = symbolTable.getLastArea();
    }

    //    public void maxTheArea(){
//        area=topArea;
//
//    }
    private void subArea() {
        symbolTable.leaveArea();
        area = symbolTable.getLastArea();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    ////    public void removeArea(){
////        if (symbols.containsKey(area)) {
////            SymbolTable clonedTable = symbols.get(area).clone();
////            resultSymbols.put(area, clonedTable);
////        }
////        symbols.remove(area);
////        area--;
////    }
//    public boolean hasSymbol(String name){
//        for(SymbolTable s: symbols.values()){
//            if(s.hasSymbol(name)){
//                return true;
//            }
//        }
//        return false;
//    }
//    public void addVarSymbol(String name,boolean isConst,boolean isInt,int dimension,int lineNum){
//        if(symbols.get(area).hasSymbol(name)){
//            ErrorHandler.getInstance().addError(new Error('b',lineNum));
//        }else {
//            symbols.get(area).addSymbol(name,isConst,isInt,dimension,lineNum);
//        }
//
//    }
//    public void addFuncSymbol(String name,int returnType,ArrayList<VarSymbol> paras,int lineNum){
//        symbols.get(area).addSymbol(name,returnType,paras,lineNum);
//    }
//    public void addFuncSymbol(FuncSymbol funcSymbol){
//        if(symbols.get(area).hasSymbol(funcSymbol.getName())){
//            ErrorHandler.getInstance().addError(new Error('b',funcSymbol.getLineNum()));
//        }else {
//            symbols.get(1).addSymbol(funcSymbol);
//        }
//
//    }
    public void retract(int index) {
        pos -= index;
        if (pos >= 0) {
            curToken = tokens.get(pos);
        }
    }

    public boolean getToken(Word... words) {
        pos++;
        curToken = tokens.get(pos);
        if (words.length == 0) {
            return true;
        }
        for (Word word : words) {
            if (curToken.getWord().equals(word)) return true;
        }
        retract(1);
        return false;
    }

    public Comp parseCompUnit() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<ConstDecl> constDecls = new ArrayList<>();
        ArrayList<VarDecl> varDecls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();


        addArea();


        while (getToken(Word.CONSTTK, Word.CHARTK, Word.INTTK)) { //先预读一个，有可能是Decl、FuncDef、MainFuncDef

            if (curToken.isType(Word.CONSTTK)) {
                retract(1);
                ConstDecl constDecl = parseConstDecl();
                subUnits.add(constDecl);
                constDecls.add(constDecl);
            } else {
                //再预读一个
                if (!getToken(Word.IDENFR)) {
                    retract(1);
                    break;
                } else {
                    if (getToken(Word.LPARENT)) {
                        retract(3);
                        break;
                    } else {
                        retract(1);
                        VarDecl varDecl = parseVarDecl();
                        subUnits.add(varDecl);
                        varDecls.add(varDecl);
                    }

                }
            }
        }

        while (getToken(Word.VOIDTK, Word.INTTK, Word.CHARTK)) { //可能是FuncDef、MainFuncDef
            if (!curToken.isType(Word.INTTK)) {
                retract(1);
                FuncDef funcDef = parseFuncDef();
                subUnits.add(funcDef);
                funcDefs.add(funcDef);
            } else {
                if (getToken(Word.MAINTK)) {
                    retract(2);
                    break;
                } else {
                    retract(1);
                    FuncDef funcDef = parseFuncDef();
                    subUnits.add(funcDef);
                    funcDefs.add(funcDef);
                }
            }
        }
        MainFuncDef mainFuncDef = parseMainFuncDef();
        subUnits.add(mainFuncDef);

        return new Comp("CompUnit", subUnits, constDecls, varDecls, funcDefs, mainFuncDef);
    }

    //    public boolean isInt(Word word){
//        if(word.equals(Word.INTTK)){
//            return true;
//        }else {
//            return false;
//        }
//    }
    public ConstDecl parseConstDecl() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        getToken(Word.CONSTTK);
        subUnits.add(curToken);
        //subUnits.add(parseBType());
        getToken(Word.INTTK, Word.CHARTK);
        subUnits.add(curToken);
        Token BType = curToken;

        //boolean isInt=isInt(curToken.getWord());
        ConstDef constDef = parseConstDef();
        subUnits.add(constDef);
        constDefs.add(constDef);
        while (getToken(Word.COMMA)) {
            subUnits.add(curToken);
            constDef = parseConstDef();
            subUnits.add(constDef);
            constDefs.add(constDef);
        }
        if (getToken(Word.SEMICN)) {
            subUnits.add(curToken);
        } else {
            ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
        }
        ConstDecl constDecl = new ConstDecl("ConstDecl", subUnits, BType, constDefs);
        constDecl.addSymbol(symbolTable, area);
        return constDecl;
    }

    public ParseUnit parseBType() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        getToken(Word.INTTK, Word.CHARTK);
        subUnits.add(curToken);
        return new ParseUnit("BType", subUnits);

    }

    public ConstDef parseConstDef() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        Token ident = null;
        boolean isArray;
        ConstExp constExp=null;
        ConstInitVal constInitVal = null;

        getToken(Word.IDENFR);
        subUnits.add(curToken);
        ident = curToken;
        if (getToken(Word.LBRACK)) {
            subUnits.add(curToken);
            isArray = true;
            constExp=parseConstExp();
            subUnits.add(constExp);
            if (getToken(Word.RBRACK)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('k', curToken.getLineNum()));
            }
            //addVarSymbol(varName,true,isInt,1,line);

        } else {
            isArray = false;
            //addVarSymbol(varName,true,isInt,0,line);
        }
        getToken(Word.ASSIGN);
        subUnits.add(curToken);
        constInitVal = parseConstInitVal();
        subUnits.add(constInitVal);
        return new ConstDef("ConstDef", subUnits, ident, isArray, constInitVal,constExp);
    }

    public ConstInitVal parseConstInitVal() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<ConstExp> constExps = new ArrayList<>();
        Token stringConst = null;


        if (getToken(Word.STRCON)) {
            subUnits.add(curToken);
            stringConst = curToken;
        } else if (!getToken(Word.LBRACE)) {
            ConstExp constExp = parseConstExp();
            subUnits.add(constExp);
            constExps.add(constExp);
        } else {
            subUnits.add(curToken);
            if (!getToken(Word.RBRACE)) {
                ConstExp constExp = parseConstExp();
                subUnits.add(constExp);
                constExps.add(constExp);
                while (getToken(Word.COMMA)) {
                    subUnits.add(curToken);
                    constExp = parseConstExp();
                    subUnits.add(constExp);
                    constExps.add(constExp);
                }
            }
            getToken(Word.RBRACE);
            subUnits.add(curToken);
        }
        return new ConstInitVal("ConstInitVal", subUnits, constExps, stringConst);
    }

    public VarDecl parseVarDecl() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<VarDef> varDefs = new ArrayList<>();

        getToken(Word.INTTK, Word.CHARTK);
        subUnits.add(curToken);
        Token BType = curToken;
        //boolean isInt=isInt(curToken.getWord());
        VarDef varDef = parseVarDef();
        subUnits.add(varDef);
        varDefs.add(varDef);
        while (getToken(Word.COMMA)) {
            subUnits.add(curToken);
            varDef = parseVarDef();
            subUnits.add(varDef);
            varDefs.add(varDef);
        }
        if (getToken(Word.SEMICN)) {
            subUnits.add(curToken);
        } else {
            ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
        }
        VarDecl varDecl = new VarDecl("VarDecl", subUnits, BType, varDefs);
        varDecl.addSymbol(symbolTable, area);
        return varDecl;
    }

    public VarDef parseVarDef() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        Token ident = null;
        ConstExp constExp = null;
        InitVal initVal = null;
        boolean isArray;
        getToken(Word.IDENFR);
        subUnits.add(curToken);
        ident = curToken;

        if (getToken(Word.LBRACK)) {
            subUnits.add(curToken);
            isArray = true;
            constExp = parseConstExp();
            subUnits.add(constExp);
            if (getToken(Word.RBRACK)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('k', curToken.getLineNum()));
            }
            //addVarSymbol(varName,false,isInt,1,line);

        } else {
            isArray = false;
            //addVarSymbol(varName,false,isInt,0,line);
        }
        if (getToken(Word.ASSIGN)) {
            subUnits.add(curToken);
            initVal = parseInitVal();
            subUnits.add(initVal);
        }
        return new VarDef("VarDef", subUnits, ident, isArray, initVal,constExp);

    }

    public InitVal parseInitVal() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<Exp> exps = new ArrayList<>();
        Token stringConst = null;
        if (getToken(Word.STRCON)) {
            subUnits.add(curToken);
            stringConst = curToken;
        } else if (getToken(Word.LBRACE)) {
            subUnits.add(curToken);

            if (getToken(Word.LPARENT, Word.IDENFR, Word.PLUS, Word.MINU, Word.NOT, Word.INTCON, Word.CHRCON)) {
                retract(1);
                Exp exp = parseExp();
                subUnits.add(exp);
                exps.add(exp);
                while (getToken(Word.COMMA)) {
                    subUnits.add(curToken);
                    exp = parseExp();
                    subUnits.add(exp);
                    exps.add(exp);
                }
            }
            getToken(Word.RBRACE);
            subUnits.add(curToken);
        } else {
            Exp exp=parseExp();
            subUnits.add(exp);
            exps.add(exp);
        }
        return new InitVal("InitVal", subUnits, exps, stringConst);
    }

    public int funcType(Word word) {
        if (word.equals(Word.VOIDTK)) {
            return 1;
        } else if (word.equals(Word.INTTK)) {
            return 2;
        } else {
            return 3;
        }
    }

    public FuncDef parseFuncDef() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        FuncType funcType = null;
        Token ident = null;
        FuncFParams funcFParams = null;
        Block block = null;

        funcType = parseFuncType();
        subUnits.add(funcType);
        //int returnType = funcType(curToken.getWord());
        getToken(Word.IDENFR);
        subUnits.add(curToken);
        ident = curToken;
        String funcName = curToken.getContext();
        //FuncSymbol funcSymbol=new FuncSymbol(funcName,returnType,new ArrayList<>(),curToken.getLineNum());


        getToken(Word.LPARENT);
        subUnits.add(curToken);
//        addFuncSymbol(funcSymbol);
        //maxTheArea();
        addArea();
        symbolTable.updateMaxArea(area);
        //ArrayList<VarSymbol> varSymbolArrayList=new ArrayList<>();
        if (getToken(Word.INTTK, Word.CHARTK)) {
            retract(1);
            funcFParams = parseFuncFParams();
            subUnits.add(funcFParams);
            funcFParams.addSymbol(symbolTable, area);
        }

        if (getToken(Word.RPARENT)) {
            subUnits.add(curToken);
        } else {
            ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
        }

        FuncDef funcDef = new FuncDef("FuncDef", subUnits, funcType, ident, funcFParams, block);
        funcDef.addSymbol(symbolTable, area);

        block = parseBlock(true, false);
        //block.checkM();
        subUnits.add(block);

        funcDef.setBlock(block);


        Symbol tmp = symbolTable.getSymbol(funcDef.getIdent().getContext());
        if (tmp instanceof FuncSymbol) {
            funcDef.checkG();
            funcDef.checkF(symbolTable);
        }

        //funcDef.checkM();
        return funcDef;
    }

    public MainFuncDef parseMainFuncDef() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        Block block = null;

        getToken(Word.INTTK);
        subUnits.add(curToken);
        getToken(Word.MAINTK);
        subUnits.add(curToken);
        getToken(Word.LPARENT);
        subUnits.add(curToken);
        if (getToken(Word.RPARENT)) {
            subUnits.add(curToken);
        } else {
            ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
        }
        //maxTheArea();
        block = parseBlock(false, false);
        //block.checkM();
        subUnits.add(block);
        MainFuncDef mainFuncDef = new MainFuncDef("MainFuncDef", subUnits, block);
        //mainFuncDef.check();
        mainFuncDef.checkG();
        return mainFuncDef;
    }

    public FuncType parseFuncType() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        Token funcType = null;

        getToken(Word.VOIDTK, Word.INTTK, Word.CHARTK);
        subUnits.add(curToken);
        funcType = curToken;
        return new FuncType("FuncType", subUnits, funcType);
    }

    public FuncFParams parseFuncFParams() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();

        FuncFParam funcFParam = parseFuncFParam();
        subUnits.add(funcFParam);
        funcFParams.add(funcFParam);
        while (getToken(Word.COMMA)) {
            subUnits.add(curToken);
            funcFParam = parseFuncFParam();
            subUnits.add(funcFParam);
            funcFParams.add(funcFParam);
        }
        return new FuncFParams("FuncFParams", subUnits, funcFParams);
    }

    public FuncFParam parseFuncFParam() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        Token BType = null;
        Token ident = null;
        Boolean isArray;

        getToken(Word.INTTK, Word.CHARTK);
        subUnits.add(curToken);
        BType = curToken;
        //boolean isInt=isInt(curToken.getWord());
        getToken(Word.IDENFR);
        subUnits.add(curToken);
        ident = curToken;

        if (getToken(Word.LBRACK)) {
            subUnits.add(curToken);
            isArray = true;
            if (getToken(Word.RBRACK)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('k', curToken.getLineNum()));
            }
            //funcSymbol.addPara(varName,false,isInt,1,line);
            //addVarSymbol(varName,false,isInt,1,line);
        } else {
            isArray = false;
            //funcSymbol.addPara(varName,false,isInt,0,line);
            //addVarSymbol(varName,false,isInt,0,line);
        }

        return new FuncFParam("FuncFParam", subUnits, BType, ident, isArray);
    }

    public Block parseBlock(boolean fromFunc, boolean isFromLoop) {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<BlockItem> blockItems = new ArrayList<>();

        getToken(Word.LBRACE);
        subUnits.add(curToken);
        if (!fromFunc) {
            //maxTheArea();
            addArea();
            symbolTable.updateMaxArea(area);
        }
        while (!getToken(Word.RBRACE)) {
            //addArea();
            BlockItem blockItem = parseBlockItem(isFromLoop);
            subUnits.add(blockItem);
            blockItems.add(blockItem);
        }
        subUnits.add(curToken);
//        if(!fromFunc){
//            subArea();
//        }
        Token RBrace = curToken;
        subArea();
        return new Block("Block", subUnits, blockItems, RBrace);
    }


    public BlockItem parseBlockItem(boolean isFromLoop) {
        //ArrayList<ParseUnit> subUnits=new ArrayList<>();
        if (getToken(Word.CONSTTK)) {
            retract(1);
            //subUnits.add(parseConstDecl());

            return parseConstDecl();
        } else if (getToken(Word.INTTK, Word.CHARTK)) {
            retract(1);
            //subUnits.add(parseVarDecl());

            return parseVarDecl();
        } else {

            //subUnits.add(parseStmt());
            return parseStmt(isFromLoop);
        }
    }

    public Stmt parseStmt(boolean isFromLoop) {

        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        if (getToken(Word.IFTK)) { //if

            Cond cond = null;
            Stmt thenStmt = null;
            Stmt elseStmt = null;

            subUnits.add(curToken);
            getToken(Word.LPARENT);
            subUnits.add(curToken);
            cond = parseCond();
            subUnits.add(cond);
            if (getToken(Word.RPARENT)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
            }
            thenStmt = parseStmt(isFromLoop);
            //thenStmt.checkM();
            subUnits.add(thenStmt);
            if (getToken(Word.ELSETK)) {
                subUnits.add(curToken);
                elseStmt = parseStmt(isFromLoop);
                //elseStmt.checkM();
                subUnits.add(elseStmt);
            }
            return new IfStmt("Stmt", subUnits, cond, thenStmt, elseStmt);

        } else if (getToken(Word.FORTK)) { //for
            ForStmt initStmt = null;
            Cond loopCond = null;
            ForStmt updateStmt = null;
            Stmt loopStmt = null;

            subUnits.add(curToken);
            getToken(Word.LPARENT);
            subUnits.add(curToken);
            if (getToken(Word.IDENFR)) {
                retract(1);
                initStmt = parseForStmt();
                subUnits.add(initStmt);
            }
            getToken(Word.SEMICN);
            subUnits.add(curToken);
            if (getToken(Word.LPARENT, Word.IDENFR, Word.INTCON, Word.CHRCON, Word.PLUS, Word.MINU, Word.NOT)) {
                retract(1);
                loopCond = parseCond();
                subUnits.add(loopCond);
            }
            getToken(Word.SEMICN);
            subUnits.add(curToken);
            if (getToken(Word.IDENFR)) {
                retract(1);
                updateStmt = parseForStmt();
                subUnits.add(updateStmt);
            }
            getToken(Word.RPARENT);
            subUnits.add(curToken);

            loopStmt = parseStmt(true);
            //loopStmt.checkF();
            subUnits.add(loopStmt);

            return new ForLoopStmt("Stmt", subUnits, initStmt, loopCond, updateStmt, loopStmt);
        } else if (getToken(Word.BREAKTK)) { //break


            subUnits.add(curToken);
            Token breakTK = curToken;
            if (getToken(Word.SEMICN)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
            }
            BreakStmt breakStmt = new BreakStmt("Stmt", subUnits, breakTK, isFromLoop);
            breakStmt.checkM();
            return breakStmt;
        } else if (getToken(Word.CONTINUETK)) { //continue
            subUnits.add(curToken);
            Token continueTK = curToken;
            if (getToken(Word.SEMICN)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
            }
            ContinueStmt continueStmt = new ContinueStmt("Stmt", subUnits, continueTK, isFromLoop);
            continueStmt.checkM();
            return continueStmt;
        } else if (getToken(Word.RETURNTK)) { //return
            Token returnTK = null;
            Exp returnVal = null;

            subUnits.add(curToken);
            returnTK = curToken;
            int tmpPos=pos;
            if (getToken(Word.LPARENT, Word.IDENFR, Word.PLUS, Word.MINU, Word.NOT, Word.INTCON, Word.CHRCON)) {
                retract(1);
                returnVal = parseExp();
                subUnits.add(returnVal);
            }
            if(getToken(Word.ASSIGN)){
                retract(pos-tmpPos);
                returnVal=null;
            }
            if (getToken(Word.SEMICN)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
            }
            return new ReturnStmt("Stmt", subUnits, returnTK, returnVal);
        } else if (getToken(Word.PRINTFTK)) { //printf
            Token stringConst = null;
            ArrayList<Exp> args = new ArrayList<>();
            Token printfTK = null;

            subUnits.add(curToken);
            printfTK = curToken;
            getToken(Word.LPARENT);
            subUnits.add(curToken);
            getToken(Word.STRCON);
            subUnits.add(curToken);
            stringConst = curToken;
            while (getToken(Word.COMMA)) {
                subUnits.add(curToken);
                Exp arg = parseExp();
                subUnits.add(arg);
                args.add(arg);
            }
            if (getToken(Word.RPARENT)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
            }
            if (getToken(Word.SEMICN)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
            }
            PrintfStmt printfStmt = new PrintfStmt("Stmt", subUnits, stringConst, args, printfTK);
            printfStmt.checkL();
            return printfStmt;
        } else if (getToken(Word.LBRACE)) { //Block

            retract(1);
            Block block = parseBlock(false, isFromLoop);
            subUnits.add(block);
            return new BlockStmt("Stmt", subUnits, block);
        } else if (getToken(Word.IDENFR)) { //LVal
            if (getToken(Word.LPARENT)) {
                retract(2);
                Exp exp = parseExp();
                subUnits.add(exp);
                if (getToken(Word.SEMICN)) {
                    subUnits.add(curToken);
                } else {
                    ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
                }
                return new ExpStmt("Stmt", subUnits, exp);
            } else {
                retract(1);
                int tmpPos = pos;
                int pastErrorNum=ErrorHandler.getInstance().getErrorNum();
                LVal tmpUnits = parseLVal();
                //parseLVal();
                if (getToken(Word.ASSIGN)) {

                    subUnits.add(tmpUnits);

                    subUnits.add(curToken);
                    if (getToken(Word.GETINTTK)) { //getint
                        subUnits.add(curToken);
                        getToken(Word.LPARENT);
                        subUnits.add(curToken);
                        if (getToken(Word.RPARENT)) {
                            subUnits.add(curToken);
                        } else {
                            ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
                        }
                        if (getToken(Word.SEMICN)) {
                            subUnits.add(curToken);
                        } else {
                            ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
                        }
                        GetIntStmt getIntStmt = new GetIntStmt("Stmt", subUnits, tmpUnits);
                        getIntStmt.check(symbolTable);
                        return getIntStmt;
                    } else if (getToken(Word.GETCHARTK)) { //getchar
                        subUnits.add(curToken);
                        getToken(Word.LPARENT);
                        subUnits.add(curToken);
                        if (getToken(Word.RPARENT)) {
                            subUnits.add(curToken);
                        } else {
                            ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
                        }
                        if (getToken(Word.SEMICN)) {
                            subUnits.add(curToken);
                        } else {
                            ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
                        }
                        GetCharStmt getCharStmt = new GetCharStmt("Stmt", subUnits, tmpUnits);
                        getCharStmt.check(symbolTable);
                        return getCharStmt;
                    } else {

                        Exp exp = null;
                        if (!getToken(Word.SEMICN)) {
                            exp = parseExp();
                            subUnits.add(exp);
                        }
                        if (getToken(Word.SEMICN)) {
                            subUnits.add(curToken);
                        } else {
                            ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
                        }
                        AssignStmt assignStmt = new AssignStmt("Stmt", subUnits, tmpUnits, exp);
                        assignStmt.check(symbolTable);
                        return assignStmt;
                    }
                } else {
                    int nowErrorNum=ErrorHandler.getInstance().getErrorNum();
                    if(pastErrorNum!=nowErrorNum){
                        ErrorHandler.getInstance().delLastError();
                    }
                    retract(pos - tmpPos);
                    Exp exp = null;
                    if (!getToken(Word.SEMICN)) {
                        exp = parseExp();
                        subUnits.add(exp);
                    }
                    if (getToken(Word.SEMICN)) {
                        subUnits.add(curToken);
                    } else {
                        ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
                    }
                    return new ExpStmt("Stmt", subUnits, exp);
                }
            }


        } else { //Exp
            Exp exp = null;
            if (!getToken(Word.SEMICN)) {
                exp = parseExp();
                subUnits.add(exp);
            } else {
                retract(1);
            }

            if (getToken(Word.SEMICN)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('i', curToken.getLineNum()));
            }
            return new ExpStmt("Stmt", subUnits, exp);
        }

    }

    public ForStmt parseForStmt() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        LVal lVal = null;
        Exp exp = null;

        lVal = parseLVal();
        subUnits.add(lVal);
        getToken(Word.ASSIGN);
        subUnits.add(curToken);
        exp = parseExp();
        subUnits.add(exp);
        ForStmt forStmt = new ForStmt("ForStmt", subUnits, lVal, exp);
        forStmt.check(symbolTable);
        return forStmt;
    }

    public Exp parseExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        AddExp addExp = null;

        addExp = parseAddExp();
        subUnits.add(addExp);
        return new Exp("Exp", subUnits, addExp);
    }

    public Cond parseCond() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        LOrExp lOrExp = null;

        lOrExp = parseLOrExp();
        subUnits.add(lOrExp);
        return new Cond("Cond", subUnits, lOrExp);

    }

    public LVal parseLVal() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        Token ident = null;
        Exp exp = null;

        getToken(Word.IDENFR);
        subUnits.add(curToken);
        ident = curToken;
        if (getToken(Word.LBRACK)) {
            subUnits.add(curToken);
            exp = parseExp();
            subUnits.add(exp);
            if (getToken(Word.RBRACK)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('k', curToken.getLineNum()));
            }
        }
        LVal lVal = new LVal("LVal", subUnits, ident, exp, symbolTable);
        lVal.checkC(symbolTable);
        return lVal;
    }

    public PrimaryExp parsePrimaryExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        Exp exp = null;
        LVal lVal = null;
        Number number = null;
        Character character = null;

        if (getToken(Word.LPARENT)) {
            subUnits.add(curToken);
            exp = parseExp();
            subUnits.add(exp);
            if (getToken(Word.RPARENT)) {
                subUnits.add(curToken);
            } else {
                ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
            }

        } else if (getToken(Word.IDENFR)) {
            retract(1);
            lVal = parseLVal();
            subUnits.add(lVal);
        } else if (getToken(Word.INTCON)) {
            retract(1);
            number = parseNumber();
            subUnits.add(number);
        } else {
            character = parseCharacter();
            subUnits.add(character);
        }
        return new PrimaryExp("PrimaryExp", subUnits, exp, lVal, number, character);
    }

    public Number parseNumber() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        getToken(Word.INTCON);
        subUnits.add(curToken);
        Token intConst = curToken;
        return new Number("Number", subUnits, intConst);
    }

    public Character parseCharacter() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        getToken(Word.CHRCON);
        subUnits.add(curToken);
        Token charConst = curToken;
        return new Character("Character", subUnits, charConst);
    }

    public UnaryExp parseUnaryExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        PrimaryExp primaryExp = null;
        Token ident = null;
        FuncRParams funcRParams = null;
        UnaryOp unaryOp = null;
        UnaryExp unaryExp = null;
        //boolean isFromIdent=false;


        if (getToken(Word.IDENFR)) {

            if (getToken(Word.LPARENT)) {
                //isFromIdent=true;
                retract(1);
                subUnits.add(curToken);
                ident = curToken;
                getToken(Word.LPARENT);
                subUnits.add(curToken);
                if (getToken(Word.LPARENT, Word.IDENFR, Word.PLUS, Word.MINU, Word.NOT, Word.INTCON, Word.CHRCON)) { //FIRST(Exp) FuncRParams
                    retract(1);
                    funcRParams = parseFuncRParams();
                    subUnits.add(funcRParams);

                }
                if (getToken(Word.RPARENT)) {
                    subUnits.add(curToken);
                } else {
                    ErrorHandler.getInstance().addError(new Error('j', curToken.getLineNum()));
                }
            } else {
                retract(1);
                primaryExp = parsePrimaryExp();
                subUnits.add(primaryExp);
            }
        } else if (getToken(Word.LPARENT, Word.INTCON, Word.CHRCON)) {
            retract(1);
            primaryExp = parsePrimaryExp();
            subUnits.add(primaryExp);
        } else {
            if (getToken(Word.PLUS, Word.MINU, Word.NOT)) {
                retract(1);
                unaryOp = parseUnaryOp();
                subUnits.add(unaryOp);
            }
//            getToken(Word.PLUS,Word.MINU,Word.NOT);
//            subUnits.add(curToken);
            unaryExp = parseUnaryExp();
            subUnits.add(unaryExp);
        }
        UnaryExp unaryExp1 = new UnaryExp("UnaryExp", subUnits, primaryExp, ident, funcRParams, unaryOp, unaryExp, symbolTable);
        unaryExp1.checkC(symbolTable);
        unaryExp1.checkDE(symbolTable);
        return unaryExp1;
    }

    public UnaryOp parseUnaryOp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        getToken(Word.PLUS, Word.MINU, Word.NOT);
        subUnits.add(curToken);
        Token op = curToken;
        return new UnaryOp("UnaryOp", subUnits, op);
    }

    public FuncRParams parseFuncRParams() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<Exp> rArgs = new ArrayList<>();

        Exp rArg = parseExp();
        subUnits.add(rArg);
        rArgs.add(rArg);
        //checkParam(funcName,tmp,paraIndex,lineNum);
        while (getToken(Word.COMMA)) {
            subUnits.add(curToken);
            rArg = parseExp();
            subUnits.add(rArg);
            rArgs.add(rArg);
            //checkParam(funcName,tmp1,paraIndex,lineNum);
        }
        return new FuncRParams("FuncRParams", subUnits, rArgs);
    }

    public MulExp parseMulExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        ArrayList<Word> ops=new ArrayList<>();

        UnaryExp unaryExp = parseUnaryExp();
        subUnits.add(unaryExp);
        unaryExps.add(unaryExp);
        while (getToken(Word.MULT, Word.DIV, Word.MOD)) {
            ops.add(curToken.getWord());
            ArrayList<ParseUnit> newUnits = new ArrayList<>(subUnits);
            ParseUnit parseUnit = new ParseUnit("MulExp", newUnits);

            subUnits.clear();
            subUnits.add(parseUnit);
            subUnits.add(curToken);
            unaryExp = parseUnaryExp();
            subUnits.add(unaryExp);
            unaryExps.add(unaryExp);
        }
        return new MulExp("MulExp", subUnits, unaryExps,ops);
    }

    public AddExp parseAddExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<MulExp> mulExps = new ArrayList<>();
        ArrayList<Word> ops=new ArrayList<>();

        MulExp mulExp = parseMulExp();
        subUnits.add(mulExp);
        mulExps.add(mulExp);
        while (getToken(Word.PLUS, Word.MINU)) {
            ops.add(curToken.getWord());
            ArrayList<ParseUnit> newUnits = new ArrayList<>(subUnits);
            ParseUnit parseUnit = new ParseUnit("AddExp", newUnits);

            subUnits.clear();
            subUnits.add(parseUnit);
            subUnits.add(curToken);
            mulExp = parseMulExp();
            subUnits.add(mulExp);
            mulExps.add(mulExp);
        }
        return new AddExp("AddExp", subUnits, mulExps,ops);
    }

    public RelExp parseRelExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<AddExp> addExps = new ArrayList<>();
        ArrayList<Word> ops=new ArrayList<>();

        AddExp addExp = parseAddExp();
        subUnits.add(addExp);
        addExps.add(addExp);
        while (getToken(Word.LSS, Word.GRE, Word.LEQ, Word.GEQ)) {
            ops.add(curToken.getWord());
            ArrayList<ParseUnit> newUnits = new ArrayList<>(subUnits);
            ParseUnit parseUnit = new ParseUnit("RelExp", newUnits);

            subUnits.clear();
            subUnits.add(parseUnit);
            subUnits.add(curToken);
            addExp = parseAddExp();
            subUnits.add(addExp);
            addExps.add(addExp);
        }
        return new RelExp("RelExp", subUnits, addExps,ops);
    }

    public EqExp parseEqExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<RelExp> relExps = new ArrayList<>();
        ArrayList<Word> ops=new ArrayList<>();

        RelExp relExp = parseRelExp();
        subUnits.add(relExp);
        relExps.add(relExp);
        while (getToken(Word.EQL, Word.NEQ)) {
            ops.add(curToken.getWord());
            ArrayList<ParseUnit> newUnits = new ArrayList<>(subUnits);
            ParseUnit parseUnit = new ParseUnit("EqExp", newUnits);

            subUnits.clear();
            subUnits.add(parseUnit);
            subUnits.add(curToken);
            relExp = parseRelExp();
            subUnits.add(relExp);
            relExps.add(relExp);
        }
        return new EqExp("EqExp", subUnits, relExps,ops);
    }

    public LAndExp parseLAndExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<EqExp> eqExps = new ArrayList<>();

        EqExp eqExp = parseEqExp();
        subUnits.add(eqExp);
        eqExps.add(eqExp);
        while (getToken(Word.AND)) {
            ArrayList<ParseUnit> newUnits = new ArrayList<>(subUnits);
            ParseUnit parseUnit = new ParseUnit("LAndExp", newUnits);

            subUnits.clear();
            subUnits.add(parseUnit);
            subUnits.add(curToken);
            eqExp = parseEqExp();
            subUnits.add(eqExp);
            eqExps.add(eqExp);
        }
        return new LAndExp("LAndExp", subUnits, eqExps);
    }

    public LOrExp parseLOrExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        ArrayList<LAndExp> lAndExps = new ArrayList<>();


        LAndExp lAndExp = parseLAndExp();
        subUnits.add(lAndExp);
        lAndExps.add(lAndExp);
        while (getToken(Word.OR)) {
            ArrayList<ParseUnit> newUnits = new ArrayList<>(subUnits);
            ParseUnit parseUnit = new ParseUnit("LOrExp", newUnits);

            subUnits.clear();
            subUnits.add(parseUnit);
            subUnits.add(curToken);
            lAndExp = parseLAndExp();
            subUnits.add(lAndExp);
            lAndExps.add(lAndExp);
        }
        return new LOrExp("LOrExp", subUnits, lAndExps);
    }

    public ConstExp parseConstExp() {
        ArrayList<ParseUnit> subUnits = new ArrayList<>();
        AddExp addExp = parseAddExp();
        subUnits.add(addExp);
        return new ConstExp("ConstExp", subUnits, addExp);
    }
//    //public HashMap<Integer,SymbolTable> getSymbols(){
//        return symbols;
//    }
//    public HashMap<Integer,SymbolTable> getResultSymbols(){
//        return resultSymbols;
//    }
//    public ArrayList<Error> getErrors(){
//        return errors;
//    }
}
