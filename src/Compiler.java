
import backend.MipsGenerator;
import backend.MipsModule;
import frontend.ErrorHandler.ErrorHandler;
import frontend.ErrorHandler.Error;
import frontend.lexer.LexicalAnalyzer;
import frontend.lexer.Preprocess;
import frontend.lexer.Token;
import frontend.parser.ParseUnit;
import frontend.parser.Parser;
import frontend.parser.specificUnit.Comp;
import frontend.symbols.Symbol;
import frontend.symbols.SymbolTable;
import midend.IrBuilder;
import midend.IrModule;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String path = "testfile.txt"; // 指定文件路径
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n"); // 读取每一行并追加换行符
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        Preprocess preprocess = new Preprocess(contentBuilder);
        StringBuilder afterPreProcess = preprocess.process();
        // 将读取的内容转换为String
        String content = afterPreProcess.toString();
        //System.out.println(content);
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(content);
        lexicalAnalyzer.parse();

        ArrayList<Token> tokens = lexicalAnalyzer.getTokens();
        Parser parser = new Parser(tokens);

//        System.out.print(parser.getSymbolTable().toString());
//        ErrorHandler.getInstance().sortErrorsByLine();
//        ErrorHandler.getInstance().printErrors();
//        System.out.println(parser.parseCompUnit().toString());
//        parser.parseCompUnit();
//        ErrorHandler.getInstance().sortErrorsByLine();
//        ErrorHandler.getInstance().printErrors();
        //System.out.println(parser.getErrors().toString());
        try {
            Comp comp=parser.parseCompUnit();
            SymbolTable symbolTable = parser.getSymbolTable();
            if (!ErrorHandler.getInstance().getErrors().isEmpty()) {
                ErrorHandler.getInstance().sortErrorsByLine();
                FileWriter fileWriter = new FileWriter("error.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                for (Error error : ErrorHandler.getInstance().getErrors()) {
                    bufferedWriter.write(error.toString());
                    bufferedWriter.newLine(); // 写入换行符
                }
                bufferedWriter.close();
            } else {
                //写词法分析结果
                FileWriter fileWriter_lexer = new FileWriter("lexer.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter_lexer = new BufferedWriter(fileWriter_lexer);
                for (Token token :lexicalAnalyzer.getTokens()){
                    bufferedWriter_lexer.write(token.toString());
                    bufferedWriter_lexer.newLine(); // 写入换行符
                }
                bufferedWriter_lexer.close();

                //写语法分析结果
                FileWriter fileWriter_parser = new FileWriter("parser.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter_parser = new BufferedWriter(fileWriter_parser);
                bufferedWriter_parser.write(comp.toString());
                bufferedWriter_parser.close();

                IrBuilder irBuilder=new IrBuilder(comp);
                IrModule irModule=irBuilder.generateIrModule();
                FileWriter fileWriter3 = new FileWriter("llvm_ir_ori.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter3 = new BufferedWriter(fileWriter3);
                bufferedWriter3.write(irModule.toString());
                bufferedWriter3.close();
                MipsGenerator mipsGenerator1=new MipsGenerator(irModule);
                MipsModule mipsModule1=mipsGenerator1.generateMipsModule();

                FileWriter fileWriter4 = new FileWriter("mips_ori.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter4 = new BufferedWriter(fileWriter4);
                bufferedWriter4.write(mipsModule1.toString());
                bufferedWriter4.close();

                //Optimizer.getInstance().run(irModule);
                FileWriter fileWriter1 = new FileWriter("llvm_ir.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter1 = new BufferedWriter(fileWriter1);
                bufferedWriter1.write(irModule.toString());
                bufferedWriter1.close();

                MipsGenerator mipsGenerator=new MipsGenerator(irModule);
                MipsModule mipsModule=mipsGenerator.generateMipsModule();

                FileWriter fileWriter = new FileWriter("symbol.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(symbolTable.toString());
                bufferedWriter.close();

                FileWriter fileWriter2 = new FileWriter("mips.txt", false); // true 表示追加写入
                BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
                bufferedWriter2.write(mipsModule.toString());
                bufferedWriter2.close();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 使用LexicalAnalyzer处理读取的内容
//        LexicalAnalyzer analyzer = new LexicalAnalyzer(content);
//        try {
//            ArrayList<HashMap<Word, String>> result = analyzer.parse();
//            // 输出或处理结果
//        } catch (Exception e) {
//            System.err.println("Error during lexical analysis: " + e.getMessage());
//        }
    }
}
