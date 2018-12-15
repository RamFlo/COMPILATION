package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;

public class AST_DEC_VAR extends AST_DEC
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String type;
	public String name;
	public AST_EXP initialValue;
	public AST_NEWEXP initialValueNew;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_VAR(String type,String name,AST_EXP initialValue, AST_NEWEXP newInitialValue)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.type = type;
		this.name = name;
		this.initialValue = initialValue;
		this.initialValueNew = newInitialValue;
	}
	

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void PrintMe()
	{
		/********************************/
		/* AST NODE TYPE = AST DEC LIST */
		/********************************/
		if (initialValue != null) System.out.format("VAR-DEC(%s):%s := initialValue\n",name,type);
		if (initialValueNew != null) System.out.format("VAR-DEC(%s):%s := initialValueNew\n",name,type);
		if (initialValue == null && initialValueNew == null) System.out.format("VAR-DEC(%s):%s\n",name,type);

		/**************************************/
		/* RECURSIVELY PRINT initialValue ... */
		/**************************************/
		if (initialValue != null) initialValue.PrintMe();
		if (initialValueNew != null) initialValueNew.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nDEC(%s)\nTYPE(%s)",name,type));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (initialValue != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,initialValue.SerialNumber);		
		if (initialValueNew != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,initialValueNew.SerialNumber);
			
	}
	//Copied from next exercise
	public TYPE SemantMe()
	{
		TYPE t;
	
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t = SYMBOL_TABLE.getInstance().find(type);
		if (t == null)
		{
			throw new SemanticRuntimeException(lineNum,colNum,String.format("non existing type %s\n",type));
		}
		
		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		if (SYMBOL_TABLE.getInstance().find(name) != null)
		{
			throw new SemanticRuntimeException(lineNum,colNum,String.format("variable %s already exists in scope\n",name));
		}

		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enterObject(name,t);

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return t;		
	}
}
