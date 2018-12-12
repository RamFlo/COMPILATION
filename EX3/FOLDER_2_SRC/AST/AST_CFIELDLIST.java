package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_LIST;

public class AST_CFIELDLIST extends AST_Node {
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_DEC_FUNC headFunc;
	public AST_DEC_VAR headVar;
	public AST_CFIELDLIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELDLIST(AST_DEC_FUNC headFunc,AST_CFIELDLIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== classFields -> classField classFields\n");
		if (tail == null) System.out.print("====================== classFields -> classField      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.headFunc = headFunc;
		this.tail = tail;
	}
	public AST_CFIELDLIST(AST_DEC_VAR headVar,AST_CFIELDLIST tail)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (tail != null) System.out.print("====================== classFields -> classField classFields\n");
		if (tail == null) System.out.print("====================== classFields -> classField      \n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.headVar = headVar;
		this.tail = tail;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST CFIELD LIST */
		/**************************************/
		System.out.print("AST NODE CFIELD LIST\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (headVar != null) headVar.PrintMe();
		if (headFunc != null) headFunc.PrintMe();
		if (tail != null) tail.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"CFIELD\nLIST\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (headVar != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,headVar.SerialNumber);
		if (headFunc != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,headFunc.SerialNumber);
		if (tail != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,tail.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		//scope begins and ends in AST_DEC_CLASS, no need to begin scope here
		
		//TO-DO: should probably move entire SemantMe to AST_DEC_CLASS to properly handle overloading
		
		AST_DEC_FUNC curHeadFunc;
		AST_DEC_VAR curHeadVar;
		
		TYPE_FUNCTION curFunction = null;
		TYPE curVariant = null;
		/*************************************************************************************/
		/* [0] Semant data members and functions (without the functions' bodies\param names) */
		/*************************************************************************************/
		for (AST_CFIELDLIST it = this; it  != null; it = it.tail)
		{
			curHeadFunc = it.headFunc;
			curHeadVar = it.headVar;
			
			if (curHeadFunc != null) curFunction = (TYPE_FUNCTION) curHeadFunc.SemantFuncSignatureAndParamTypes();
			if (curHeadVar != null) curVariant = curHeadVar.SemantMe(); //MAKE SURE DEC_VAR returns its type!
			
			//TO-DO
			//use curFunction and curVariant to populate data_members, which is a TYPE_LIST of TYPE_CLASS (use it later
			// to compare with superclasses' data_members, in order to allow overloading and prevent shadowing)
			
			//TO-DO
			//if curHeadVar !=null, compare with superclasses' data_members and throw exception in case of equal names
			//if curHeadFunc != null, comapre with superclasses' data_members and throw exception in case of same name & non-overloading function
			
			
		}
		
		/************************************************/
		/* [1] Semant functions' param names and bodies */
		/************************************************/
		for (AST_CFIELDLIST it = this; it  != null; it = it.tail)
		{
			curHeadFunc = it.headFunc;
			if (curHeadFunc != null) curHeadFunc.SemantFuncParamNamesAndBody();
		}
		
		/**************************************************/
		/* [2] Return value is irrelevant for CFIELDLIST  */
		/**************************************************/
		return null;
	}
	
}
