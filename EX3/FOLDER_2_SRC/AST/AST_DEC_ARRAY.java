package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_INT;
import TYPES.TYPE_STRING;

public class AST_DEC_ARRAY extends AST_DEC
{
	/********/
	/* NAME */
	/********/
	public String name;
	public String type;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_ARRAY(String name,String type)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		this.name = name;
		this.type = type;
	}

	/*********************************************************/
	/* The printing message for a class declaration AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.format("ARRAY NAME(%s)\nTYPE(%s)\n",name,type);
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("ARRAY\nNAME(%s)\nTYPE(%s)\n",name,type));
			
	}
	
	public TYPE SemantMe()
	{
		TYPE t;

		/********************************************************/
		/* [0] Semant type array decleration */
		/********************************************************/
		if (type.equals("string"))
			t = TYPE_STRING.getInstance();
		else if (type.equals("int"))
			t = TYPE_INT.getInstance();
		else
			t = SYMBOL_TABLE.getInstance().find(type);

		if (t == null)
		{
			throw new SemanticRuntimeException(lineNum, colNum, String.format
					("non existing type (%s) for array (%s)\n", type,name));
		}
		
		if (t instanceof TYPE_FUNCTION)
		{
			throw new SemanticRuntimeException(lineNum, colNum, String.format
					("function (%s) cannot be used as a type for an array \n",type));
		}
		
		/***************************************************/
		/* [1] Enter the array type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enter(name,new TYPE_ARRAY(name,t));
		
		/*********************************************************/
		/* [2] Return value is irrelevant for array declarations */
		/*********************************************************/
		return null;
	}
}