package AST;

import IR.IR;
import IR.IRcommand;
import IR.IRcommand_Allocate_On_Stack;
import IR.IRcommand_BNEZ;
import IR.IRcommand_Dealloc_Stack;
import IR.IRcommand_Exit;
import IR.IRcommand_Frame_Load;
import IR.IRcommand_JAL_Label;
import IR.IRcommand_Jump_Label;
import IR.IRcommand_Jump_Reg;
import IR.IRcommand_Label;
import IR.IRcommand_Load;
import IR.IRcommand_Load_Address;
import IR.IRcommand_Load_To_Reg_Stack;
import IR.IRcommand_Move_From_v0;
import IR.IRcommand_PrintInt;
import IR.IRcommand_Print_String_By_Address;
import IR.IRcommand_Print_Trace;
import IR.IRcommand_Stack_Load;
import IR.IRcommand_Store_Reg_On_Stack_Offset;
import IR.IRcommand_Store_Word_Stack_Offset;
import IR.IRcommand_String_Creation;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.ENUM_SCOPE_TYPES.ScopeTypes;
import SYMBOL_TABLE.COUNTERS;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_DATA_MEMBERS_LIST;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_LIST;
import TYPES.TYPE_NIL;
import TYPES.TYPE_VOID;

public class AST_EXP_CALL extends AST_EXP
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_VAR callingObject;
	public String funcName;
	public AST_EXP_LIST params;
	
	public TYPE_CLASS curClassName = null;
	public TYPE_CLASS callingObjectClassName = null;
	
	public boolean retValue = false;
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
		TYPE foundFunctionType = null;
		this.curClassName = SYMBOL_TABLE.getInstance().curClass;
		this.objScopeType = SYMBOL_TABLE.getInstance().curScopeType;
		if (callingObject == null) {
			foundFunctionType = findFunctionNameInClassAndItsSupers(funcName,SYMBOL_TABLE.getInstance().curClassExtends);
			
			if (foundFunctionType == null){ //search funcName in global scope when not found in class scope
				SYMBOL_TABLE_ENTRY searchRes = SYMBOL_TABLE.getInstance().find(funcName);
				if (searchRes == null) {
					throw new SemanticRuntimeException(lineNum,colNum,String.format("function %s does not exist in scope\n",funcName));
				}
				foundFunctionType = searchRes.type;
			}
			
			if (!(foundFunctionType instanceof TYPE_FUNCTION)) {
				throw new SemanticRuntimeException(lineNum,colNum,String.format("%s is not a valid function name in this scope\n",funcName));
			}
			listOfCalledFunctionParams = ((TYPE_FUNCTION)foundFunctionType).params;
			funcReturnType = ((TYPE_FUNCTION)foundFunctionType).returnType;
		}
		else {
			//if SemantMe didn't throw an error, the type is in the table
			callingObjectType = callingObject.SemantMe();
			//if it's not a class - it's an error
			if (!(callingObjectType instanceof TYPE_CLASS)) {
				throw new SemanticRuntimeException(lineNum,colNum,String.format("%s is not a class\n",callingObjectType.name));
			}
			TYPE_CLASS callingObjectTypeClass = (TYPE_CLASS) callingObjectType;
			
			this.callingObjectClassName = callingObjectTypeClass;
			
			foundFunctionType = findFunctionNameInClassAndItsSupers(funcName,callingObjectTypeClass);
			
			if (foundFunctionType != null) {
				listOfCalledFunctionParams = ((TYPE_FUNCTION)foundFunctionType).params;
				funcReturnType = ((TYPE_FUNCTION)foundFunctionType).returnType;
			}
			
			if (funcReturnType == null) {
				throw new SemanticRuntimeException(lineNum,colNum,String.format("%s does not have a function %s, nor inherits one\n",callingObjectType.name, funcName));
			}
		}
		compareFunctionsArgsTypes(listOfGivenParams, listOfCalledFunctionParams);
		
		if (funcReturnType != TYPE_VOID.getInstance())
			this.retValue = true;
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


	private void saveAllRegistersOnStack()
	{
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(8));
		for(int i=0;i<8;i++)
		{
			String curReg = String.format("t%d", i);
			int curOffset = i*IR.getInstance().WORD_SIZE;
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Reg_On_Stack_Offset(curReg,curOffset));
		}
	}
	
	private int countParamNum()
	{
		int i = 0;
		for (AST_EXP_LIST it = this.params; it  != null; it = it.tail)
			i++;
		return i;
	}
	
	// moved to dec func
//	private void createFuncNameStringAndPushToStack()
//	{
//		// create string in data segment
//		IR.getInstance().Add_dataSegmentIRcommand(new IRcommand_String_Creation(this.funcName, COUNTERS.stringCounter));
//
//		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
//		
//		// load string address (by it's label) into temp t
//		IR.getInstance().Add_currentListIRcommand(
//				new IRcommand_Load_Address(String.format("string_%d", COUNTERS.stringCounter), t));
//
//		// increment string counter
//		COUNTERS.stringCounter++;
//		
//		// allocate space for funcname string address on stack
//		IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(1));
//						
//		// save string address on stack
//		IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Word_Stack_Offset(t,0));
//	}
	
	
	private void pushReturnAddressAndFuncNameToStack()
	{
		// allocate space for ra on stack
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(1));
				
		// save ra on stack
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Reg_On_Stack_Offset("ra",0));
				
		//createFuncNameStringAndPushToStack(); //moved to dec func
	}
	
	private void checkNullPtrDeref(TEMP t) {

		String label_not_null = IRcommand.getFreshLabel("not_null_var");

		IR.getInstance().Add_currentListIRcommand(new IRcommand_BNEZ(t, label_not_null));

		String label_nullp_exception = "string_invalid_ptr_dref";

		TEMP nullp_exception_str_address = TEMP_FACTORY.getInstance().getFreshTEMP();

		IR.getInstance().Add_currentListIRcommand(
				new IRcommand_Load_Address(label_nullp_exception, nullp_exception_str_address));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Print_String_By_Address(nullp_exception_str_address));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Exit());

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_not_null));
	}
	
	private void afterReturnCode(int paramNum)
	{
		// pop callee's name - moved to return!
		//IR.getInstance().Add_currentListIRcommand(new IRcommand_Dealloc_Stack(1));
		
		// load prev ra from stack and pop
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_To_Reg_Stack("ra",0));
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Dealloc_Stack(1));
		
		//deallocate params on stack
		if (paramNum != 0)
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Dealloc_Stack(paramNum));
		
		// load registers values
		for(int i=0;i<8;i++)
		{
			String curReg = String.format("t%d", i);
			int curOffset = i*IR.getInstance().WORD_SIZE;
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_To_Reg_Stack(curReg,curOffset));
		}
		
		// deallocate registers place on stack
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Dealloc_Stack(8));
	}
	
	private boolean isGlobalCall()
	{
		if (this.callingObject == null)
		{
			if (this.objScopeType == ScopeTypes.classMethodScope && this.curClassName.methodsMap.containsKey(this.funcName))
				return false;
			else
				return true;
		}
		return false;
	}
	
	public TEMP IRme()
	{
		if (this.isGlobalCall())
		{
			if (this.funcName.equals("PrintInt"))
			{
				TEMP intToPrint = this.params.head.IRme();
				IR.getInstance().Add_currentListIRcommand(new IRcommand_PrintInt(intToPrint));
				return null;
			}
			if (this.funcName.equals("PrintString"))
			{
				TEMP stringToPrint = this.params.head.IRme();
				IR.getInstance().Add_currentListIRcommand(new IRcommand_Print_String_By_Address(stringToPrint));
				return null;
			}
			if (this.funcName.equals("PrintTrace"))
			{
				IR.getInstance().Add_currentListIRcommand(new IRcommand_Print_Trace());
				return null;
			}
				
		}
		
		this.saveAllRegistersOnStack();
		
		int paramNum = countParamNum();
		
		//allocate space for params on stack
		if (paramNum != 0)
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(paramNum));
		
		
		// push params to stack
		TEMP curParam;
		int i = 0;
		
		for (AST_EXP_LIST it = this.params; it  != null; it = it.tail)
		{
			curParam = it.head.IRme();
			
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Word_Stack_Offset(curParam,i));
			
			i += IR.getInstance().WORD_SIZE;
		}
		
		// t should eventually contain function address to jump to
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		if (this.callingObject == null)
		{
			if (this.objScopeType == ScopeTypes.classMethodScope && this.curClassName.methodsMap.containsKey(this.funcName))
			{
					int offset = IR.getInstance().WORD_SIZE * this.curClassName.methodsMap.get(this.funcName).methodIndex;
					
					// classObj is the first method's parameter: fp+12
					IR.getInstance().Add_currentListIRcommand(new IRcommand_Frame_Load(t, 12));
					
					// push t (hidden clasObj) to stack
					// allocate space for hiddenClassObj on stack
					IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(1));
					IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Word_Stack_Offset(t,0));

					// load vftable's address into t
					IR.getInstance().Add_currentListIRcommand(new IRcommand_Load(t, t, 0));
					
					// load object's function address into t
					IR.getInstance().Add_currentListIRcommand(new IRcommand_Load(t, t, offset));
					
					pushReturnAddressAndFuncNameToStack();
					
					// jump and link!
					IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_Reg(t));
			}
			else //global function!
			{
				pushReturnAddressAndFuncNameToStack();
				
				// jump and link!
				String funcLabel = String.format("global_function_%s", this.funcName);
				IR.getInstance().Add_currentListIRcommand(new IRcommand_JAL_Label(funcLabel));
			}
		}
		else
		{
			TEMP t2 = this.callingObject.IRme();
			
			checkNullPtrDeref(t2);
			
			// push classObj (hidden clasObj) to stack
			// allocate space for hiddenClassObj on stack
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(1));
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Word_Stack_Offset(t2,0));
			
			int offset2 = IR.getInstance().WORD_SIZE * this.callingObjectClassName.methodsMap.get(this.funcName).methodIndex;
			
			// load vftable's address into t2
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Load(t2, t2, 0));
			
			// load object's function address into t2
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Load(t2, t2, offset2));
			
			pushReturnAddressAndFuncNameToStack();
			
			// jump and link!
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Jump_Reg(t2));
		}
		
		//after return code	
		afterReturnCode(paramNum);
		
		// move return val into t (if not void function) and return it as call's value
		if (this.retValue)
		{
			//put retval in t
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Move_From_v0(t));
			return t;
		}
		
		//no ret val!
		return null;
	}
}
