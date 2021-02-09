// generated with ast extension for cup
// version 0.8
// 9/1/2021 22:58:56


package rs.ac.bg.etf.pp1.ast;

public class FormParPartStd extends FormParPart {

    private Type Type;
    private String formParStd;

    public FormParPartStd (Type Type, String formParStd) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.formParStd=formParStd;
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public String getFormParStd() {
        return formParStd;
    }

    public void setFormParStd(String formParStd) {
        this.formParStd=formParStd;
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
        buffer.append("FormParPartStd(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+formParStd);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParPartStd]");
        return buffer.toString();
    }
}
