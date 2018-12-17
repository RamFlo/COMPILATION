package AST;

import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_NIL;

public class AST_DEC_VAR extends AST_DEC
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String type;
	public String name;
	public AST_EXP initialValue;
	public AST_NEWEXP initialValueNew;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_DEC_VAR(String type,String name,AST_EXP initialValue, AST_NEWEXP newInitialValue)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.type = type;
		this.name = name;
		this.initialValue = initialValue;
		this.initialValueNew = newInitialValue;
	}
	

	/********************************************************/
	/* The printing message for a declaration list AST node */
	/********************************************************/
	public void PrintMe()
	{
		/********************************/
		/* AST NODE TYPE = AST DEC LIST */
		/********************************/
		if (initialValue != null) System.out.format("VAR-DEC(%s):%s := initialValue\n",name,type);
		if (initialValueNew != null) System.out.format("VAR-DEC(%s):%s := initialValueNew\n",name,type);
		if (initialValue == null && initialValueNew == null) System.out.format("VAR-DEC(%s):%s\n",name,type);

		/**************************************/
		/* RECURSIVELY PRINT initialValue ... */
		/**************************************/
		if (initialValue != null) initialValue.PrintMe();
		if (initialValueNew != null) initialValueNew.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("VAR\nDEC(%s)\nTYPE(%s)",name,type));

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (initialValue != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,initialValue.SerialNumber);		
		if (initialValueNew != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,initialValueNew.SerialNumber);
			
	}
	//Copied from next exercise
	public TYPE SemantMe()
	{
		TYPE t1 = null;
		TYPE t2 = null;
		
	
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		t1 = SYMBOL_TABLE.getInstance().findDataType(type);
		if (t1 == null)
		{
			throw new SemanticRuntimeException(lineNum,colNum,String.format("non existing type %s\n",type));
		}
		
		
		/**************************************/
		/* [2] Check That Name does NOT exist */
		/**************************************/
		if (SYMBOL_TABLE.getInstance().findInCurrentScope(name) != null) //changed to findInCurrentScope
		{
			throw new SemanticRuntimeException(lineNum,colNum,String.format("variable %s already exists in scope\n",name));
		}
		
		if (this.initialValue != null) t2 =  initialValue.SemantMe();
		if (this.initialValueNew != null) t2 =  initialValueNew.SemantMe();
		
		if (initialValue != null){
			t2 = initialValue.SemantMe();
			
			if (t1.getClass() == t2.getClass()){
				if (t1 instanceof TYPE_CLASS && !isExtends((TYPE_CLASS)t1, (TYPE_CLASS)t2))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=class)var := (type=class)exp (not equal/extends)\n");
				
				if (t1 instanceof TYPE_ARRAY)
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=array)var := (type=array)exp (assign without NEW)\n");					
			}
			
			else{ /*t1.getClass() != t2.getClass()*/
				if (t2 == TYPE_NIL.getInstance() &&
						((!(t1 instanceof TYPE_CLASS)) && (!(t1 instanceof TYPE_ARRAY))))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=int/string)var := (type=nil)exp\n");
				
				if (t2 != TYPE_NIL.getInstance())
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for var := exp\n");	
			}
		}
		
		else if (initialValueNew != null){ /*newExp != null*/
			t2 = initialValueNew.SemantMe();
			
			if (t1.getClass() == t2.getClass()){
				if (t1 instanceof TYPE_CLASS && !isExtends((TYPE_CLASS)t1, (TYPE_CLASS)t2))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=class)var := NEW (type=class)newExp (not equal/extends)\n");
				
				else{/*t1.getclass()==t2.getclass()==TYPE_ARRAY*/					
					if (!((TYPE_ARRAY)t1).arrayTypeString.equals(((TYPE_ARRAY)t2).name))
						throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=TYPE_ARRAY)var := NEW (type=TYPE_ARRAY)newExp (the type is differrent from what was declare)\n");
				}
			}
			else throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for var := NEW newExp\n");
		}

		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enterObject(name,t1);

		/*********************************************************/
		/* [4] Return value is irrelevant for class declarations */
		/*********************************************************/
		return t1;		
	}
	
	private boolean isExtends(TYPE_CLASS t1, TYPE_CLASS t2){
		if (t2 == null) return false;
		if (t1.name.equals(t2.name)) return true;
		TYPE_CLASS tmp = t2.father;
		return isExtends(t1, tmp);
		
	}
}
