package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_INT;
import TYPES.TYPE_LIST;

public class AST_NEWEXP extends AST_Node{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP e;
	public String type;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_NEWEXP( String type, AST_EXP e)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (e != null) System.out.format("NEW %s(exp)\n", type);
		else System.out.format("NEW %s\n", type);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.e = e;
		this.type = type;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST NEWEXP KAKI */
		/**************************************/
		System.out.print("AST NODE NEWEXP\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (e != null) e.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		if (e != null) AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NEW %s(exp...)\n", type));
		else AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("NEW %s\n", type));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (e != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,e.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		TYPE t = SYMBOL_TABLE.getInstance().find(type);
		if (t == null)
			throw new SemanticRuntimeException(lineNum, colNum, String.format("non existing type (%s) for (NEWEXP)\n", type));
		/*******************************************************/
		/* when e == null, NEWEXP should be: 'NEW <CLASSNAME>' */
		/*******************************************************/
		//allow all types not including primitive types
		if (e == null)
		{
			if (type.equals("int") || type.equals("string"))
				throw new SemanticRuntimeException(lineNum, colNum, String.format("an attempt to use 'NEW' with primitive type (%s)\n", type));
			
			return t;
		}
		/******************************************************************/
		/* when e != null, NEWEXP should be: 'NEW <TYPE>[<integral exp>]' */
		/******************************************************************/
		//allow all existing types
		//when encountring TYPE_ARRAY in AST_STMT_ASSIGN, should check if both sides are TYPE_ARRAY (or right side = nil) and also if both sides have the same type
		if (e.SemantMe() != TYPE_INT.getInstance())
			throw new SemanticRuntimeException(lineNum, colNum, "expression (exp) of 'NEW <TYPE>[<exp>]' is not an integral\n");
		return new TYPE_ARRAY(t,null);
	}
	
}
