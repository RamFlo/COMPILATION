/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; } 

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; } 
	/**********************************************/
	/* Create string buffer for strings */
	/**********************************************/
	StringBuffer string = new StringBuffer();
	
	/**********************************************/
	/* Create integer buffer for strings */
	/**********************************************/
	StringBuffer myInt = new StringBuffer();
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t\f]
INTEGER			= 0 | [1-9][0-9]*
ID				= [a-zA-Z][a-zA-Z0-9]*
MINUS_INTEGER   = -[1-9][0-9]*

%state STRING
%state COMMENT_ONE_LINE
%state COMMENT_MULTI_LINE
/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

"+"					{ return symbol(TokenNames.PLUS.ordinal());}
"-"					{ return symbol(TokenNames.MINUS.ordinal());}
"*"					{ return symbol(TokenNames.TIMES.ordinal());}
"/"					{ return symbol(TokenNames.DIVIDE.ordinal());}
"("					{ return symbol(TokenNames.LPAREN.ordinal());}
")"					{ return symbol(TokenNames.RPAREN.ordinal());}
"["					{ return symbol(TokenNames.LBRACK.ordinal());}
"]"					{ return symbol(TokenNames.RBRACK.ordinal());}
"{"					{ return symbol(TokenNames.LBRACE.ordinal());}
"}"					{ return symbol(TokenNames.RBRACE.ordinal());}
","					{ return symbol(TokenNames.COMMA.ordinal());}
"."					{ return symbol(TokenNames.DOT.ordinal());}
";"					{ return symbol(TokenNames.SEMICOLON.ordinal());}
":="				{ return symbol(TokenNames.ASSIGN.ordinal());}
"="					{ return symbol(TokenNames.EQ.ordinal());}
"<"					{ return symbol(TokenNames.LT.ordinal());}
">"					{ return symbol(TokenNames.GT.ordinal());}
"class"				{ return symbol(TokenNames.CLASS.ordinal());}
"nil"				{ return symbol(TokenNames.NIL.ordinal());}
"array"				{ return symbol(TokenNames.ARRAY.ordinal()	);}
"while"				{ return symbol(TokenNames.WHILE.ordinal());}
"extends"			{ return symbol(TokenNames.EXTENDS.ordinal());}
"return"			{ return symbol(TokenNames.RETURN.ordinal());}
"new"				{ return symbol(TokenNames.NEW.ordinal());}
"if"				{ return symbol(TokenNames.IF.ordinal());}
\"                  { string.setLength(0); yybegin(STRING); }
"//"				{ yybegin(COMMENT_ONE_LINE); }
"/*"				{ yybegin(COMMENT_MULTI_LINE); }
{INTEGER}			{ 
						Integer x = new Integer(yytext());
						if (x > 32767) return symbol(TokenNames.ERROR.ordinal());
						else return symbol(TokenNames.INT.ordinal(), x);
					}
{MINUS_INTEGER}		{ 
						Integer x = new Integer(yytext());
						if (x < -32768) return symbol(TokenNames.ERROR.ordinal());
						else return symbol(TokenNames.INT.ordinal(), x);
					}
{ID}				{ return symbol(TokenNames.ID, new String( yytext()));}   
{WhiteSpace}		{ /* just skip what was found, do nothing */ }
<<EOF>>				{ return symbol(TokenNames.EOF.ordinal());}
}

<STRING> {
\"                  { yybegin(YYINITIAL); return symbol(TokenNames.STRING, string.toString()); }
[a-zA-Z]+           { string.append( yytext() ); }
}

<COMMENT_ONE_LINE> {
{LineTerminator}    									{ yybegin(YYINITIAL); }
<<EOF>>    												{ return symbol(TokenNames.EOF.ordinal()); }
[-a-zA-Z0-9\. \t\f\(\)\{\}\[\]\?\!\+\*\/\;]+            { /* comment still ongoing, do nothing */ }
}

<COMMENT_MULTI_LINE> {
"*/"    															{ yybegin(YYINITIAL); }
[-a-zA-Z0-9\. \t\f\(\)\{\}\[\]\?\!\+\*\/\;(\r\n|\r|\n)]+            { /* comment still ongoing, do nothing */ }
}

/* error fallback */
[^]                              { return symbol(TokenNames.ERROR.ordinal()); }

