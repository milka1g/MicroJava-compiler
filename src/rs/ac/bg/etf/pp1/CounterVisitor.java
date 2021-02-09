package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.FormParPartArr;
import rs.ac.bg.etf.pp1.ast.FormParPartStd;
import rs.ac.bg.etf.pp1.ast.VarDeclPartArr;
import rs.ac.bg.etf.pp1.ast.VarDeclPartStd;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;

public class CounterVisitor extends VisitorAdaptor {
	
	protected int count = 0;
	
	public int getCount(){
		return count;
	}
	
	public static class FormParamCounter extends CounterVisitor{
	/////////PREBROJAVANJE FORMALNIH PARAMETARA FJE//////////////
		public void visit(FormParPartStd p){
			count++;
		}
		public void visit(FormParPartArr p){
			count++;
		}
	}
	
	public static class VarCounter extends CounterVisitor{
	//////////PREBROJAVANJE DEKLARACIJE LOKALNIH PROMENLJIVIH/////////
		public void visit(VarDeclPartArr varDecl){
			count++;
		}
		public void visit(VarDeclPartStd varDecl){
			count++;
		}
	}
}
