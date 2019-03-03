package AST;

import IR.IR;
import IR.IRcommand_Store_Word_Offset;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TEMP.TEMP;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_NIL;
import TYPES.TYPE_ARRAY;


public class AST_STMT_ASSIGN extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public AST_VAR var;
	public AST_EXP exp;
	public AST_NEWEXP newExp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN(AST_VAR var,AST_EXP exp,AST_NEWEXP newExp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.exp = exp;
		this.newExp = newExp;
	}
	

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE ASSIGN STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		var.PrintMe();
		if (exp != null) exp.PrintMe();
		if (newExp != null) newExp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"ASSIGN\nleft := right\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
		if (newExp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,newExp.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		TYPE t1 = null;
		TYPE t2 = null;
		
		t1 = var.SemantMe();
		
		if (exp != null){
			t2 = exp.SemantMe();
			
			if (t1.getClass() == t2.getClass()){
				if (t1 instanceof TYPE_CLASS && !isExtends((TYPE_CLASS)t1, (TYPE_CLASS)t2))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=class)var := (type=class)exp (not equal/extends)\n");
				
				if (t1 instanceof TYPE_ARRAY) {
					if (!((TYPE_ARRAY) t1).name.equals(((TYPE_ARRAY) t2).name))
						throw new SemanticRuntimeException(lineNum, colNum,
								"type mismatch for (type=array)var := (type=array_other)exp\n");
				}
					
			}
			
			else{ /*t1.getClass() != t2.getClass()*/
				if (t2 == TYPE_NIL.getInstance() &&
						((!(t1 instanceof TYPE_CLASS)) && (!(t1 instanceof TYPE_ARRAY))))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=int/string)var := (type=nil)exp\n");
				
				if (t2 != TYPE_NIL.getInstance())
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for var := exp\n");	
			}
		}
		
		else { /*newExp != null*/
			t2 = newExp.SemantMe();
			
			if (t1.getClass() == t2.getClass()){
				if (t1 instanceof TYPE_CLASS && !isExtends((TYPE_CLASS)t1, (TYPE_CLASS)t2))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=class)var := NEW (type=class)newExp (not equal/extends)\n");
				
				else if (t1 instanceof TYPE_ARRAY) {/*t1.getclass()==t2.getclass()==TYPE_ARRAY*/					
					if (!((TYPE_ARRAY)t1).arrayTypeString.equals(((TYPE_ARRAY)t2).arrayTypeString))
						throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=TYPE_ARRAY)var := NEW (type=TYPE_ARRAY)newExp (the type is differrent from what was declare)\n");
				}
			}
			else throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for var := NEW newExp\n");
		}
		
		return null;
	}
	
	private boolean isExtends(TYPE_CLASS t1, TYPE_CLASS t2) {
		if (t2 == null) return false;
		if (t1.name.equals(t2.name)) return true;
		TYPE_CLASS tmp = t2.father;
		return isExtends(t1, tmp);
	}
	
	public TEMP IRme()
	{
		TEMP left, right;
		left = this.var.get_L_Value();
		
		right = (this.exp == null)? this.newExp.IRme():this.exp.IRme();
		
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Word_Offset(right,0,left));
		
		return null;
	}
}
