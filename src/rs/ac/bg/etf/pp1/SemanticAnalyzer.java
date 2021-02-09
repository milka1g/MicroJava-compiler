package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

public class SemanticAnalyzer extends VisitorAdaptor {

	static final Struct boolType;
	static {
		boolType = Tab.insert(Obj.Type, "bool", new Struct(Struct.Bool)).getType();
	}

	class Decl {
		/*
		 * univerzalna klasa za promenljive i konstante
		 */
		String name;
		Object decl = null;
		Struct type = null;

		Integer vali = null;
		Character valc = null;
		Boolean valb = null;

		boolean isarray;

		public Decl(String n, boolean isa) {
			name = n;
			isarray = isa;
		}

		public Decl(String n) {
			name = n;
			isarray = false;
		}

		public Decl(String n, Struct t) {
			type = t;
			name = n;
		}

		public Decl(String n, Struct t, Integer i) {
			type = t;
			name = n;
			vali = i;
		}

		public Decl(String n, Struct t, Character c) {
			type = t;
			name = n;
			valc = c;
		}

		public Decl(String n, Struct t, Boolean b) {
			type = t;
			name = n;
			valb = b;
		}

		public String getName() {
			return name;
		}

	}

	private Integer currConstInt;
	private Character currConstChar;
	private Boolean currConstBool;

	Obj currentMethod = null;
	Scope programScope = null;// za potrebe da znam jel globalna ili lokalna prom pri konacnoj smeni za
								// vardecl
	int nVars;
	boolean errorDetected = false, returnDetected = false, isVoid = false, mainDetected = false;
	Struct currAddop = null, currMulop = null;
	private List<Decl> declList = new ArrayList<Decl>();
	private Obj program = null;
	private MyTableDumpVisitor tv = new MyTableDumpVisitor();

	private static Logger log = Logger.getLogger("info");
	private static Logger loge = Logger.getLogger("error");

	boolean getErrorDetected() {
		return errorDetected;
	}

	Obj getProgram() {
		return program;
	}

	////////////////////// LOG /////////////////////////
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

	//////////////////////////////////////////////////////
	/////////////////// POCETAK I KRAJ PROGRAMA///////////
	public void visit(ProgName progName) {
		program = progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
		report_info("Pocetak semanticke obrade programa " + progName.getProgName(), progName);
		Tab.openScope();
		programScope = Tab.currentScope();
	}

	// dodato da se proverava dal ima main
	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
		if (mainDetected == false) {
			report_error("Semanticka greska - mora postojati metoda main!", null);
		}
		report_info("Kraj semanticke obrade programa " + program.getProgName().getProgName(), null);
	}

	/////////////////////////////////////////////////////
	// kad se obradi cela smena za deklaraciju promenljivih
	public void visit(VarDeclaration p) {
		for (Decl d : declList) {
			String name = d.name;
			if (d.isarray == false) {
				Obj ret = Tab.insert(Obj.Var, d.name, p.getType().struct);
				tv.visitObjNode(Tab.currentScope.findSymbol(name));
				if (Tab.currentScope() == programScope)
					report_info("Deklarisana globalna promenljiva " + d.name + " : " + tv.getOutput(), p);
				else
					report_info("Deklarisana lokalna promenljiva " + d.name + " : " + tv.getOutput(), p);
				tv.clearOutput();
				ret.setFpPos(0);
			} else {
				Obj ret = Tab.insert(Obj.Var, d.name, new Struct(Struct.Array, p.getType().struct));
				tv.visitObjNode(Tab.currentScope.findSymbol(name));
				if (Tab.currentScope() == programScope)
					report_info("Deklarisan globalni niz " + d.name + " : " + tv.getOutput(), p);
				else
					report_info("Deklarisan lokalni niz " + d.name + " : " + tv.getOutput(), p);
				tv.clearOutput();
				ret.setFpPos(0);
			}
		}
		declList.clear();
	}
	
	public void visit(VarDeclarationFinal p) {
		for (Decl d : declList) {
			String name = d.name;
			if (d.isarray == false) {
				Obj ret = Tab.insert(Obj.Var, d.name, p.getType().struct);
				tv.visitObjNode(Tab.currentScope.findSymbol(name));
				if (Tab.currentScope() == programScope)
					report_info("Deklarisana globalna promenljiva " + d.name + " : " + tv.getOutput(), p);
				else
					report_info("Deklarisana lokalna promenljiva " + d.name + " : " + tv.getOutput(), p);
				tv.clearOutput();
				ret.setFpPos(1);
			} else {
				Obj ret = Tab.insert(Obj.Var, d.name, new Struct(Struct.Array, p.getType().struct));
				tv.visitObjNode(Tab.currentScope.findSymbol(name));
				if (Tab.currentScope() == programScope)
					report_info("Deklarisan globalni niz " + d.name + " : " + tv.getOutput(), p);
				else
					report_info("Deklarisan lokalni niz " + d.name + " : " + tv.getOutput(), p);
				tv.clearOutput();
				ret.setFpPos(1);
			}
		}
		declList.clear();
	}

	// niz //poboljsano da se proveri i currentScope i u listi declList
	public void visit(VarDeclPartArr p) {
		// Obj ret = Tab.find(p.getArrName());
		Obj ret = Tab.currentScope.findSymbol(p.getArrName()); // vraca null, a ono gore Tab.noType
		if (ret == null) {
			String pname = p.getArrName();
			for (Decl d : declList) {
				if (d.getName().equals(pname)) {
					report_error("Semanticka greska - ime promenljive " + p.getArrName() + " vec postoji", p);
				}
			}
			declList.add(new Decl(p.getArrName(), true));
		} else {
			report_error("Semanticka greska - ime niza " + p.getArrName() + " vec postoji", p);
		}
	}

	// obicna prom //poboljsano da se proveri i currentScope i u listi declList
	public void visit(VarDeclPartStd p) {
		// Obj ret = Tab.find(p.getVarName());
		Obj ret = Tab.currentScope.findSymbol(p.getVarName());
		if (ret == null) {
			String pname = p.getVarName();
			for (Decl d : declList) {
				if (d.getName().equals(pname)) {
					report_error("Semanticka greska - ime promenljive " + p.getVarName() + " vec postoji", p);
				}
			}
			declList.add(new Decl(p.getVarName(), false));
		} else {
			report_error("Semanticka greska - ime promenljive " + p.getVarName() + " vec postoji", p);
		}
	}

	public void visit(ConstDeclPart p) {
		Obj ret = Tab.find(p.getConstIdName()); // proveri mi na nivou cele tabele jel ima ta konstanta
		if (ret == Tab.noObj) {
			if (p.getConstType().struct == Tab.charType)
				declList.add(new Decl(p.getConstIdName(), p.getConstType().struct, currConstChar));
			else if (p.getConstType().struct == Tab.intType)
				declList.add(new Decl(p.getConstIdName(), p.getConstType().struct, currConstInt));
			else if (p.getConstType().struct == boolType)
				declList.add(new Decl(p.getConstIdName(), p.getConstType().struct, currConstBool));
			else
				report_info("Semanticka greska - tip konstante ne postoji", null);
		} else {
			report_error("Semanticka greska - ime konstante " + p.getConstIdName() + " vec postoji", p);
		}
	}

	public void visit(ConstDeclaration p) {
		for (Decl d : declList) {
			if (d.type == p.getType().struct) {
				Obj ret = Tab.insert(Obj.Con, d.name, p.getType().struct);
				tv.visitObjNode(ret); // da dobijemo lep ispis kao u njihovom primeru
				if (p.getType().struct == Tab.intType)
					ret.setAdr(d.vali);
				else if (p.getType().struct == Tab.charType)
					ret.setAdr(d.valc);
				else if (p.getType().struct == boolType)
					ret.setAdr(d.valb ? 1 : 0);
				else
					report_info("Tip konstante ne postoji [ConstDeclaration]", null);
				ret.setLevel(0); // globalne konstante
				report_info("Deklarisana konstanta " + d.name + " : " + tv.getOutput(), p);
				tv.clearOutput();
			} else {
				report_error("Semanticka greska - tip konstante i dodeljeno se ne podudaraju", p);
				break;
			}
		}
		declList.clear();
	}

	///////////////// CONST DECL VREDNOSTI////////////////
	public void visit(NumConst p) {
		p.struct = Tab.intType;
		currConstInt = p.getNumVal();
	}

	public void visit(BoolConst p) {
		p.struct = boolType;
		currConstBool = p.getBoolVal();
	}

	public void visit(CharConst p) {
		p.struct = Tab.charType;
		currConstChar = p.getCharVal();
	}

	///////////////////////////////////////////////////////
	/////////////////////// PROVERA TIPA////////////////////
	public void visit(Type type) {
		Obj typeNode = Tab.find(type.getTypeName());
		if (typeNode == Tab.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola! ", null);
			type.struct = Tab.noType;
		} else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} else {
				report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip!", type);
				type.struct = Tab.noType;
			}
		}
	}

	//////////////////////////////////////////////////////
	///////////// METODE ZAPOCINJU S OVIM/////////////////
	public void visit(MethodTypeName p) {
		currentMethod = Tab.insert(Obj.Meth, p.getMetName(), p.getType().struct);
		Tab.openScope();
		p.obj = currentMethod;
		isVoid = false;
		if (p.getMetName().equals("main"))
			report_error("Main metoda ne sme imati povratnu vrednost ", p);
		else
			report_info("Obrada metode " + p.getMetName() + " s povratnom vrednoscu", p);
	}

	public void visit(MethodVoidName p) {
		currentMethod = Tab.insert(Obj.Meth, p.getMetName(), Tab.noType);
		if (p.getMetName().equals("main"))
			mainDetected = true; // mora ima main da proveris u zavrsnoj smeni Program
		Tab.openScope();
		p.obj = currentMethod;
		returnDetected = true;
		isVoid = true;
		report_info("Obrada metode " + p.getMetName() + " bez povratne vrednosti", p);
	}

	////////////////////////////////////////////////
	////////// DODAVANJE PARAMETARA FJE/METODE////////////////////
	public void visit(FormParPartStd p) {
		if (currentMethod.getName().equals("main")) {
			report_error("Semanticka greska - main metoda ne sme imati parametre ", p);
		}
		Obj ret = Tab.find(p.getFormParStd());
		if (ret == Tab.noObj) {
			Obj ins = Tab.insert(Obj.Var, p.getFormParStd(), p.getType().struct);
			ins.setFpPos(currentMethod.getLevel());
			currentMethod.setLevel(currentMethod.getLevel() + 1);
			report_info("Obradjen obican parametar f-je " + currentMethod.getName(), p);
		} else {
			report_error("Semanticka greska - ime obicnog parametra " + p.getFormParStd() + " vec postoji", p);
		}
	}

	public void visit(FormParPartArr p) {
		if (currentMethod.getName().equals("main")) {
			report_error("Semanticka greska - main metoda ne sme imati parametre ", p);
		}
		Obj ret = Tab.find(p.getFormParArr());
		if (ret == Tab.noObj) {
			Obj ins = Tab.insert(Obj.Var, p.getFormParArr(), new Struct(Struct.Array, p.getType().struct));
			ins.setFpPos(currentMethod.getLevel());
			currentMethod.setLevel(currentMethod.getLevel() + 1);
			report_info("Obradjen niz parametar f-je " + currentMethod.getName(), p);
		} else {
			report_error("Semanticka greska - ime parametra tipa niz " + p.getFormParArr() + " vec postoji", p);
		}
	}

	//////////////////////////////////////////////
	/////////////// ZAVRSETAK FJE/METODE///////////
	public void visit(MethodDecl p) {
		if (returnDetected == false) {
			report_error("Semanticka greska - metoda (funkcija) " + currentMethod.getName() + " nema return iskaz",
					null);
		}
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		report_info("Zavrsetak obrade metode (funkcije) " + currentMethod.getName(), p);
		returnDetected = false;
		currentMethod = null;
	}

	//////////////////////////////////////////////////////
	////////////////// RETURNS/////////////////////////////
	public void visit(ReturnExpr p) {
		returnDetected = true;
		Struct curr = currentMethod.getType();
		if (!curr.compatibleWith(p.getExpr().struct)) {
			report_error("Greska na liniji " + p.getLine()
					+ " : tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
					+ currentMethod.getName(), null);
		}
	}

	public void visit(ReturnNoExpr p) {
		returnDetected = true;
		// da vidis dal ima return iskaz;
	}

	/////////////////////////////////////////////////////
	///////////////////////// FACTORS/////////////////////
	public void visit(FactorNumconst p) {
		p.struct = Tab.intType;
	}

	public void visit(FactorCharconst p) {
		p.struct = Tab.charType;
	}

	public void visit(FactorBoolconst p) {
		p.struct = boolType;
	}

	public void visit(FactorExpr p) {
		p.struct = p.getExpr().struct;
	}

	public void visit(FactorNewArr p) {
		if (p.getExpr().struct != Tab.intType) {
			report_error("Greska - izraz za new naredbu pri alociranju niza mora biti ceo broj", p);
			p.struct = new Struct(Struct.Array, p.getType().struct); // da bi dodela i kod greske prepoznala da to ne
																		// valja
		} else {
			p.struct = new Struct(Struct.Array, p.getType().struct);
		}
	}

	public void visit(FactorDesigOne p) {
		p.struct = p.getDesignator().obj.getType();
	}

	public void visit(FactorNew p) {
		// ne koristi se za nivo A
	}

	/////////////////////////////////////////////////////
	/////////// PROPAGIRANJE FACTOR-TERM-EXPR1-EXPR///////
	public void visit(Term p) {
		p.struct = p.getFactor().struct;
		// currMulop = p.struct; //da vidis posle kroz muloplistu dok ides dal su isti
		// tipovi kao ovaj
	}

	public void visit(Expression1 p) {
		p.struct = p.getTerm().struct;
		// currAddop = p.struct; //da vidis posle kroz addoplistu dok ides dal su isti
		// tipovi kao ovaj
	}

	public void visit(Expression1Minus p) {
		p.struct = p.getTerm().struct;
		// currAddop = p.struct; //da vidis posle kroz addoplistu dok ides dal su isti
		// tipovi kao ovaj
	}

	public void visit(Expression p) {
		p.struct = p.getExpr1().struct;
	}

	//////////////////////////////////////////////////////
	//////////////////// DESIGNATORI///////////////////////
	public void visit(DesignatorStd p) {
		Obj ret = Tab.find(p.getDesigName());
		if (ret == Tab.noObj) {
			report_error("Greska na liniji " + p.getLine() + " : ime " + p.getDesigName() + " nije deklarisano! ",
					null);
		}
		tv.visitObjNode(ret);
		report_info("Upotreba (" + ret.getName() + ") : " + tv.getOutput(), p);
		tv.clearOutput();
		p.obj = ret; // da mozes posle proveris tip kod dodele
	}

	public void visit(DesignatorArr p) {
		Obj ret = Tab.find(p.getDesigName());
		if (ret == Tab.noObj) {
			report_error("Greska na liniji " + p.getLine() + " : ime " + p.getDesigName() + " nije deklarisano! ",
					null);
		}
		// p.obj = new Obj(Obj.Elem, ret.getName(), ret.getType().getElemType());
		if (p.getExpr().struct != Tab.intType) {
			report_error("Semanticka greska - nevalidan pristup elementu niza, indeks mora biti ceo broj "
					+ p.getDesigName(), p);
		} else if (ret.getType().getKind() != Struct.Array) {
			report_error("Semanticka greska - " + p.getDesigName() + " mora biti niz ", p);
		}
		tv.visitObjNode(ret);
		report_info("Upotreba  (" + ret.getName() + ") : " + tv.getOutput(), p);
		tv.clearOutput();
		p.obj = new Obj(Obj.Elem, ret.getName(), ret.getType().getElemType());
		if(ret.getFpPos()==1) {
			p.obj.setFpPos(1);
		} else p.obj.setFpPos(0);
	}

	///////////////////////////////////////////////////
	//////////////// DESIGNATOR STATEMENT////////////////
	public void visit(Assignment p) {
		if (!p.getExpr().struct.assignableTo(p.getDesignator().obj.getType())) {
			report_error("Greska na liniji " + p.getLine() + " : " + "nekompatibilni tipovi u dodeli vrednosti! ",
					null);
		}
	}

	public void visit(Incr p) {
		if (p.getDesignator().obj.getType() != Tab.intType && p.getDesignator().obj.getKind() != Obj.Elem) {
			report_error("Greska na liniji " + p.getLine() + " : " + " tip operatora ++ mora biti ceo broj ili el. niza! ", null);
		}
	}

	public void visit(Decr p) {
		if (p.getDesignator().obj.getType() != Tab.intType && p.getDesignator().obj.getKind() != Obj.Elem) {
			report_error("Greska na liniji " + p.getLine() + " : " + " tip operatora -- mora biti ceo broj ili el. niza! ", null);
		}
	}

	//////////////////////////////////////////////////////////
	//////////////////// READ I PRINT//////////////////////////
	public void visit(Read p) {
		Struct type = p.getDesignator().obj.getType();
		if (type == Tab.charType || type == Tab.intType || type == boolType) {
			// report_info("Obrada Read smene", p);
		} else {
			report_error(
					"Greska pri read na liniji " + p.getLine() + " : " + ": dozvoljeni tipovi su int, char, bool! ",
					null);
		}
	}

	public void visit(PrintNum p) {
		Struct type = p.getExpr().struct;
		if (type == Tab.charType || type == Tab.intType || type == boolType) {
			// report_info("Obrada PrintNum smene", p);
		} else {
			report_error(
					"Greska pri print na liniji " + p.getLine() + " : " + ": dozvoljeni tipovi su int, char, bool! ",
					null);
		}
	}

	public void visit(PrintNoNum p) {
		Struct type = p.getExpr().struct;
		if (type == Tab.charType || type == Tab.intType || type == boolType) {
			// report_info("Obrada PrintNoNum smene", p);
		} else {
			report_error(
					"Greska pri print na liniji " + p.getLine() + " : " + ": dozvoljeni tipovi su int, char, bool! ",
					null);
		}
	}

	//////////////////////////////////////////////////////////
	public void visit(TernaryOp p) {
		if (p.getTernaryCond().struct == Tab.charType) {
			report_error("Greska - prvi izraz ternarnog operatora mora biti bool ili ceo broj", p);
		}
		if (p.getTernaryExpr1().struct != p.getTernaryExpr2().struct) {
			report_error("Greska - drugi i treci izraz ternarnog operatora moraju biti istog tipa", p);
		} else {
			p.struct = p.getTernaryExpr1().struct;
		}

	}

	public void visit(TernaryCond p) {
		p.struct = p.getExpr1().struct;
	}

	public void visit(TernaryExpr1 p) {
		p.struct = p.getExpr1().struct;
	}

	public void visit(TernaryExpr2 p) {
		p.struct = p.getExpr1().struct;
	}

	////////////////// LISTE ZA ADD I MUL /////////////////////
	public void visit(AddopTermLista p) {
		if (p.getTerm().struct != Tab.intType) {
			report_error("Greska na liniji " + p.getLine() + " - Ne mogu se sabirati razliciti tipovi", p);
		}
	}

	public void visit(MulopFactorLista p) {
		if (p.getFactor().struct != Tab.intType) { // moze i currMulop al tjt
			report_error("Greska na liniji " + p.getLine() + " - Ne mogu se mnoziti razliciti tipovi", p);
		}
	}

	//////////////////////////////////////////////////////////
	//////////////////////// ORD/CHR/LEN///////////////////////
	public void visit(FactorFunctionCallPars p) {
		Obj func = p.getDesignator().obj;
		p.struct = p.getDesignator().obj.getType();
		if (func.getName().equals("ord")) {
			if (p.getExpr().struct != Tab.charType) {
				report_error("Parametar inline funkcije ord mora biti tipa char", p);
			}
		} else if (func.getName().equals("chr")) {
			if (p.getExpr().struct != Tab.intType) {
				report_error("Parametar  inline funkcije chr mora biti tipa int", p);
			}
		} else if (func.getName().equals("len")) {
			Struct niz = new Struct(Struct.Array, p.getExpr().struct.getElemType());
			int kind = p.getExpr().struct.getKind();
			if (kind != Struct.Array) {
				report_error("Parametar inline funkcije len mora biti tipa niz", p);
			}
		} else {
			report_error("Ne postoji " + p.getLine() + " kao inline funkcija", p);
		}

	}

	//////////////////////////////////////////
	public void visit(Swap p) {
		if(p.getDesignator().obj.getKind() != Obj.Var) {
			report_error("Designator mora biti niz", p);
		}
	}
	
	public void visit(FactorHash p) {
		p.struct = Tab.intType;
	}
	
	public void visit(FactorAt p) {
		p.struct = Tab.intType;
	}

}
