package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_INT;
import TYPES.TYPE_LIST;
import TYPES.TYPE_STRING;
import TYPES.TYPE_VOID;

public class AST_DEC_FUNC extends AST_DEC
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String returnTypeName;
	public String name;
	public AST_TYPE_NAME_LIST params;
	public AST_STMT_LIST body;
	private TYPE funcReturnType = null;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_FUNC(
		String returnTypeName,
		String name,
		AST_TYPE_NAME_LIST params,
		AST_STMT_LIST body)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.returnTypeName = returnTypeName;
		this.name = name;
		this.params = params;
		this.body = body;
	}

	/************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void PrintMe()
	{
		/*************************************************/
		/* AST NODE TYPE = AST NODE FUNCTION DECLARATION */
		/*************************************************/
		System.out.format("FUNC(%s):%s\n",name,returnTypeName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (params != null) params.PrintMe();
		if (body   != null) body.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FUNC(%s)\n:%s\n",name,returnTypeName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (params != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);		
		if (body   != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);		
	}
	
	
	public TYPE SemantFuncSignatureAndParamTypes()
	{
		TYPE t = null;
		TYPE returnType = null;
		TYPE_LIST type_list = null;
		TYPE existingNamesType = null;

		/*******************/
		/* [0] return type */
		/*******************/
		returnType = returnTypeName.equals("void") ? TYPE_VOID.getInstance() : SYMBOL_TABLE.getInstance().findDataType(returnTypeName);
		if (returnType == null)
		{
			throw new SemanticRuntimeException(lineNum, colNum, String.format("non existing return type (%s)\n", returnType));
		}
		//should function be able to return an array type?
		
		this.funcReturnType = returnType;
		
		/*********************/
		/* [1] function name */
		/*********************/
		if (name.equals("void"))
			throw new SemanticRuntimeException(lineNum, colNum, String.format("declared function's name cannot be (void)\n", name));
		
		/***************************************************/
		/* [1.1] function name is not an existing dataType */
		/***************************************************/
		if (SYMBOL_TABLE.getInstance().findDataType(name) != null)
		{
			throw new SemanticRuntimeException(lineNum, colNum, String.format("declared function's name (%s) is already in use as dataType\n", name));
		}
		
		/***************************************************/
		/* [1.2] function name cannot be another function's name  */
		/***************************************************/
		
		if ((existingNamesType = SYMBOL_TABLE.getInstance().findInCurrentScope(name)) != null)
			throw new SemanticRuntimeException(lineNum, colNum,
					String.format("declared function's name (%s) is already in use\n", name));
		
		/********************************************************/
		/* [2] Semant type of input params & populate type_list */
		/********************************************************/
		for (AST_TYPE_NAME_LIST it = params; it  != null; it = it.tail)
		{
			String curParamType = it.head.type;
			
			t = SYMBOL_TABLE.getInstance().findDataType(curParamType);
			
			if (t == null)
				throw new SemanticRuntimeException(lineNum, colNum, String.format
						("non existing type (%s) for parameter (%s) at function (%s) decleration\n", it.head.type,it.head.name,name));
			
			type_list = new TYPE_LIST(t,type_list);
		}
		
		TYPE_FUNCTION curFunc = new TYPE_FUNCTION(returnType,name,type_list);

		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		//must enter function into symbol table BEFORE beginning the function's scope in order to allow recursive calls
		SYMBOL_TABLE.getInstance().enterObject(name,curFunc);
		
		return curFunc;
	}
	
	public void SemantFuncParamNamesAndBody()
	{
		/****************************/
		/* [4] Begin Function Scope */
		/****************************/
		SYMBOL_TABLE.getInstance().beginFunctionScope("FUNCTION",this.funcReturnType);
		
		/****************************************/
		/* [5] Semant Input Params (names only) */
		/****************************************/
		if (params != null) params.SemantMe();

		/*******************/
		/* [6] Semant Body */
		/*******************/
		body.SemantMe();

		/*****************/
		/* [7] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endFunctionScope();
	}
	
	public TYPE SemantMe() //used only for global functions
	{
		SemantFuncSignatureAndParamTypes();
		SemantFuncParamNamesAndBody();

		/*********************************************************/
		/* [8] Return value is irrelevant for function declarations */
		/*********************************************************/
		return null;		
	}
}
