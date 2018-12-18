package AST;

import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_DATA_MEMBERS_LIST;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_LIST;
import TYPES.TYPE_NIL;

public class AST_EXP_CALL extends AST_EXP
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_VAR callingObject;
	public String funcName;
	public AST_EXP_LIST params;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_CALL(AST_VAR callingObject, String funcName,AST_EXP_LIST params)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		this.callingObject = callingObject;
		this.funcName = funcName;
		this.params = params;
	}

	/************************************************************/
	/* The printing message for a function declaration AST node */
	/************************************************************/
	public void PrintMe()
	{
		/*************************************************/
		/* AST NODE TYPE = AST NODE FUNCTION DECLARATION */
		/*************************************************/
		if (callingObject == null) System.out.format("CALL(%s)\nWITH:...",funcName);
		else System.out.format("CALLER: ...\nCALL(%s)\nWITH:...",funcName);

		/***************************************/
		/* RECURSIVELY PRINT params + body ... */
		/***************************************/
		if (callingObject != null) callingObject.PrintMe();
		if (params != null) params.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		if (callingObject == null) AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("CALL(%s)\nWITH...",funcName));
		else AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("CALLER: ...\nCALL(%s)\nWITH...",funcName));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (callingObject != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,callingObject.SerialNumber);
		if (params != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,params.SerialNumber);		
	}
	
	//returns found function's TYPE_FUNCTION
	private TYPE_FUNCTION findFunctionNameInClassAndItsSupers(String funcName,TYPE_CLASS callingObjectTypeClass)
	{
		//check that the function is a field of the calling object's class
		while (callingObjectTypeClass != null) {
			for (TYPE_CLASS_DATA_MEMBERS_LIST it = callingObjectTypeClass.data_members; it != null; it = it.tail) {
				if (it.head.name.equals(funcName)) {
					TYPE fieldWithFuncNameType = it.head.type;
					//if I found a field with the function's name, it has to be a function - else error
					if (!(fieldWithFuncNameType instanceof TYPE_FUNCTION)) {
						throw new SemanticRuntimeException(lineNum,colNum,String.format("%s is a non-function field in class %s\n",funcName,callingObjectTypeClass.name));
					}
					return ((TYPE_FUNCTION)it.head.type);
				}	
			}
			callingObjectTypeClass = callingObjectTypeClass.father;
		}
		return null;
	}
	
	public TYPE SemantMe() {
		TYPE_LIST listOfGivenParams = (this.params == null) ? null : params.semantMe();
		TYPE_LIST listOfCalledFunctionParams = null;
		TYPE callingObjectType = null;
		TYPE funcReturnType = null;
		TYPE_FUNCTION foundFunctionType = null;
		if (callingObject == null) {
			TYPE t = SYMBOL_TABLE.getInstance().find(funcName);
			
			//if (t == null) //search funcName in superclasses, if exists
			//{
				
			//}
			
			if (t == null) {
				throw new SemanticRuntimeException(lineNum,colNum,String.format("function %s does not exist in scope\n",funcName));
			}
			if (!(t instanceof TYPE_FUNCTION)) {
				throw new SemanticRuntimeException(lineNum,colNum,String.format("%s is not a valid function name in this scope\n",funcName));
			}
			listOfCalledFunctionParams = ((TYPE_FUNCTION)t).params;
			funcReturnType = ((TYPE_FUNCTION)t).returnType;
		}
		else {
			//if SemantMe didn't throw an error, the type is in the table
			callingObjectType = callingObject.SemantMe();
			//if it's not a class - it's an error
			if (!(callingObjectType instanceof TYPE_CLASS)) {
				throw new SemanticRuntimeException(lineNum,colNum,String.format("%s is not a class\n",callingObjectType.name));
			}
			TYPE_CLASS callingObjectTypeClass = (TYPE_CLASS) callingObjectType;
			
			
			
			/*
			//check that the function is a field of the calling object's class
			while (callingObjectTypeClass != null && funcReturnType == null) {
				for (TYPE_CLASS_DATA_MEMBERS_LIST it = callingObjectTypeClass.data_members; it != null; it = it.tail) {
					if (it.head.name.equals(funcName) && funcReturnType == null) {
						TYPE fieldWithFuncNameType = it.head.type;
						//if I found a field with the function's name, it has to be a function - else error
						if (!(fieldWithFuncNameType instanceof TYPE_FUNCTION)) {
							throw new SemanticRuntimeException(lineNum,colNum,String.format("%s is a non-function field in class %s\n",funcName,callingObjectType.name));
						}
						funcReturnType =  ((TYPE_FUNCTION)it.head.type).returnType;
						listOfCalledFunctionParams = ((TYPE_FUNCTION)it.head.type).params;
					}	
				}
				callingObjectTypeClass = callingObjectTypeClass.father;
			}
			*/
			
			foundFunctionType = findFunctionNameInClassAndItsSupers(funcName,callingObjectTypeClass);
			
			if (foundFunctionType != null) {
				listOfCalledFunctionParams = foundFunctionType.params;
				funcReturnType = foundFunctionType.returnType;
			}
			
			if (funcReturnType == null) {
				throw new SemanticRuntimeException(lineNum,colNum,String.format("%s does not have a function %s, nor inherits one\n",callingObjectType.name, funcName));
			}
		}
		compareFunctionsArgsTypes(listOfGivenParams, listOfCalledFunctionParams);
		return funcReturnType;
	}
	
	//t1 is father for t2?
	private boolean isExtends(TYPE_CLASS t1, TYPE_CLASS t2) {
		if (t2 == null) return false;
		if (t1.name.equals(t2.name)) return true;
		TYPE_CLASS tmp = t2.father;
		return isExtends(t1, tmp);
	}
	
	//funcArgsTwo is original function's args list
	//funcArgsOne is the current call
	private void compareFunctionsArgsTypes(TYPE_LIST funcArgsOne,TYPE_LIST funcArgsTwo)
	{
		TYPE_LIST itOne = null,itTwo = null;
		for (itOne = funcArgsOne,itTwo = funcArgsTwo; itOne != null && itTwo != null; itOne = itOne.tail,itTwo = itTwo.tail)
		{
			if (itOne.head.getClass() != itTwo.head.getClass())
			{
				if (itTwo.head instanceof TYPE_ARRAY || itTwo.head instanceof TYPE_CLASS)
				{
					if (itOne.head != TYPE_NIL.getInstance())
						throw new SemanticRuntimeException(lineNum, colNum,
								"Function call args are different from function's expected args\n");
				}
				else 
					throw new SemanticRuntimeException(lineNum, colNum,
						"Function call args are different from function's expected args\n");
			}
			if (itOne.head instanceof TYPE_CLASS) // arg is TYPE_CLASS for both
			{
				if(!isExtends((TYPE_CLASS)itTwo.head,(TYPE_CLASS)itOne.head))
					throw new SemanticRuntimeException(lineNum, colNum,
							"Function call expected specific class and got another class without inheritance relation\n");
			}

			if (itOne.head instanceof TYPE_ARRAY) // arg is TYPE_ARRAY for both
			{
				if (!((TYPE_ARRAY) itOne.head).name.equals(((TYPE_ARRAY) itTwo.head).name))
					throw new SemanticRuntimeException(lineNum, colNum,
							"Function call expected specific arrayType and got another arrayType\n");
			}
		}
		
		if (itOne != null || itTwo !=null)
			throw new SemanticRuntimeException(lineNum, colNum,
					"Function call with different number of args than original function\n");
	}
}
