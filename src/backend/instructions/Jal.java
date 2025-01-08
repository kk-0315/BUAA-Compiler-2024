package backend.instructions;

public class Jal extends MipsInstruction{
    private String destLabel;
    public Jal(String destLabel) {
        super("jal");
        this.destLabel=destLabel;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('\t');
        sb.append(getName()).append(' ').append(destLabel).append('\n');
        return sb.toString();
    }
}
