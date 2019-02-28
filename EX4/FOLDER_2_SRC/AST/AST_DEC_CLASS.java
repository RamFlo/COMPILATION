package AST;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import IR.IR;
import IR.IRcommand;
import IR.IRcommand_Create_Class_VFTable;
import IR.IRcommand_Jump_If_Eq_To_Zero;
import IR.IRcommand_Label;
import MyClasses.ClassMethodDetails;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import TEMP.TEMP;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_DATA_MEMBER;
import TYPES.TYPE_CLASS_DATA_MEMBERS_LIST;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_LIST;

public class AST_DEC_CLASS extends AST_DEC
{
	/********/
	/* NAME */
	/********/
	public String name;
	public String supername;
	public TYPE_CLASS classInfo;

	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_CFIELDLIST class_fields;
	
	//private List<String[]> listOfFunctionsForClassVFTable;
	
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
	
	private void compareFunctionsArgsTypes(TYPE_LIST funcArgsOne,TYPE_LIST funcArgsTwo)
	{
		TYPE_LIST itOne = null,itTwo = null;
		for (itOne = funcArgsOne,itTwo = funcArgsTwo; itOne != null && itTwo != null; itOne = itOne.tail,itTwo = itTwo.tail)
		{
			if (itOne.head.getClass() != itTwo.head.getClass())
				throw new SemanticRuntimeException(lineNum, colNum,
						"Class method is overloading a superclass's method with different argument's type\n");

			if (itOne.head instanceof TYPE_CLASS) // arg is TYPE_CLASS for both
			{
				if (!((TYPE_CLASS) itOne.head).name.equals(((TYPE_CLASS) itTwo.head).name))
					throw new SemanticRuntimeException(lineNum, colNum,
							"Class method is overloading a superclass's method with different argument's type (TYPE_CLASS)\n");
			}

			if (itOne.head instanceof TYPE_ARRAY) // arg is TYPE_ARRAY for both
			{
				if (!((TYPE_ARRAY) itOne.head).name.equals(((TYPE_ARRAY) itTwo.head).name))
					throw new SemanticRuntimeException(lineNum, colNum,
							"Class method is overloading a superclass's method with different argument's type (TYPE_ARRAY)\n");
			}
		}
		
		if (itOne != null || itTwo !=null)
			throw new SemanticRuntimeException(lineNum, colNum,
					"Class method is overloading a superclass's method with different number of arguments\n");
	}
	
	
	private void doesFunctionOverloadProperly(TYPE_FUNCTION curFunction, TYPE_CLASS superType, int funcLineNum, int funcColNum)
	{
		if (superType == null)
			return;
		
		for (TYPE_CLASS_DATA_MEMBERS_LIST it = superType.data_members; it  != null; it = it.tail)
		{
			if (!it.head.name.equals(curFunction.name))
				continue;
			//Assumption: declaring a function with the same name as declared variable in father is illegal
			
			if (!(it.head.type instanceof TYPE_FUNCTION))
				throw new SemanticRuntimeException(funcLineNum, funcColNum,
						"Class method is shadowing a superclass's variable\n");

			TYPE_FUNCTION superFunc = (TYPE_FUNCTION) it.head.type;
			if (superFunc.returnType.getClass() != curFunction.returnType.getClass())
				throw new SemanticRuntimeException(funcLineNum, funcColNum,
						"Class method overloading a superclass's method with different return type\n");

			if (superFunc.returnType instanceof TYPE_CLASS) // return type is
															// TYPE_CLASS for
															// both
			{
				if (!((TYPE_CLASS) superFunc.returnType).name.equals(((TYPE_CLASS) curFunction.returnType).name))
					throw new SemanticRuntimeException(funcLineNum, funcColNum,
							"Class method overloading a superclass's method with different return type (TYPE_CLASS)\n");
			}

			if (superFunc.returnType instanceof TYPE_ARRAY) // return type is
															// TYPE_ARRAY for
															// both
			{
				if (!((TYPE_ARRAY) superFunc.returnType).name.equals(((TYPE_ARRAY) curFunction.returnType).name))
					throw new SemanticRuntimeException(funcLineNum, funcColNum,
							"Class method overloading a superclass's method with different return type (TYPE_ARRAY)\n");
			}
			
			//compare functions' args list
			compareFunctionsArgsTypes(superFunc.params,curFunction.params);
			
			return; //found overloaded super class's method, no need to continue
		}
		doesFunctionOverloadProperly(curFunction,superType.father,funcLineNum, funcColNum);
	}
	
	
	private void doesVariableShadow(String varName, TYPE_CLASS superType, int varLineNum, int varColNum)
	{
		if (superType == null)
			return;
		
		for (TYPE_CLASS_DATA_MEMBERS_LIST it = superType.data_members; it  != null; it = it.tail)
		{
			if (it.head.name.equals(varName))
				throw new SemanticRuntimeException(varLineNum, varColNum,
						"Class variable is shadowing a superclass variable or method\n");
		}
		
		doesVariableShadow(varName,superType.father,varLineNum, varColNum);
	}
	
//	private void cloneFathersListOfFunctionsForVFTable(TYPE_CLASS curClass, TYPE_CLASS superClass) {
//		curClass.listOfFunctionsForClassVFTable = new LinkedList<String[]>();
//		if (superClass != null) {
//			for (String[] superClassEntry : superClass.listOfFunctionsForClassVFTable) {
//				String[] curClassEntry = new String[2];
//				curClassEntry[0] = superClassEntry[0];
//				curClassEntry[1] = superClassEntry[1];
//				curClass.listOfFunctionsForClassVFTable.add(curClassEntry);
//			}
//		}
//	}
	
//	private int findAndUpdateOrInsertToVFTableList(String funcName, TYPE_CLASS curClass) {
//		int indexInVFTable = 1;
//		for (String[] VFTableFuncEntry : curClass.listOfFunctionsForClassVFTable) {
//			if (VFTableFuncEntry[1].equals(funcName)) {
//				VFTableFuncEntry[0] = curClass.name;
//				return indexInVFTable;
//			}
//			indexInVFTable++;
//		}
//		String[] newVFTableListEntry = new String[2];
//		newVFTableListEntry[0] = curClass.name;
//		newVFTableListEntry[1] = funcName;
//		curClass.listOfFunctionsForClassVFTable.add(newVFTableListEntry);
//		return curClass.listOfFunctionsForClassVFTable.size();
//	}
	
	private int findAndUpdateOrInsertToMethodMap(String methodName, TYPE_CLASS curClass) {
		int methodIndex;
		if (curClass.methodsMap.containsKey(methodName))
			methodIndex = curClass.methodsMap.get(methodName).methodIndex;
		else
			methodIndex = curClass.methodsMap.size() + 1;
		
		curClass.methodsMap.put(methodName, new ClassMethodDetails(methodIndex, curClass.name));
		return methodIndex;
	}
	
	private void insertToDataMembersMap(String dataMemberName, TYPE_CLASS curClass) {
		int dataMemberIndex = curClass.dataMembersMap.size() + 1;
		curClass.dataMembersMap.put(dataMemberName,dataMemberIndex);
	}
	
	
	public TYPE SemantMe()
	{	
		TYPE_CLASS t = null;
		TYPE superType = null;
		/*the class name is used already*/
		if (SYMBOL_TABLE.getInstance().find(name) != null)
			throw new SemanticRuntimeException(lineNum, colNum, String.format
					("name class: %s is already exists in SYMBOL_TABLE\n", name));
		
		if (name.equals("void"))
			throw new SemanticRuntimeException(lineNum, colNum, String.format
					("class name cannot be (void)\n", name));
			

		/*There is no extends*/
		//if (supername == null) t = new TYPE_CLASS(null,name,class_fields.SemantMe());
		
		if (supername != null){
			/*Searching for supername in SYMBOL_TABLE*/
			SYMBOL_TABLE_ENTRY searchRes = SYMBOL_TABLE.getInstance().findDataType(supername);
			/*Supername is not in SYMBOL_TABLE -> error*/
			if (searchRes == null)
				throw new SemanticRuntimeException(lineNum, colNum, String.format
						("class %s extends undefined class %s\n", name, supername));
			superType = searchRes.type;
			
			/*Supername is not a class*/
			if (!(superType instanceof TYPE_CLASS))
				throw new SemanticRuntimeException(lineNum, colNum, String.format
						("class %s extends %s of type %s\n", name, supername, superType.getClass()));
			
		}
		
		/**********************************************************************/
		/* [0] Create TYPE_CLASS with dataMembersList=null
		 * 		 and insert the class to the symbol table */
		/**********************************************************************/		
		
		t = new TYPE_CLASS((TYPE_CLASS)superType,name,null);
		t.classOriginASTNode = this;
		this.classInfo = t;
		if(superType != null) //copy entire super's dataMembersMap and methodMap!
		{
			t.dataMembersMap = new LinkedHashMap<String,Integer>(((TYPE_CLASS)superType).dataMembersMap);
			t.methodsMap = new LinkedHashMap<String,ClassMethodDetails>(((TYPE_CLASS)superType).methodsMap);
		}
		SYMBOL_TABLE.getInstance().enterDataType(name,t,this);
		
		/*************************/
		/* [1] Begin Class Scope */
		/*************************/
		SYMBOL_TABLE.getInstance().beginClassScope("CLASS", (TYPE_CLASS)superType);
		SYMBOL_TABLE.getInstance().curClass = t;
		
		
		
		/**********************************************************************/
		/* [1] Semant data members (type only) and populate data members list (without functions' bodies) */
		/**********************************************************************/
		
		AST_DEC_FUNC curHeadFunc;
		AST_DEC_VAR curHeadVar;
		
		int funcIndexInCurClass;

		//cloneFathersListOfFunctionsForVFTable(t, (TYPE_CLASS) superType);
		
		
		TYPE_FUNCTION curFunction = null;
		TYPE curVariant = null;
		//TYPE_CLASS_DATA_MEMBERS_LIST dataMembersList = null;
		
		/*************************************************************************************/
		/* [0] Semant data members and functions (without the functions' bodies\param names) */
		/*************************************************************************************/
		for (AST_CFIELDLIST it = class_fields; it  != null; it = it.tail)
		{
			curHeadFunc = it.headFunc;
			curHeadVar = it.headVar;
			
			if (curHeadFunc != null)
				{
					funcIndexInCurClass = findAndUpdateOrInsertToMethodMap(curHeadFunc.name,t);
					curFunction = (TYPE_FUNCTION) curHeadFunc.SemantFuncSignatureAndParamTypes(funcIndexInCurClass);
					t.data_members = new TYPE_CLASS_DATA_MEMBERS_LIST(new TYPE_CLASS_DATA_MEMBER(curFunction,curFunction.name),t.data_members);
					doesFunctionOverloadProperly(curFunction,((TYPE_CLASS)superType),curHeadFunc.lineNum,curHeadFunc.colNum);
				}
			if (curHeadVar != null)
				{
					curVariant = curHeadVar.SemantMeFromClass();
					t.data_members = new TYPE_CLASS_DATA_MEMBERS_LIST(new TYPE_CLASS_DATA_MEMBER(curVariant,curHeadVar.name),t.data_members);
					doesVariableShadow(curHeadVar.name,((TYPE_CLASS)superType),curHeadVar.lineNum,curHeadVar.colNum);
					insertToDataMembersMap(curHeadVar.name,t);
				}
		}
		
		//this.listOfFunctionsForClassVFTable = t.listOfFunctionsForClassVFTable;
		
		/*************************/
		/* [2] Semant functions' bodies */
		/*************************/
		for (AST_CFIELDLIST it = class_fields; it  != null; it = it.tail)
		{
			curHeadFunc = it.headFunc;
			if (curHeadFunc != null)
					curHeadFunc.SemantFuncParamNamesAndBody();
		}
		/*****************/
		/* [3] End Scope */
		/*****************/
		SYMBOL_TABLE.getInstance().endClassScope();

		/*********************************************************/
		/* [5] Return value is irrelevant for class declarations */
		/*********************************************************/
		return null;		
	}
	
	public TEMP IRme()
	{
		/**************************************/
		/* [1] create VFTable in data segment */
		/**************************************/
		IR.getInstance().Add_dataSegmentIRcommand(new IRcommand_Create_Class_VFTable(this.classInfo.methodsMap,this.name));		
	
		/****************************************/
		/* [2] add methods code to code segment */
		/****************************************/
		AST_DEC_FUNC curHeadFunc;
		for (AST_CFIELDLIST it = class_fields; it  != null; it = it.tail)
		{
			curHeadFunc = it.headFunc;
			if (curHeadFunc != null)
				curHeadFunc.IRmeFromClass(this.name); //TO-DO: make sure DEC_FUNC differentiates methods from functions
		}
		
		//data members IRMe happens in NEWEXP! not here!
		
		return null;
	}
}
