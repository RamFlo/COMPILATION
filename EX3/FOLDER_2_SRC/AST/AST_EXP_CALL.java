package AST;

import TYPES.TYPE;

public class AST_EXP_CALL extends AST_EXP
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_VAR callingObject;
	public String funcName;
	public AST_EXP_LIST params;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_CALL(AST_VAR callingObject, String funcName,AST_EXP_LIST params)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.callingObject = callingObject;
		this.funcName = funcName;
		this.params = params;
	}

	/************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void PrintMe()
	{
		/*************************************************/
		/* AST NODE TYPE = AST NODE FUNCTION DECLARATION */
		/*************************************************/
		if (callingObject == null) System.out.format("CALL(%s)\nWITH:...",funcName);
		else System.out.format("CALLER: ...\nCALL(%s)\nWITH:...",funcName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (callingObject != null) callingObject.PrintMe();
		if (params != null) params.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (callingObject == null) AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CALL(%s)\nWITH...",funcName));
		else AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("CALLER: ...\nCALL(%s)\nWITH...",funcName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (callingObject != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,callingObject.SerialNumber);
		if (params != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);		
	}
	
	public TYPE SemantMe() {
		
	}
}
