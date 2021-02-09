package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.CounterVisitor.*;
import rs.ac.bg.etf.pp1.SemanticAnalyzer.Decl;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;
	private boolean errorDetected = false;

	enum Addop {
		ADD, SUB
	};

	enum Mulop {
		MUL, DIV, REM
	};

	private Stack<Addop> addopStack = new Stack<Addop>();
	private Stack<Mulop> mulopStack = new Stack<Mulop>();
	
	private ArrayList<Obj> definedLabels = new ArrayList<Obj>();
	private ArrayList<Obj> gotoLabels = new ArrayList<Obj>();
	
	private int krpa1 = 0, krpa2 = 0, fin1 = 0, fin2 = 0;
	Logger log = Logger.getLogger("info");
	Logger loge = Logger.getLogger("error");

	private Obj program;
	// posto je ovde Tabela simbola sa scopeom na universe-u, moramo da
	// prosledimo Obj koji pokazuje na program da bismo mogli da iskopamo
	// sve promenljive koje nam trebaju
	private String currMethod = "";
	private Obj currentMethod = null;

	public int getMainPc() {
		return mainPc;
	}

	public boolean getErrorDetected() {
		return errorDetected;
	}

	public CodeGenerator(Obj prog) {
		program = prog;
	}

	/////////////////////// REPORT I ERROR LOG/////////////////////////
	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		loge.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

	//////////////////////////////////////////////////////////////////
	///// METODA ZA TRAZENJE SVIH PROMENLJIVIH UNUTAR SMENE PROGRAM///
	public Obj getVar(String name, boolean isMethod) {
		Obj obj = Tab.find(name); // ako smo trazili nesto iz unverse-a, tipa ord/char/len onda ce naci
		// inace treba da idemo na locals od smene program i tu da kopamo
		if (obj == Tab.noObj) {
			Collection<Obj> localsProgram = program.getLocalSymbols();
			Iterator<Obj> iter = localsProgram.iterator();
			while (iter.hasNext()) {
				obj = iter.next();
				if (isMethod == true && obj.getKind() == Obj.Meth && obj.getName().equals(name)) {
					return obj;
				} else if (isMethod == false && obj.getKind() != Obj.Meth && obj.getName().equals(name)) {
					return obj;
				}

			} // ako si stigao dovde onda nisi nasao medj locals od programa
				// onda je sigurno neki od locals od metode koja je trenutna

			Collection<Obj> localsMethod = currentMethod.getLocalSymbols();
			iter = localsMethod.iterator();
			while (iter.hasNext()) {
				obj = iter.next();
				if (obj.getName().equals(name))
					return obj;
			}
			// ako si stigao dovde onda definitivno nema tog simbola
			obj = null;

		} else {
			return obj;
		}
		return null;
	}

	////////////////////////////////////////////////////////
	////////////////////// PRINT////////////////////////////
	public void visit(PrintNoNum p) {
		if (p.getExpr().struct == Tab.intType || p.getExpr().struct == SemanticAnalyzer.boolType) {
			Code.loadConst(5); // promeni ako oces po defaultu da ispisuje na nekoj sirini
			Code.put(Code.print);
		} else {
			Code.loadConst(1);
			Code.put(Code.bprint);
		}
	}

	public void visit(PrintNum p) {
		if (p.getExpr().struct == Tab.intType || p.getExpr().struct == SemanticAnalyzer.boolType) {
			Code.loadConst(p.getN2());
			Code.put(Code.print);
		} else {
			Code.loadConst(p.getN2());
			Code.put(Code.bprint);
		}
	}

	public void visit(Read p) {
		if (p.getDesignator().obj.getType() == Tab.charType)
			Code.put(Code.bread);
		else
			Code.put(Code.read);
		Code.store(p.getDesignator().obj);
	}

	///////////////////////////////////////////////////////
	/////////////////// POJAVLJIVANJE KONSTANTI U EXPR//////////////////////
	public void visit(FactorNumconst p) {
		Code.loadConst(p.getN1());
	}

	public void visit(FactorCharconst p) {
		Code.loadConst(p.getC1());
	}

	public void visit(FactorBoolconst p) {
		Code.loadConst(p.getB1() ? 1 : 0);
	}

	///////////////////////////////////////////////////////
	////////////////// KONSTANTE SE DEKLARISU///////////////
	public void visit(NumConst p) {
		// sve sredjeno u semantickoj, vrednost u addr polje itd
	}

	public void visit(BoolConst p) {
		// sve sredjeno u semantickoj, vrednost u addr polje itd
	}

	public void visit(CharConst p) {
		// sve sredjeno u semantickoj, vrednost u addr polje itd
	}

	///////////////////////////////////////////////////////
	///////// METODA SET PC,FORM PARS, LOCAL VARS, ENTER///////////
	public void visit(MethodTypeName p) {
		p.obj.setAdr(Code.pc);
		SyntaxNode fja = p.getParent();

		currMethod = p.getMetName();
		currentMethod = getVar(currMethod, true);

		VarCounter vc = new VarCounter();
		fja.traverseTopDown(vc);

		FormParamCounter fc = new FormParamCounter();
		fja.traverseTopDown(fc);

		Code.put(Code.enter);
		Code.put(fc.getCount());
		Code.put(fc.getCount() + vc.getCount());

	}

	public void visit(MethodVoidName p) {
		if (p.getMetName().equals("main")) {
			mainPc = Code.pc;
		}
		p.obj.setAdr(Code.pc);

		currMethod = p.getMetName();
		currentMethod = getVar(currMethod, true);

		SyntaxNode fja = p.getParent();

		VarCounter vc = new VarCounter();
		fja.traverseTopDown(vc);

		FormParamCounter fc = new FormParamCounter();
		fja.traverseTopDown(fc);

		Code.put(Code.enter);
		Code.put(fc.getCount());
		Code.put(fc.getCount() + vc.getCount());
	}

	public void visit(MethodDecl p) {
		Code.put(Code.exit);
		Code.put(Code.return_);
		currMethod = "";
		currentMethod = null;
	}
	/////////////////////////////////////////////////////////////////

	public void visit(Assignment p) {
		// Code.store(p.getDesignator().obj);
		Obj ret = getVar(p.getDesignator().obj.getName(), false);
		if (ret.getFpPos() == 1 && p.getDesignator().obj.getKind() == Obj.Elem) {
			// ako je na ind+arrlen==1 to znaci da je upisano vec i ne radis to opet
			// ovde imas vec addr,ind,val i samo store treba
			Code.put(Code.dup2); // addr, ind, val, ind, val
			Code.put(Code.pop); // addr, ind, val, ind
			Code.load(ret); //// addr, ind, val, ind, addr
			Code.put(Code.arraylength);// addr, ind, val, ind, len
			Code.loadConst(2);
			Code.put(Code.div);
			Code.put(Code.add);
			Code.load(ret);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.put(Code.aload);
			// broj pristupa je tu
			Code.loadConst(1);
			Code.putFalseJump(Code.eq, 0);
			fin1 = Code.pc - 2;
			Code.store(p.getDesignator().obj);
			Code.putJump(0);
			fin2 = Code.pc - 2;
			Code.fixup(fin1);
			Code.put(Code.pop);
			Code.put(Code.pop);
			Code.put(Code.pop);
			Code.fixup(fin2);

		} else {
			Code.store(p.getDesignator().obj);
		}

	}

	public void visit(FactorDesigOne p) {
		// Obj ret = getVar(p.getDesignator().obj.getName());
		SyntaxNode parent = p.getParent();

		Code.load(p.getDesignator().obj);

	}

	public void visit(DesignatorStd p) {
//		SyntaxNode parent = p.getParent();
//
//		if (parent.getClass() == FactorDesigOne.class) {
//			Code.load(p.obj);
//		}
		// Code.load(p.obj);
	}

	public void visit(DesignatorArr p) {
		// tu je Expr vec na steku, kao ceo broj

		// if (parent.getClass() == FactorDesigOne.class) {
		Obj ret = getVar(p.getDesigName(), false);
		if (ret == null)
			report_error("Nema ti niza baki", null);

		if (ret.getFpPos() == 1 || ret.getFpPos() == 0) {
			// kad god pristupamo nekom indeksu niza u arrlen+ind upisemo 1 da znamo da smo
			// pristupili ako je taj niz final
			Code.put(Code.dup); // da imamo dupli index
			Code.load(ret);
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
			Code.load(ret);
			Code.put(Code.arraylength);
			Code.loadConst(2);
			Code.put(Code.div);
			Code.put(Code.add);
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.put(Code.astore);
		}
		Code.load(ret);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		// posle ovoga imas adr, ind na steku ali moras da uvecas za 1 onu lokaciju na
		// niz[arrlen+ind];
	}

	///////////////// ARITMETICKE OP///////////////////
	public void visit(AddopTermLista p) {
		Addop op = addopStack.pop();
		if (op == Addop.ADD) {
			Code.put(Code.add);
		} else if (op == Addop.SUB) {
			Code.put(Code.sub);
		} else {
			report_error("Desila se greska pri aritmetickoj operaciji add/sub", null);
		}
	}

	public void visit(MulopFactorLista p) {
		Mulop op = mulopStack.pop();
		if (op == Mulop.MUL) {
			Code.put(Code.mul);
		} else if (op == Mulop.DIV) {
			Code.put(Code.div);
		} else if (op == Mulop.REM) {
			Code.put(Code.rem);
		} else {
			report_error("Desila se greska pri aritmetickoj operaciji mul/div/rem", null);
		}
	}

	public void visit(Addition p) {
		addopStack.push(Addop.ADD);
	}

	public void visit(Subtraction p) {
		addopStack.push(Addop.SUB);
	}

	public void visit(Multiplication p) {
		mulopStack.push(Mulop.MUL);
	}

	public void visit(Division p) {
		mulopStack.push(Mulop.DIV);
	}

	public void visit(Modulus p) {
		mulopStack.push(Mulop.REM);
	}

	////////////////////////////////////////////
	//////////// ALOCIRANJE NIZA/////////////////
	public void visit(FactorNewArr p) {
		// da zauzmemo 2* prostor da tu skladistimo nesto.. dal je final ili da brojimo
		// broj pristupa itd..
		Code.loadConst(2);
		Code.put(Code.mul);

		if (p.getType().struct == Tab.charType) {
			Code.put(Code.newarray);
			Code.put(0);
		} else if (p.getType().struct == Tab.intType || p.getType().struct == SemanticAnalyzer.boolType) {
			Code.put(Code.newarray);
			Code.put(1);
		} else {
			report_error("Nizovi tipa " + p.getType() + " nisu podrzani", null);
		}
	}

	////////////////////////////////////////////
	//////////////// INCR DECR/////////////////////
	public void visit(Incr p) {
		if (p.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(p.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(p.getDesignator().obj);
	}

	public void visit(Decr p) {
		if (p.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2);
		}
		Code.load(p.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(p.getDesignator().obj);
	}

	//////////////////////////////////////////////
	public void visit(Term p) {
		if (p.getParent().getClass() == Expression1Minus.class)
			Code.put(Code.neg);
	}

	///////////////////////////////////////////////
	////////////////// TERNARNI OPERATOR////////////////
	//// Ternary ::= TernaryCond ? TernaryExpr1 : TernaryExpr2
	// true ? x+2 : y+3
	public void visit(TernaryCond p) {
		Code.loadConst(0);
		Code.putFalseJump(Code.gt, 0);
		krpa1 = Code.pc - 2;
		// 0 ili 1 zavisi sta je expr1 bilo tj true vs false == 1 vs 0, prepravljeno da
		// moze i bilo koji int
		// pa se gleda ako je 0 onda je false, ako je >0 onda je true
		// const_1
		// jle
		// ?? <-krpa1 == pc-2
		// ??
		// pc ostale inst
		/// inst....
	}

	public void visit(TernaryExpr1 p) {
		Code.putJump(0);
		krpa2 = Code.pc - 2;
		Code.fixup(krpa1);
		// kod expr1
		// jmp
		// ?? <-krpa2 == pc-2
		// ??
		// pc
		// ostale inst od else
	}

	public void visit(TernaryExpr2 p) {
		Code.fixup(krpa2);
		/// inst od else popunjene
		// pc
	}

	////////////////////////////////////////////////////
	////////////// POZIVI FUNKCIJA ODR/CHR/LEN/////////

	public void visit(FactorFunctionCallPars p) {
		if (p.getDesignator().obj.getKind() != Obj.Meth)
			report_error("Semanticka greska - objekat nije funkcija!", null);
		if (p.getDesignator().obj.getName().equals("len")) {
			Code.put(Code.arraylength); // pre ovoga je vec obradjen designator kao arr
		} else {
			Obj ret = getVar(p.getDesignator().obj.getName(), true);
			// report_info("INLINE " + ret.getName() + "na adresi" + ret.getAdr()+
			// "!",null);
			if (ret.getName().equals("ord")) {
				// Code.loadConst(0);
				// Code.put(Code.add);
			} else if (ret.getName().equals("chr")) {
				// Code.loadConst(0);
				// Code.put(Code.sub);
			} else {
				report_info("NE VALJA TI NISTA CHR/LEN/ORD " + ret.getName() + "!", null);
			}

		}

	}
	///////////////////////////////////////////////////
	///////////////////////////////////////////////////
	public void visit(GotoLabelStmt p) {
		Obj ret = null;
		for(Obj o : definedLabels) {
			if(o.getName().equals(p.getLabel())) {
				ret = o;
			}
		}
		if(ret == null) {
			Code.putJump(0);
			Obj fix = new Obj(Obj.Con, p.getLabel(), Tab.noType, Code.pc-2, 0); //pc-2 nam je patchAddr
			gotoLabels.add(fix);
		} else {
			Code.putJump(ret.getAdr());
		}
	}

	public void visit(Label p) {
		Obj lab = new Obj(Obj.Con, p.getLabel(), Tab.noType, Code.pc, 0);
		definedLabels.add(lab);
		for(Obj o : gotoLabels) {
			if(o.getName().equals(p.getLabel()) && o.getLevel()==0) {
				Code.fixup(o.getAdr());
				o.setLevel(1); //da ako smo vec fixovali da ne fixujemo opet ako naidjemo na istu labelu
			}
		}
	}
	
	public void visit(Swap p) {
		Obj ret = getVar(p.getDesignator().obj.getName(), false);
		int ind1 = p.getVal1();
		int ind2 = p.getVal2();
		
		Code.load(ret);
		Code.loadConst(ind1);
		Code.put(Code.aload);
		//imas staru vrednost sa ind1, sad upisi sa ind2 na ind1
		Code.load(ret);
		Code.loadConst(ind1);
		Code.load(ret);
		Code.loadConst(ind2);
		Code.put(Code.aload);
		Code.put(Code.astore);
		//sad onu vrednost sa ind1 od gore stavi u ind2
		Code.load(ret);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.loadConst(ind2);
		Code.put(Code.dup_x1);
		Code.put(Code.pop);
		Code.put(Code.astore);
	}
	
	public void visit(FactorHash p) {
		Obj ret = getVar(p.getDesigName(),false);
		int ind = p.getIndex();
		Code.load(ret);
		Code.load(ret);
		Code.put(Code.arraylength);
		Code.loadConst(2);
		Code.put(Code.div);
		Code.loadConst(ind);
		Code.put(Code.add);
		Code.put(Code.aload);
	}
	
	public void visit(FactorAt p) { //niz@1 = niz[arrlen-1]+ niz[1]
		Obj ret = getVar(p.getDesignator().obj.getName(),false);
		int ind = p.getVal();
		Code.load(ret);
		Code.load(ret);
		Code.put(Code.arraylength);
		Code.loadConst(ind);
		Code.put(Code.sub);
		Code.put(Code.aload);
		Code.load(ret);
		Code.loadConst(ind);
		Code.put(Code.aload);
		Code.put(Code.add);
	}

}
