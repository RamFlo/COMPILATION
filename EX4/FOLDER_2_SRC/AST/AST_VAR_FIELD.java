package AST;

import IR.IR;
import IR.IRcommand;
import IR.IRcommandConstInt;
import IR.IRcommand_Jump_If_Eq_To_Zero;
import IR.IRcommand_Label;
import IR.IRcommand_Load;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.ENUM_OBJECT_CONTEXT.ObjectContext;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_DATA_MEMBERS_LIST;
import TYPES.TYPE_LIST;

public class AST_VAR_FIELD extends AST_VAR
{
	public AST_VAR var;
	public String fieldName;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_FIELD(AST_VAR var,String fieldName)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> var DOT ID( %s )\n",fieldName);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.fieldName = fieldName;
	}

	/*************************************************/
	/* The printing message for a field var AST node */
	/*************************************************/
	public void PrintMe()
	{
		/*********************************/
		/* AST NODE TYPE = AST FIELD VAR */
		/*********************************/
		System.out.print("AST NODE FIELD VAR\n");

		/**********************************************/
		/* RECURSIVELY PRINT VAR, then FIELD NAME ... */
		/**********************************************/
		if (var != null) var.PrintMe();
		System.out.format("FIELD NAME( %s )\n",fieldName);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("FIELD\nVAR\n...->%s",fieldName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		TYPE t = null;
		TYPE_CLASS tc = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.SemantMe();
		
		/*********************************/
		/* [2] Make sure type is a class */
		/*********************************/
		if (!(t instanceof TYPE_CLASS))
			throw new SemanticRuntimeException(lineNum, colNum, String.format("access (%s) field of a non-class variable\n",fieldName));
		else
			tc = (TYPE_CLASS) t;
		
		/**********************************************************/
		/* [3] Look for fieldlName inside tc and its superclasses */
		/**********************************************************/
		//delete later-debug
		if (tc.data_members == null)
			System.out.println(String.format("tc.data_members is null. tc name: %s",tc.name));
		//delete later-debug
		while (tc != null) {
			for (TYPE_CLASS_DATA_MEMBERS_LIST it = tc.data_members; it != null; it = it.tail) {
				if (it.head.name.equals(fieldName)){
					this.objIndexInContext = tc.dataMembersMap.get(fieldName);
					this.objScopeType = SYMBOL_TABLE.getInstance().curScopeType;
					this.objContext = ObjectContext.classDataMember;

					return it.head.type;
				}
			}
			tc = tc.father;
		}
		
		/*********************************************/
		/* [4] fieldName does not exist in class var */
		/*********************************************/
		throw new SemanticRuntimeException(lineNum, colNum, String.format("field (%s) does not exist in class (%s)\n",fieldName,t.name));
	}
	
	private void checkNullPtrDeref(TEMP t){
		
	}
	
	public TEMP IRme() {
		/*******************/
		/* [1] IR class object */
		/*******************/
		TEMP srcClass = var.IRme();
		
		checkNullPtrDeref(srcClass);

		/*************************************/
		/* [2] load class object data member */
		/*************************************/
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		int offset = 4 * this.objIndexInContext;
		IR.getInstance().Add_codeSegmentIRcommand(new IRcommand_Load(t, srcClass, offset));

		return t;
	}
}
