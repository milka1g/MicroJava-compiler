package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;


%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line //sa yyline dohvatis trenutnu liniju
%column //sa yycolumn dohvatis trenutnu kolonu

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\f" 	{ }

<YYINITIAL> "//" 		     { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }
<COMMENT> .      { yybegin(COMMENT); }


"program"   { return new_symbol(sym.PROG, yytext()); }
"print" 	{ return new_symbol(sym.PRINT, yytext()); }
"return" 	{ return new_symbol(sym.RETURN, yytext()); }
"void" 		{ return new_symbol(sym.VOID, yytext()); }
<YYINITIAL> "+" 		{ return new_symbol(sym.PLUS, yytext()); }
"-" 		{ return new_symbol(sym.MINUS, yytext()); }
"*" 		{ return new_symbol(sym.MULT, yytext()); }
"/" 		{ return new_symbol(sym.DIV, yytext()); }
"%" 		{ return new_symbol(sym.MOD, yytext()); }
"=" 		{ return new_symbol(sym.EQUAL, yytext()); }
"==" 		{ return new_symbol(sym.EQ, yytext()); }
"!=" 		{ return new_symbol(sym.NOTEQ, yytext()); }
">" 		{ return new_symbol(sym.GR, yytext()); }
">=" 		{ return new_symbol(sym.GREQ, yytext()); }
"<" 		{ return new_symbol(sym.LE, yytext()); }
"<=" 		{ return new_symbol(sym.LEEQ, yytext()); }
"&&" 		{ return new_symbol(sym.AND, yytext()); }
"||" 		{ return new_symbol(sym.OR, yytext()); }
"++" 		{ return new_symbol(sym.INCR, yytext()); }
"--" 		{ return new_symbol(sym.DECR, yytext()); }
";" 		{ return new_symbol(sym.SEMI, yytext()); }
"."			{ return new_symbol(sym.DOT, yytext()); }
"?" 		{ return new_symbol(sym.QMARK, yytext()); }
":" 		{ return new_symbol(sym.COLON, yytext()); }
"," 		{ return new_symbol(sym.COMMA, yytext()); }
"(" 		{ return new_symbol(sym.LPAREN, yytext()); }
")" 		{ return new_symbol(sym.RPAREN, yytext()); }
"{" 		{ return new_symbol(sym.LBRACE, yytext()); }
"}"			{ return new_symbol(sym.RBRACE, yytext()); }
"@"			{ return new_symbol(sym.AT, yytext()); }
"["			{ return new_symbol(sym.SQBRL, yytext()); }
"]"			{ return new_symbol(sym.SQBRR, yytext()); }
"#"			{ return new_symbol(sym.HASH, yytext()); }
"break"		{ return new_symbol(sym.BREAK, yytext()); }
"~"			{ return new_symbol(sym.SWAP, yytext()); }
"class"		{ return new_symbol(sym.CLASS, yytext()); }
"enum"		{ return new_symbol(sym.ENUM, yytext()); }
"else"		{ return new_symbol(sym.ELSE, yytext()); }
"const"		{ return new_symbol(sym.CONST, yytext()); }
"if"		{ return new_symbol(sym.IF, yytext()); }
"switch"	{ return new_symbol(sym.SWITCH, yytext()); }
"do"		{ return new_symbol(sym.DO, yytext()); }
"while"		{ return new_symbol(sym.WHILE, yytext()); }
"new"		{ return new_symbol(sym.NEW, yytext()); }
"print"		{ return new_symbol(sym.PRINT, yytext()); }
"read"		{ return new_symbol(sym.READ, yytext()); }
"extends"		{ return new_symbol(sym.EXTENDS, yytext()); }
"continue"		{ return new_symbol(sym.CONTINUE, yytext()); }
"case"		{ return new_symbol(sym.CASE, yytext()); }
"final"		{ return new_symbol(sym.FINAL, yytext()); }
"goto"		{ return new_symbol(sym.GOTO, yytext()); }
"true"|"false"		{ return new_symbol(sym.BOOLCONST, new Boolean(yytext())); }
<YYINITIAL> "'"[ -~]"'" { return new_symbol(sym.CHARCONST, new Character(yytext().charAt(1))); }



([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{return new_symbol (sym.IDENT, yytext()); }
[0-9]+  { return new_symbol(sym.NUMCONST, new Integer(yytext())); }

. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)+" na poziciji "+(yycolumn+1)+"!"); }






