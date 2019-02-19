package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import TEMP.TEMP;
import SYMBOL_TABLE.ENUM_SCOPE_TYPES.ScopeTypes;
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
		SYMBOL_TABLE_ENTRY searchRes = SYMBOL_TABLE.getInstance().findDataType(type);
		
		if (searchRes == null)
			throw new SemanticRuntimeException(lineNum, colNum, String.format
					("non existing type (%s) for array (%s)\n", type,name));
		
		t = searchRes.type;
		
		
		
		/*********************/
		/* [1] array name */
		/*********************/
		if (SYMBOL_TABLE.getInstance().find(name) != null || name.equals("void"))
		{
			throw new SemanticRuntimeException(lineNum, colNum, String.format("declared array type name (%s) is already in use\n", name));
		}
		
		/***************************************************/
		/* [2] Enter the array type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enterDataType(name,new TYPE_ARRAY(name,t,type),this);
		
		/*********************************************************/
		/* [3] Return value is irrelevant for array declarations */
		/*********************************************************/
		return null;
	}
	
	public TEMP IRme(){
		// nothing to do here??
		return null;
	}
}