import Optimize.*;
import midend.IrModule;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Optimizer {
    private static final Optimizer optimizer = new Optimizer();

    public static Optimizer getInstance() {
        return optimizer;
    }
    public void run(IrModule irModule) throws IOException {

        new SimplifyBB(irModule).run();
        new CFGBuilder(irModule).run();
        new Mem2Reg(irModule).run();
        new GVN(irModule).run();
        new ActiveVarAnalyze(irModule).run();
        new RegAllocator(irModule).run();
        new DeadCodeKiller(irModule).run();
        FileWriter fileWriter1 = new FileWriter("llvm_ir_phi.txt", false); // true 表示追加写入
        BufferedWriter bufferedWriter1 = new BufferedWriter(fileWriter1);
        bufferedWriter1.write(irModule.toString());
        bufferedWriter1.close();
        new EliminatePhi(irModule).run();
        new Other(irModule).run();
    }
}
