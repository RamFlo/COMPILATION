package AST;

public class AST_EXP_NIL extends AST_EXP{
	
	public void PrintMe()
	{
		SerialNumber = AST_Node_Serial_Number.getFresh();
		
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.print("NIL\n");
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"NIL");
			
	}
}
