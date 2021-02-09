// generated with ast extension for cup
// version 0.8
// 9/1/2021 22:58:56


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclaration extends ConstDecl {

    private Type Type;
    private ConstDeclPart ConstDeclPart;
    private ConstTypeList ConstTypeList;

    public ConstDeclaration (Type Type, ConstDeclPart ConstDeclPart, ConstTypeList ConstTypeList) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.ConstDeclPart=ConstDeclPart;
        if(ConstDeclPart!=null) ConstDeclPart.setParent(this);
        this.ConstTypeList=ConstTypeList;
        if(ConstTypeList!=null) ConstTypeList.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public ConstDeclPart getConstDeclPart() {
        return ConstDeclPart;
    }

    public void setConstDeclPart(ConstDeclPart ConstDeclPart) {
        this.ConstDeclPart=ConstDeclPart;
    }

    public ConstTypeList getConstTypeList() {
        return ConstTypeList;
    }

    public void setConstTypeList(ConstTypeList ConstTypeList) {
        this.ConstTypeList=ConstTypeList;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
        if(ConstDeclPart!=null) ConstDeclPart.accept(visitor);
        if(ConstTypeList!=null) ConstTypeList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(ConstDeclPart!=null) ConstDeclPart.traverseTopDown(visitor);
        if(ConstTypeList!=null) ConstTypeList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(ConstDeclPart!=null) ConstDeclPart.traverseBottomUp(visitor);
        if(ConstTypeList!=null) ConstTypeList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclaration(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstDeclPart!=null)
            buffer.append(ConstDeclPart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstTypeList!=null)
            buffer.append(ConstTypeList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclaration]");
        return buffer.toString();
    }
}
