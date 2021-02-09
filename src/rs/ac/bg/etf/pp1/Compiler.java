package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java_cup.runtime.Symbol;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;
import rs.ac.bg.etf.pp1.MyTableDumpVisitor;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class Compiler {

	private static Logger loge = Logger.getLogger("error");
	private static Logger log = Logger.getLogger("info");
	
	private static FileAppender fa,fae;
	
	static {
		fa = new FileAppender();
		fa.setLayout(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN));
		//fa.setFile("test/nesto5.mj");
		//fa.activateOptions();
		//log.addAppender(fa);
		
		fae = new FileAppender();
		fae.setLayout(new PatternLayout(PatternLayout.DEFAULT_CONVERSION_PATTERN));
		//fae.setFile("test/nesto7.mj");
		//fae.activateOptions();
		//loge.addAppender(fae);
	}

	public static void main(String[] args) throws Exception {
		File f = new File(args[0]);
		if (f.exists()) {
			if(args.length==3 || args.length==4) {
				String out = args[2].substring(1);
				String err = "";
				if(args.length==4) {
					err = args[3].substring(1);
					PrintWriter pw = new PrintWriter("test/"+err);
					pw.close();
				}
				
				PrintWriter pw = new PrintWriter("test/"+out);//da obrises staro
				pw.close();
				
				if(err.equals("")) {
					fa.setFile("test/"+out);
					fae.setFile("test/"+out);
					fa.activateOptions();
					fae.activateOptions();
					log.addAppender(fa);
					log.addAppender(fae);
				} else {
					fa.setFile("test/"+out);
					fae.setFile("test/"+err);
					fa.activateOptions();
					fae.activateOptions();
					log.addAppender(fa);
					loge.addAppender(fae);
				}
				
			}
			log.info("Obrada ulaznog fajla " + f.getPath());
			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);
			Yylex myLexer = new Yylex(br);
			MJParser myParser = new MJParser(myLexer);
			Symbol sym = myParser.parse();
			Program prog = (Program) sym.value;
			if (myParser.getErrorDetected()) {
				loge.info("Ulazni fajl " + args[0] + " ima sintaksne greske!");
			} else {
				log.info("=====================SINTAKSNO_STABLO=========================");
				log.info("\n" + prog.toString(""));

				Tab.init(); //pravi universe
				SemanticAnalyzer semanticCheck = new SemanticAnalyzer();
				prog.traverseBottomUp(semanticCheck);

				tsdump();

				if (semanticCheck.getErrorDetected()) {
					loge.error("Ulazni fajl " + args[0] + " ima semanticke greske!");
				} else {
					log.info("Sintaksna i semanticka analiza uspesno zavrsena!");								
					if (args.length > 1) {						
						log.info("======================GENERISANJE_KODA========================");
						CodeGenerator cg = new CodeGenerator(semanticCheck.getProgram()); 
						prog.traverseBottomUp(cg);
						
						File f2 = new File(args[1]); 
						if (f2.exists()) {
							f2.delete();
						} 
						Code.dataSize = semanticCheck.nVars;
						Code.mainPc = cg.getMainPc();
						FileOutputStream fos = new FileOutputStream(f2);						
						
						if (cg.getErrorDetected()) {
							loge.error ("Generisanje koda neuspesno!");
						} else {
							log.info("Generisanje koda uspesno zavrseno!");
							Code.write(fos);
							log.info("Generisanje izlaznog fajla " + f2.getPath());
						}
						
						log.info("==============================================================");
					}					
				}										
			}
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			loge.error("Nema ulaznog fajla!");
		}
	}
	
	public static void tsdump() {
		MyTableDumpVisitor mv = new MyTableDumpVisitor();
		Tab.dump(mv);
	}

}
