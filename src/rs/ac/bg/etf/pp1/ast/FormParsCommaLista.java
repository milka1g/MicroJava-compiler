// generated with ast extension for cup
// version 0.8
// 9/1/2021 22:58:56


package rs.ac.bg.etf.pp1.ast;

public class FormParsCommaLista extends FormParsCommaList {

    private FormParsCommaList FormParsCommaList;
    private FormParPart FormParPart;

    public FormParsCommaLista (FormParsCommaList FormParsCommaList, FormParPart FormParPart) {
        this.FormParsCommaList=FormParsCommaList;
        if(FormParsCommaList!=null) FormParsCommaList.setParent(this);
        this.FormParPart=FormParPart;
        if(FormParPart!=null) FormParPart.setParent(this);
    }

    public FormParsCommaList getFormParsCommaList() {
        return FormParsCommaList;
    }

    public void setFormParsCommaList(FormParsCommaList FormParsCommaList) {
        this.FormParsCommaList=FormParsCommaList;
    }

    public FormParPart getFormParPart() {
        return FormParPart;
    }

    public void setFormParPart(FormParPart FormParPart) {
        this.FormParPart=FormParPart;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FormParsCommaList!=null) FormParsCommaList.accept(visitor);
        if(FormParPart!=null) FormParPart.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FormParsCommaList!=null) FormParsCommaList.traverseTopDown(visitor);
        if(FormParPart!=null) FormParPart.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FormParsCommaList!=null) FormParsCommaList.traverseBottomUp(visitor);
        if(FormParPart!=null) FormParPart.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FormParsCommaLista(\n");

        if(FormParsCommaList!=null)
            buffer.append(FormParsCommaList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormParPart!=null)
            buffer.append(FormParPart.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FormParsCommaLista]");
        return buffer.toString();
    }
}
