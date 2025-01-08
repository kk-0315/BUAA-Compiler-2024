package backend.instructions;

public class Syscall extends MipsInstruction{
    public Syscall() {
        super("syscall");
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append('\n');
        return sb.toString();
    }
}
