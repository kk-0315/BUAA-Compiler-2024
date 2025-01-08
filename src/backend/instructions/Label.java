package backend.instructions;

public class Label extends MipsInstruction{
    private String label;
    public Label(String label) {
        super("label");
        this.label=label;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append(label).append(':').append('\n');
        return sb.toString();
    }
}
