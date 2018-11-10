package AST;

public class AST_DEC_CLASS extends AST_DEC
{
	/********/
	/* NAME */
	/********/
	public String name;
	public String supername;

	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_TYPE_NAME_LIST data_members;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_CLASS(String name,String supername, AST_TYPE_NAME_LIST data_members)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		this.name = name;
		this.supername = supername;
		this.data_members = data_members;
	}

	/*********************************************************/
	/* The printing message for a class declaration AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.format("CLASS DEC = %s\n",name);
		if (supername != null) System.out.format("extends %s\n",supername);
		if (data_members != null) data_members.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (supername != null) 
			AST_GRAPHVIZ.getInstance().logNode(
					SerialNumber,
					String.format("CLASS\n%s\nEXTENDS\n%s",name,supername));
		else
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CLASS\n%s",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,data_members.SerialNumber);		
	}
}
