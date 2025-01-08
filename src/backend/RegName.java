package backend;

import java.util.ArrayList;
import java.util.List;

public class RegName {
    // 私有的静态实例，确保只有一个实例
    private static RegName instance;

    // 寄存器名称列表
    private static ArrayList<String> registers = new ArrayList<>(List.of(
            "$zero", "$at", "$v0", "$v1",    // 0 - 3
            "$a0", "$a1", "$a2", "$a3",      // 4 - 7
            "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",  // 8 - 15
            "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",  // 16 - 23
            "$t8", "$t9",                     // 24 - 25
            "$k0", "$k1",                     // 26 - 27
            "$gp", "$sp", "$fp", "$ra"        // 28 - 31
    ));

    // 私有构造方法，防止外部实例化
    private RegName() {}

    // 公共静态方法获取唯一实例
    public static RegName getInstance() {
        if (instance == null) {
            instance = new RegName();
        }
        return instance;
    }

    // 根据寄存器编号获取寄存器名称
    public String getName(int i) {
        return registers.get(i);
    }
}
