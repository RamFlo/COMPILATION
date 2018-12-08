package AST;
import javax.swing.event.RowSorterEvent.Type;

import MyExceptions.SemanticRuntimeException;
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
		Type t1 = null;
		Type t2 = null;
		
		t1 = var.SemantMe();
		if (exp != null) t2 = exp.SemantMe();
		else t2 = newExp.SemantMe();
		
		if (t1.getClass() != t2.getClass()){
			if (t2 == TYPE_NIL.getInstance()){
				if (t1 == TYPE_INT.getInstance() || t1 == TYPE_STRING.getInstsance())
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=INT/STRING)var := (type=NIL)exp/newExp\n");
			}
			
			else if (t1.getClass() ){
				
			}
		}
		
		else{
			if (t1.getClass() == TYPE_CLASS){ /*t1==t2==type_class*/
				if (!isExtends((TYPE_CLASS)t1, (TYPE_CLASS)t2))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=class)var := (type=class)exp/newExp\n");
			}	
		}
		
		return null;
	}
	
	private boolean isExtends(TYPE_CLASS t1, TYPE_CLASS t2){
		if (t2 == null) return false;
		if (t1.name == t2.name) return true;
		TYPE_CLASS tmp = t2.father;
		return isExtends(t1, tmp);
		
	}
}
