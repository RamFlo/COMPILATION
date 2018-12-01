package AST;

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
}