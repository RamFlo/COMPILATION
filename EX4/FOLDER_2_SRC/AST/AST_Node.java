package AST;

import SYMBOL_TABLE.ENUM_OBJECT_CONTEXT.ObjectContext;

public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int SerialNumber;
	public int lineNum, colNum;
	public ObjectContext objContext = ObjectContext.nonObject;
	public int objectIndexInContext = -1;
	
	/***********************************************/
	/* The default message for an unknown AST node */
	/***********************************************/
	public void PrintMe()
	{
		System.out.print("AST NODE UNKNOWN\n");
	}
	public void setLineAndColNum(int line, int col)
	{
		this.lineNum = line;
		this.colNum = col;
	}
}
