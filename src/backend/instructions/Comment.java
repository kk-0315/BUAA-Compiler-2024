package backend.instructions;

public class Comment extends MipsInstruction{

    public Comment(String context) {
        super(context);
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append('#').append("----------------").append(getName()).append("---------------------").append('\n');
        return sb.toString();
    }

}
