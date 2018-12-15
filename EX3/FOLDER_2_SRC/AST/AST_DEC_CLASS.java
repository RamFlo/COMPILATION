package AST;

import javax.swing.event.RowSorterEvent.Type;

public class AST_DEC_CLASS extends AST_DEC
{
	/********/
	/* NAME */
	/********/
	public String name;
	public String supername;

	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_CFIELDLIST class_fields;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_CLASS(String name,String supername, AST_CFIELDLIST class_fields)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		this.name = name;
		this.supername = supername;
		this.class_fields = class_fields;
	}

	/*********************************************************/
	/* The printing message for a class declaration AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		System.out.format("CLASS DEC = %s\n",name);
		if (supername != null) System.out.format("EXTENDS %s\n",supername);
		if (class_fields != null) class_fields.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (supername != null) 
			AST_GRAPHVIZ.getInstance().logNode(
					SerialNumber,
					String.format("CLASS\n%s\nEXTENDS\n%s",name,supername));
		else
			AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CLASS\n%s",name));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,class_fields.SerialNumber);		
	}
	
	public TYPE SemantMe()
	{	
		TYPE_CLASS t = null;
		
		/*the class name is used already*/
		if (SYMBOL_TABLE.getInstance().find(name))
			throw new SemanticRuntimeException(lineNum, colNum, String.format
					("name class: %s is already exists in SYMBOL_TABLE\n", name));
			
		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginScope("CLASS");
				
		/*There is no extends*/
		if (supername == null) t = new TYPE_CLASS(null,name,class_fields.SemantMe());
		
		else{
			/*Searching for supername in SYMBOL_TABLE*/
			Type superType = SYMBOL_TABLE.getInstance().find(supername);
			/*Supername is not in SYMBOL_TABLE -> error*/
			if (superType == null)
				throw new SemanticRuntimeException(lineNum, colNum, String.format
						("class %s extends undefine class %s\n", name, supername));
			
			/*Supername is not a class*/
			if (!(superType instanceof TYPE_CLASS))
				throw new SemanticRuntimeException(lineNum, colNum, String.format
						("class %s extends %s of type %s\n", name, supername, superType.getClass()));	
			
			/*Supername is legal*/
			t = new TYPE_CLASS(TYPE_CLASS,name,class_fields.SemantMe());
		}

		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endScope();

		/************************************************/
		/* [4] Enter the Class Type to the Symbol Table */
		/************************************************/
		SYMBOL_TABLE.getInstance().enter(name,t);

		/*********************************************************/
		/* [5] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}
}
