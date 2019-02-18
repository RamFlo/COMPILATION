package AST;

import SYMBOL_TABLE.ENUM_OBJECT_CONTEXT.ObjectContext;
import SYMBOL_TABLE.ENUM_SCOPE_TYPES.ScopeTypes;

public abstract class AST_Node
{
	/*******************************************/
	/* The serial number is for debug purposes */
	/* In particular, it can help in creating  */
	/* a graphviz dot format of the AST ...    */
	/*******************************************/
	public int SerialNumber;
	public int lineNum, colNum;
	public ScopeTypes objScopeType = ScopeTypes.globalScope;
	public ObjectContext objContext = ObjectContext.nonObject;
	public int objIndexInContext = -1;
	
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
