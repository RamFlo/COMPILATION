package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_FUNCTION;

public class AST_TYPE_NAME extends AST_Node
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String type;
	public String name;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_TYPE_NAME(String type,String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		this.type = type;
		this.name = name;
	}

	/*************************************************/
	/* The printing message for a type name AST node */
	/*************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST TYPE NAME NODE */
		/**************************************/
		System.out.format("NAME(%s):TYPE(%s)\n",name,type);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NAME:TYPE\n%s:%s",name,type));
	}
	
	/*****************/
	/* SEMANT ME ... */
	/*****************/
	public TYPE SemantMe()
	{
		TYPE t = null;
		
		/**************/
		/* type check */ //Done in AST_DEC_FUNC
		/**************/
//		TYPE t = SYMBOL_TABLE.getInstance().findDataType(type);
//		if (t == null)
//			throw new SemanticRuntimeException(lineNum, colNum, String.format("non existing type (%s) for parameter (%s)\n", type,name));
		
		/**************/
		/* name check */
		/**************/
		if (SYMBOL_TABLE.getInstance().findDataType(name) != null)
			throw new SemanticRuntimeException(lineNum, colNum, String.format("parameter's (%s) name is an existing data type\n",name));
		
		if (SYMBOL_TABLE.getInstance().findInCurrentScope(name) != null)
			throw new SemanticRuntimeException(lineNum, colNum, String.format("parameter's (%s) name is already used in function's scope\n",name));
		
		/*******************************************************/
		/* Enter var with name=name and type=t to symbol table */
		/*******************************************************/
		t = SYMBOL_TABLE.getInstance().findDataType(type);
		SYMBOL_TABLE.getInstance().enterObject(name,t);

		/****************************/
		/* return (existing) type t */
		/****************************/
		return t; //not used, since type_list is populated in AST_DEC_FUNC before semanting the input parameters
	}	
}

