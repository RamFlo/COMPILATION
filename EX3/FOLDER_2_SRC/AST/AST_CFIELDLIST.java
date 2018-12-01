package AST;

public class AST_CFIELDLIST extends AST_Node {
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_DEC_FUNC headFunc;
	public AST_DEC_VAR headVar;
	public AST_CFIELDLIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELDLIST(AST_DEC_FUNC headFunc,AST_CFIELDLIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== classFields -> classField classFields\n");
		if (tail == null) System.out.print("====================== classFields -> classField      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.headFunc = headFunc;
		this.tail = tail;
	}
	public AST_CFIELDLIST(AST_DEC_VAR headVar,AST_CFIELDLIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== classFields -> classField classFields\n");
		if (tail == null) System.out.print("====================== classFields -> classField      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.headVar = headVar;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST CFIELD LIST */
		/**************************************/
		System.out.print("AST NODE CFIELD LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (headVar != null) headVar.PrintMe();
		if (headFunc != null) headFunc.PrintMe();
		if (tail != null) tail.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"CFIELD\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (headVar != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,headVar.SerialNumber);
		if (headFunc != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,headFunc.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	
}
