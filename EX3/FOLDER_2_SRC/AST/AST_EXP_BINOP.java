package AST;

import MyExceptions.SemanticRuntimeException;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_INT;
import TYPES.TYPE_NIL;
import TYPES.TYPE_STRING;

public class AST_EXP_BINOP extends AST_EXP
{
	int OP;
	public AST_EXP left;
	public AST_EXP right;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_BINOP(AST_EXP left,AST_EXP right,int OP)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== exp -> exp BINOP exp\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.left = left;
		this.right = right;
		this.OP = OP;
	}
	
	/*************************************************/
	/* The printing message for a binop exp AST node */
	/*************************************************/
	public void PrintMe()
	{
		String sOP="";
		
		/*********************************/
		/* CONVERT OP to a printable sOP */
		/*********************************/
		switch (OP) {
		case 0:
			sOP = "+";
			break;
		case 1:
			sOP = "-";
			break;
		case 2:
			sOP = "*";
			break;
		case 3:
			sOP = "/";
			break;
		case 4:
			sOP = "<";
			break;
		case 5:
			sOP = ">";
			break;
		case 6:
			sOP = "=";
			break;
		}
		
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE BINOP EXP\n");

		/**************************************/
		/* RECURSIVELY PRINT left + right ... */
		/**************************************/
		if (left != null) left.PrintMe();
		if (right != null) right.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("BINOP(%s)",sOP));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (left  != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,left.SerialNumber);
		if (right != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,right.SerialNumber);
	}
	
	private boolean ExtendingOrSameClass(TYPE_CLASS t1, TYPE_CLASS t2){
		if (t2 == null) return false;
		if (t1.name.equals(t2.name)) return true;
		return ExtendingOrSameClass(t1, t2.father);
		
	}
	
	public TYPE SemantMe()
	{
		TYPE t1 = null;
		TYPE t2 = null;
		
		if (left  != null) t1 = left.SemantMe();
		if (right != null) t2 = right.SemantMe();
		
		//allow any binop for integers
		if ((t1 == TYPE_INT.getInstance()) && (t2 == TYPE_INT.getInstance()))
			return TYPE_INT.getInstance();
		
		//allow + for two strings
		if ((t1 == TYPE_STRING.getInstance()) && (t2 == TYPE_STRING.getInstance()) && (OP == 0))
			return TYPE_STRING.getInstance();
		
		//equality testing
		if (OP == 6)
		{
			//nil handling
			if (t1 instanceof TYPE_NIL || t2 instanceof TYPE_NIL)
			{
				// int = nil or nil = int
				if (t1 == TYPE_INT.getInstance() || t2 == TYPE_INT.getInstance())
					throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between 'int' and 'nil'\n");
				
				// string = nil or nil = string
				if (t1 == TYPE_STRING.getInstance() || t2 == TYPE_STRING.getInstance())
					throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between 'string' and 'nil'\n");
				
				//nil = <CLASS OBJECT> or nil = <ARRAY OBJECT>
				return TYPE_INT.getInstance();
			}
			
			
			if (t1.getClass() != t2.getClass())
				throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between two expressions of different types\n");
			
			//string = string
			if ((t1 == TYPE_STRING.getInstance()) && (t2 == TYPE_STRING.getInstance()))
				return TYPE_INT.getInstance();
			
			//t1 and t2 are arrays
			if (t1 instanceof TYPE_ARRAY)
			{
				String t1ArrayTypeString = ((TYPE_ARRAY)t1).arrayTypeString;
				String t2ArrayTypeString = ((TYPE_ARRAY)t2).arrayTypeString;
				if (t1ArrayTypeString.equals(t2ArrayTypeString))
					return TYPE_INT.getInstance();;
					
				//what about two arrays of different classes that have inheritance relation? should this case be handled differently?
					
				throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between two arrays of different types\n");
			}
			
			//t1 and t2 are classes
			if (t1 instanceof TYPE_CLASS)
			{
				if (ExtendingOrSameClass((TYPE_CLASS)t1,(TYPE_CLASS)t2) || ExtendingOrSameClass((TYPE_CLASS)t2,(TYPE_CLASS)t1))
					return TYPE_INT.getInstance();;
					
				throw new SemanticRuntimeException(lineNum, colNum, "equality testing cannot be done between two classes without inheritance realtion\n");
			}
			
		}
		
		throw new SemanticRuntimeException(lineNum, colNum, "illegal binary operation\n");
	}
}
