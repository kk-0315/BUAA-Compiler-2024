package backend.instructions;

public class Asciiz extends MipsInstruction{
    private String name;
    private String context;
    public Asciiz(String name,String context) {
        super(".asciiz");
        this.name=name;
        this.context=context;
    }
    @Override
    public String getName(){
        return name;
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        this.context = this.context.replaceAll("\n", "\\\\n");
        sb.append(name).append(": .asciiz \"").append(context).append("\"\n");
        return sb.toString();
    }
}
