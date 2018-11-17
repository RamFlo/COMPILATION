package AST;

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
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"NEWEXP\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (e != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,e.SerialNumber);
	}
	
}
