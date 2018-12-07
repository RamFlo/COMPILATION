package AST;

import MyExceptions.SemanticRuntimeException;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_INT;
import TYPES.TYPE_LIST;

public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_VAR var;
	public AST_EXP subscript;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SUBSCRIPT(AST_VAR var,AST_EXP subscript)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		var.PrintMe();
		subscript.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,subscript.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		TYPE t = null;
		TYPE_ARRAY ta = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.SemantMe();
		
		/*********************************/
		/* [2] Make sure type is a class */
		/*********************************/
		if (!(t instanceof TYPE_ARRAY))
			throw new SemanticRuntimeException(lineNum, colNum, "trying to access subscript of a variable that is not an ARRAY\n");
		else
			ta = (TYPE_ARRAY) t;
		
		/***********************************************/
		/* [3] Make sure subscript's exp is an integer */
		/***********************************************/
		if (subscript.SemantMe() != TYPE_INT.getInstance())
			throw new SemanticRuntimeException(lineNum, colNum, "subscript (sub) of 'VAR[<sub>]' is not an integral\n");
		
		
		/*******************************/
		/* [4] return the array's type */
		/*******************************/
		return ta.arrayType;
	}
}
