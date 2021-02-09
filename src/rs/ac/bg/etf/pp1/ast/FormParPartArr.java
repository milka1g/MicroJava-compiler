// generated with ast extension for cup
// version 0.8
// 9/1/2021 22:58:56


package rs.ac.bg.etf.pp1.ast;

public class FormParPartArr extends FormParPart {

    private Type Type;
    private String formParArr;

    public FormParPartArr (Type Type, String formParArr) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.formParArr=formParArr;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public String getFormParArr() {
        return formParArr;
    }

    public void setFormParArr(String formParArr) {
        this.formParArr=formParArr;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParPartArr(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+formParArr);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParPartArr]");
        return buffer.toString();
    }
}
