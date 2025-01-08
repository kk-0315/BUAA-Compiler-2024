package midend.value.instructions;

public enum IrInstructionType {
    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    Lt, // <
    Le, // <=
    Ge, // >=
    Gt, // >
    Eq, // ==
    Ne, // !=
    Not, // !
    Ret,
    Load,
    Alloc,
    Store,
    Call,
    Label,
    Goto,
    Beq,
    Bne,
    Phi,
    PCopy,
    Move,
    Sll,
    Sra,
}
