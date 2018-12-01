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
%eofval{
return symbol(TokenNames.EOF);
%eofval}
/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied letter to letter into the Lexer class code.                */
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
	public int getLine()    { return yyline + 1; } 
	public int getCharPos() { return yycolumn;   } 
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
WhiteSpace	= {LineTerminator} | [ \t\f]
INTEGER		= 0 | [1-9][0-9]*
ID		= [a-zA-Z][a-zA-Z0-9]*
/*MINUS_INTEGER   = -[1-9][0-9]**/
LEADING_ZEROES  = [0]+[0-9]+
STRINGS		= \"([a-zA-Z]*)\"
   
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

"+"					{ return symbol(TokenNames.PLUS);}
"-"					{ return symbol(TokenNames.MINUS);}
"*"					{ return symbol(TokenNames.TIMES);}
"/"					{ return symbol(TokenNames.DIVIDE);}
"("					{ return symbol(TokenNames.LPAREN);}
")"					{ return symbol(TokenNames.RPAREN);}
"["					{ return symbol(TokenNames.LBRACK);}
"]"					{ return symbol(TokenNames.RBRACK);}
"{"					{ return symbol(TokenNames.LBRACE);}
"}"					{ return symbol(TokenNames.RBRACE);}
","					{ return symbol(TokenNames.COMMA);}
"."					{ return symbol(TokenNames.DOT);}
";"					{ return symbol(TokenNames.SEMICOLON);}
":="				{ return symbol(TokenNames.ASSIGN);}
"="					{ return symbol(TokenNames.EQ);}
"<"					{ return symbol(TokenNames.LT);}
">"					{ return symbol(TokenNames.GT);}
"class"				{ return symbol(TokenNames.CLASS);}
"nil"				{ return symbol(TokenNames.NIL);}
"array"				{ return symbol(TokenNames.ARRAY	);}
"while"				{ return symbol(TokenNames.WHILE);}
"extends"			{ return symbol(TokenNames.EXTENDS);}
"return"			{ return symbol(TokenNames.RETURN);}
"new"				{ return symbol(TokenNames.NEW);}
"if"				{ return symbol(TokenNames.IF);}
{STRINGS}			{ return symbol(TokenNames.STRING, yytext()); }
"//"				{ yybegin(COMMENT_ONE_LINE); }
"/*"				{ yybegin(COMMENT_MULTI_LINE); }
{INTEGER}			{ 
						if (yytext().length() > 5) {System.out.println("Lexer error 4"); return symbol(TokenNames.error);}
						Integer x = new Integer(yytext());
						if (x > 32767) {System.out.println("Lexer error 3"); return symbol(TokenNames.error);}
						else return symbol(TokenNames.INT, x);
					}
/*{MINUS_INTEGER}		{
						if (yytext().length() > 6) return symbol(TokenNames.error);
						Integer x = new Integer(yytext());
						if (x < -32768) return symbol(TokenNames.error);
						else return symbol(TokenNames.INT, x);
					}*/
{LEADING_ZEROES} 	{ System.out.println("Lexer error 2"); return symbol(TokenNames.error); }				 
/*"-0"				{ return symbol(TokenNames.error); }*/
{ID}				{ return symbol(TokenNames.ID, new String( yytext()));}   
{WhiteSpace}		{ /* just skip what was found, do nothing */ }
<<EOF>>				{ return symbol(TokenNames.EOF);}
}

<COMMENT_ONE_LINE> {
{LineTerminator}    									{ yybegin(YYINITIAL); }
<<EOF>>    												{ return symbol(TokenNames.EOF); }
[-a-zA-Z0-9\. \t\f\(\)\{\}\[\]\?\!\+\*\/\;]+            { /* comment still ongoing, do nothing */ }
}

<COMMENT_MULTI_LINE> {
"*/"    															{ yybegin(YYINITIAL); }
<<EOF>>    															{ System.out.println("Lexer error 5"); return symbol(TokenNames.error); }
[-a-zA-Z0-9\. \t\f\(\)\{\}\[\]\?\!\+\*\/\;(\r\n|\r|\n)]             { /* comment still ongoing, do nothing */ }
}

/* error fallback */
[^]                              { System.out.println("Lexer error 1"); return symbol(TokenNames.error); }