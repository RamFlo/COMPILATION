package AST;

import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import IR.IR;
import IR.IRcommand_Store_Word_Offset;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.COUNTERS;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import SYMBOL_TABLE.ENUM_OBJECT_CONTEXT.ObjectContext;
import SYMBOL_TABLE.ENUM_SCOPE_TYPES.ScopeTypes;
import TYPES.TYPE;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_INT;
import TYPES.TYPE_NIL;
import TYPES.TYPE_STRING;

public class AST_DEC_VAR extends AST_DEC
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public String type;
	public String name;
	public AST_EXP initialValue;
	public AST_NEWEXP initialValueNew;
	//public int indexOfVarInFunction = -1;
	
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
	
	private void updateObjectContextAndIndex() {
		this.objScopeType = SYMBOL_TABLE.getInstance().curScopeType;
		if (this.objScopeType == ScopeTypes.classMethodScope || this.objScopeType == ScopeTypes.globalFunctionScope) {
			this.objContext = ObjectContext.local;
			this.objIndexInContext = COUNTERS.funcLocalVariables;
			COUNTERS.funcLocalVariables++;
		}

		else if (this.objScopeType == ScopeTypes.classScope) {
			this.objContext = ObjectContext.classDataMember;
			this.objIndexInContext = COUNTERS.classDataMember;
			COUNTERS.classDataMember++;
		} else // global scope
		{
			this.objContext = ObjectContext.global;
			this.objIndexInContext = -1;
		}
	}
	
	public TYPE SemantMeFromClass()
	{
		TYPE t1 = null;
		//TYPE t2 = null;
		TYPE existingNamesType = null;
		
		this.updateObjectContextAndIndex();
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		SYMBOL_TABLE_ENTRY searchRes = SYMBOL_TABLE.getInstance().findDataType(type);
		if (searchRes == null)
		{
			throw new SemanticRuntimeException(lineNum,colNum,String.format("non existing type %s\n",type));
		}
		t1 = searchRes.type;
		/***************************************************/
		/* [2] Check That Name is not an existing dataType */
		/***************************************************/
		if (SYMBOL_TABLE.getInstance().findDataType(name) != null  || name.equals("void")) //changed to findInCurrentScope
			throw new SemanticRuntimeException(lineNum,colNum,String.format("variable %s is an existing dataType or void\n",name));
		
		/**************************************/
		/* [3] Check That Name does NOT exist */
		/**************************************/
		if ((existingNamesType = SYMBOL_TABLE.getInstance().findInCurrentScope(name)) != null) //changed to findInCurrentScope
		{
			//if (!(existingNamesType instanceof TYPE_FUNCTION))
				throw new SemanticRuntimeException(lineNum,colNum,String.format("variable %s already exists in scope\n",name));
		}
		
		if (initialValueNew != null)
			throw new SemanticRuntimeException(lineNum,colNum,"attempt to use NEW exp for class member decleration\n");
		
		if (initialValue != null)
		{
			if (t1 instanceof TYPE_ARRAY || t1 instanceof TYPE_CLASS)
			{
				if (!(initialValue instanceof AST_EXP_NIL))
					throw new SemanticRuntimeException(lineNum,colNum,"non-nil initialValue for TYPE_ARRAY or TYPE_CLASS class variable\n");
			}
			if (t1 == TYPE_INT.getInstance())
			{
				if (!(initialValue instanceof AST_EXP_INT))
					throw new SemanticRuntimeException(lineNum,colNum,"non constant int initialValue for TYPE_INT class variable\n");
			}
			if (t1 == TYPE_STRING.getInstance())
			{
				if (!(initialValue instanceof AST_EXP_STRING))
					throw new SemanticRuntimeException(lineNum,colNum,"non constant string initialValue for TYPE_STRING class variable\n");
			}
		}
		
		/***************************************************/
		/* [4] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enterObject(name,t1,this);
		
		
		return t1;
	}
	
	
//	boolean varDecIsInFunction()
//	{
//		return (SYMBOL_TABLE.getInstance().curFunctionReturnType != null);
//	}

	public TYPE SemantMe()
	{
		TYPE t1 = null;
		TYPE t2 = null;
		TYPE existingNamesType = null;
		
		this.updateObjectContextAndIndex();
		
//		if (varDecIsInFunction())
//		{
//			SYMBOL_TABLE.getInstance().curIndexOfVarInFunction++;
//			this.indexOfVarInFunction = SYMBOL_TABLE.getInstance().curIndexOfVarInFunction;
//		}
		
	
		/****************************/
		/* [1] Check If Type exists */
		/****************************/
		SYMBOL_TABLE_ENTRY searchRes = SYMBOL_TABLE.getInstance().findDataType(type);
		if (searchRes == null)
		{
			throw new SemanticRuntimeException(lineNum,colNum,String.format("non existing type %s\n",type));
		}
		t1 = searchRes.type;
		/***************************************************/
		/* [2] Check That Name is not an existing dataType */
		/***************************************************/
		if (SYMBOL_TABLE.getInstance().findDataType(name) != null  || name.equals("void")) //changed to findInCurrentScope
			throw new SemanticRuntimeException(lineNum,colNum,String.format("variable %s is an existing dataType or void\n",name));
		
		/**************************************/
		/* [3] Check That Name does NOT exist */
		/**************************************/
		if ((existingNamesType = SYMBOL_TABLE.getInstance().findInCurrentScope(name)) != null) //changed to findInCurrentScope
		{
			//if (!(existingNamesType instanceof TYPE_FUNCTION))
				throw new SemanticRuntimeException(lineNum,colNum,String.format("variable %s already exists in scope\n",name));
		}
			
		
		
		
		
		if (this.initialValue != null) t2 =  initialValue.SemantMe();
		if (this.initialValueNew != null) t2 =  initialValueNew.SemantMe();
		
		if (initialValue != null){
			t2 = initialValue.SemantMe();
			
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
		
		else if (initialValueNew != null){ /*newExp != null*/
			t2 = initialValueNew.SemantMe();
			
			if (t1.getClass() == t2.getClass()){
//				System.out.println(String.format("t1.getClass()=%s,t2.getClass()=%s",t1.getClass().getName(),t2.getClass().getName() ));
//				if (t1 instanceof TYPE_CLASS)
//					System.out.println("t1 instanceof TYPE_CLASS");

				if (t1 instanceof TYPE_CLASS && !isExtends((TYPE_CLASS)t1, (TYPE_CLASS)t2))
					throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=class)var := NEW (type=class)newExp (not equal/extends)\n");
				
				else if (t1 instanceof TYPE_ARRAY){ /*t1.getclass()==t2.getclass()==TYPE_ARRAY*/					
					if (!((TYPE_ARRAY)t1).arrayTypeString.equals(((TYPE_ARRAY)t2).arrayTypeString))
					{
						//System.out.println(String.format("t1 arrayTypeName: %s, t2 arrayTypeName: %s", ((TYPE_ARRAY)t1).name),((TYPE_ARRAY)t2).name)));
						throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for (type=TYPE_ARRAY)var := NEW (type=TYPE_ARRAY)newExp (the type is differrent from what was declare)\n");
						
					}
				}
			}
			else throw new SemanticRuntimeException(lineNum, colNum, "type mismatch for var := NEW newExp\n");
		}

		/***************************************************/
		/* [3] Enter the Function Type to the Symbol Table */
		/***************************************************/
		SYMBOL_TABLE.getInstance().enterObject(name,t1,this);

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
	
	public TEMP IRmeFromClass(int offset,TEMP allocatedAddress)
	{
		//(initialValueNew is always null in this case - no need to check)
		
		if (initialValue == null)
			return null; //no initial value for this data member!
		
		TEMP initialValueTemp = initialValue.IRme();
		IR.getInstance().Add_codeSegmentIRcommand(new IRcommand_Store_Word_Offset(initialValueTemp,offset,allocatedAddress));
		
		return null;
	}
}
