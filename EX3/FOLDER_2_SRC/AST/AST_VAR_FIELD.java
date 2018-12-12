package AST;

import MyExceptions.SemanticRuntimeException;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
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
		
		/*************************************/
		/* [3] Look for fieldlName inside tc */
		/*************************************/
		for (TYPE_LIST it=tc.data_members;it != null;it=it.tail)
		{
			if (it.head.name == fieldName)
				return it.head;
		}
		
		/**************************************************/
		/* [4] Look for fieldlName inside tc's superclass */
		/**************************************************/
		TYPE_CLASS tc_father = tc.father;
		
		while (tc_father != null)
		{
			for (TYPE_LIST it=tc_father.data_members; it != null; it=it.tail)
			{
				if (it.head.name == fieldName)
					return it.head;
			}
			tc_father = tc_father.father;
		}
		
		/*********************************************/
		/* [5] fieldName does not exist in class var */
		/*********************************************/
		throw new SemanticRuntimeException(lineNum, colNum, String.format("field (%s) does not exist in class (%s)\n",fieldName,tc.name));
	}
}
