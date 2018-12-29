package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_DATA_MEMBERS_LIST;
import TYPES.TYPE_FUNCTION;

public class AST_VAR_SIMPLE extends AST_VAR
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SIMPLE(String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n",name);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.name = name;
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void PrintMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}
	
	//returns found variable's TYPE
	private TYPE findVarNameInClassAndItsSupers(String varName, TYPE_CLASS curClassSuper) {
		// check that the variable is a field of the calling object's class
		while (curClassSuper != null) {
			for (TYPE_CLASS_DATA_MEMBERS_LIST it = curClassSuper.data_members; it != null; it = it.tail) {
				if (it.head.name.equals(varName)) {
					TYPE fieldWithVarNameType = it.head.type;
					// if I found a field with the function's name, it has to not be a function - else error
					if (fieldWithVarNameType instanceof TYPE_FUNCTION) {
						throw new SemanticRuntimeException(lineNum, colNum, String.format(
								"simple var %s is a function field in class %s\n", varName, curClassSuper.name));
					}
					return it.head.type;
				}
			}
			curClassSuper = curClassSuper.father;
		}
		return null;
	}
	
	public TYPE SemantMe()
	{
		TYPE t = findVarNameInClassAndItsSupers(name,SYMBOL_TABLE.getInstance().curClassExtends);
		if (t == null) //not found, search in global scope
			t = SYMBOL_TABLE.getInstance().findObject(name);
		if (t == null || t instanceof TYPE_FUNCTION)
			throw new SemanticRuntimeException(lineNum, colNum, String.format("(%s) cannot be resolved to a variable\n",name));
		return t;
	}
}
