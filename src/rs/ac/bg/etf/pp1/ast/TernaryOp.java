// generated with ast extension for cup
// version 0.8
// 9/1/2021 22:58:56


package rs.ac.bg.etf.pp1.ast;

public class TernaryOp extends Expr {

    private TernaryCond TernaryCond;
    private TernaryExpr1 TernaryExpr1;
    private TernaryExpr2 TernaryExpr2;

    public TernaryOp (TernaryCond TernaryCond, TernaryExpr1 TernaryExpr1, TernaryExpr2 TernaryExpr2) {
        this.TernaryCond=TernaryCond;
        if(TernaryCond!=null) TernaryCond.setParent(this);
        this.TernaryExpr1=TernaryExpr1;
        if(TernaryExpr1!=null) TernaryExpr1.setParent(this);
        this.TernaryExpr2=TernaryExpr2;
        if(TernaryExpr2!=null) TernaryExpr2.setParent(this);
    }

    public TernaryCond getTernaryCond() {
        return TernaryCond;
    }

    public void setTernaryCond(TernaryCond TernaryCond) {
        this.TernaryCond=TernaryCond;
    }

    public TernaryExpr1 getTernaryExpr1() {
        return TernaryExpr1;
    }

    public void setTernaryExpr1(TernaryExpr1 TernaryExpr1) {
        this.TernaryExpr1=TernaryExpr1;
    }

    public TernaryExpr2 getTernaryExpr2() {
        return TernaryExpr2;
    }

    public void setTernaryExpr2(TernaryExpr2 TernaryExpr2) {
        this.TernaryExpr2=TernaryExpr2;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(TernaryCond!=null) TernaryCond.accept(visitor);
        if(TernaryExpr1!=null) TernaryExpr1.accept(visitor);
        if(TernaryExpr2!=null) TernaryExpr2.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(TernaryCond!=null) TernaryCond.traverseTopDown(visitor);
        if(TernaryExpr1!=null) TernaryExpr1.traverseTopDown(visitor);
        if(TernaryExpr2!=null) TernaryExpr2.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(TernaryCond!=null) TernaryCond.traverseBottomUp(visitor);
        if(TernaryExpr1!=null) TernaryExpr1.traverseBottomUp(visitor);
        if(TernaryExpr2!=null) TernaryExpr2.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("TernaryOp(\n");

        if(TernaryCond!=null)
            buffer.append(TernaryCond.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryExpr1!=null)
            buffer.append(TernaryExpr1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(TernaryExpr2!=null)
            buffer.append(TernaryExpr2.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [TernaryOp]");
        return buffer.toString();
    }
}
