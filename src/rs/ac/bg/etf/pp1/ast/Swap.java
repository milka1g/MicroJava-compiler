// generated with ast extension for cup
// version 0.8
// 9/1/2021 22:58:56


package rs.ac.bg.etf.pp1.ast;

public class Swap extends Statement {

    private Designator Designator;
    private Integer val1;
    private Integer val2;

    public Swap (Designator Designator, Integer val1, Integer val2) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.val1=val1;
        this.val2=val2;
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public Integer getVal1() {
        return val1;
    }

    public void setVal1(Integer val1) {
        this.val1=val1;
    }

    public Integer getVal2() {
        return val2;
    }

    public void setVal2(Integer val2) {
        this.val2=val2;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Swap(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+val1);
        buffer.append("\n");

        buffer.append(" "+tab+val2);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Swap]");
        return buffer.toString();
    }
}
