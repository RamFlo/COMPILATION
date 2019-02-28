package AST;

import IR.IR;
import IR.IRcommand_Allocate_On_Stack;
import IR.IRcommand_Dealloc_Stack;
import IR.IRcommand_End_Function;
import IR.IRcommand_Jump_ra;
import IR.IRcommand_Store_Word_Stack_Offset;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TEMP.TEMP;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_NIL;
import TYPES.TYPE_VOID;

public class AST_STMT_RETURN extends AST_STMT
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_RETURN(AST_EXP exp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.exp = exp;
	}

	/************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE STMT RETURN\n");

		/*****************************/
		/* RECURSIVELY PRINT exp ... */
		/*****************************/
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"RETURN");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}
	
	private boolean isExtends(TYPE_CLASS t1, TYPE_CLASS t2) {
		if (t2 == null) return false;
		if (t1.name.equals(t2.name)) return true;
		TYPE_CLASS tmp = t2.father;
		return isExtends(t1, tmp);
	}
	
	public TYPE SemantMe()
	{
		TYPE expType = null;
		TYPE curFunctionReturnType = SYMBOL_TABLE.getInstance().curFunctionReturnType;
		if (exp == null)
		{
			if (curFunctionReturnType != TYPE_VOID.getInstance())
				throw new SemanticRuntimeException(lineNum, colNum, "empty return statement for function with non-void return type\n");
		}
		else
		{
			expType = exp.SemantMe();

			if (curFunctionReturnType.getClass() == expType.getClass()) {
				if (curFunctionReturnType instanceof TYPE_CLASS && !isExtends((TYPE_CLASS) curFunctionReturnType, (TYPE_CLASS) expType))
					throw new SemanticRuntimeException(lineNum, colNum,
							"type mismatch for function's return type and return statement (different classes)\n");

				if (curFunctionReturnType instanceof TYPE_ARRAY)
				{
					if (!((TYPE_ARRAY)curFunctionReturnType).name.equals(((TYPE_ARRAY)expType).name))
						throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for function's return type and return statement (different array types)\n");
				}
					
			}

			else { /* curFunctionReturnType.getClass() != expType.getClass() */
				if (expType == TYPE_NIL.getInstance() && ((!(curFunctionReturnType instanceof TYPE_CLASS)) && (!(curFunctionReturnType instanceof TYPE_ARRAY))))
					throw new SemanticRuntimeException(lineNum, colNum,
							"type mismatch for function's return type and return statement (nil)\n");

				if (expType != TYPE_NIL.getInstance())
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch function's return type and return statement\n");
			}
		}
		return null;
	}
	
	public TEMP IRme()
	{
		int funcLocalsNum = IR.getInstance().curFunctionParamNum;
		
		// pop locals, fp = prevfp, pop prevfp and func name address
		IR.getInstance().Add_currentListIRcommand(new IRcommand_End_Function(funcLocalsNum));
		
		if (this.exp != null)
		{
			TEMP retVal = this.exp.IRme();
			// allocate space for retVal on stack
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(1));
							
			// save retVal on stack
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Word_Stack_Offset(retVal,0));
		}
		
		// jump to ra
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_ra());
		
		return null;
	}
}

