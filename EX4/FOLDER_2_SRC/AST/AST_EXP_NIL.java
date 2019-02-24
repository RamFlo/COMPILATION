package AST;

import IR.IR;
import IR.IRcommand_NIL;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;
import TYPES.TYPE_NIL;

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
	
	public TYPE SemantMe()
	{
		return TYPE_NIL.getInstance();
	}
	
	public TEMP IRme()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_NIL(t));
		return t;
	}
}
