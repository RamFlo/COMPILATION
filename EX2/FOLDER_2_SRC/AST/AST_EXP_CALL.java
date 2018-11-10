package AST;

public class AST_EXP_CALL extends AST_EXP
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String callingObjectName;
	public String funcName;
	public AST_EXP_LIST params;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_CALL(String callingObjectName, String funcName,AST_EXP_LIST params)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.callingObjectName = callingObjectName;
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
		if (callingObjectName == null) System.out.format("CALL(%s)\nWITH:\n",funcName);
		else System.out.format("CALLER: %s\nCALL(%s)\nWITH:\n",callingObjectName,funcName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (params != null) params.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (callingObjectName == null) AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CALL(%s)\nWITH",funcName));
		else 
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);		
	}
}
