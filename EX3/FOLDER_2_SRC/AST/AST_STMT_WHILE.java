package AST;

public class AST_STMT_WHILE extends AST_STMT
{
	public AST_EXP cond;
	public AST_STMT_LIST body;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_WHILE(AST_EXP cond,AST_STMT_LIST body)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.cond = cond;
		this.body = body;
	}

	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE STMT WHILE\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		cond.PrintMe();
		body.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"WHILE (left)\nDOs right");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,cond.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,body.SerialNumber);
	}
}