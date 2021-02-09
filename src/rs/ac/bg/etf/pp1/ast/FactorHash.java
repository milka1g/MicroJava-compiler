// generated with ast extension for cup
// version 0.8
// 9/1/2021 22:58:56


package rs.ac.bg.etf.pp1.ast;

public class FactorHash extends Factor {

    private String desigName;
    private Integer index;

    public FactorHash (String desigName, Integer index) {
        this.desigName=desigName;
        this.index=index;
    }

    public String getDesigName() {
        return desigName;
    }

    public void setDesigName(String desigName) {
        this.desigName=desigName;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index=index;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorHash(\n");

        buffer.append(" "+tab+desigName);
        buffer.append("\n");

        buffer.append(" "+tab+index);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorHash]");
        return buffer.toString();
    }
}
