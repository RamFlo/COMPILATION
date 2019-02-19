package AST;

import java.util.Map;

import IR.IR;
import IR.IRcommand_Create_Class_VFTable;
import IR.IRcommand_Load_Address;
import IR.IRcommand_Malloc;
import IR.IRcommand_Shiftleft;
import IR.IRcommand_Store_Word_Offset;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_CLASS;
import TYPES.TYPE_CLASS_DATA_MEMBER;
import TYPES.TYPE_CLASS_DATA_MEMBERS_LIST;
import TYPES.TYPE_FUNCTION;
import TYPES.TYPE_INT;
import TYPES.TYPE_LIST;

public class AST_NEWEXP extends AST_Node{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public AST_EXP e;
	public String type;
	
	private SYMBOL_TABLE_ENTRY classTypeEntry = null;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_NEWEXP( String type, AST_EXP e)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		if (e != null) System.out.format("NEW %s(exp)\n", type);
		else System.out.format("NEW %s\n", type);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.e = e;
		this.type = type;
	}

	/******************************************************/
	/* The printing message for a statement list AST node */
	/******************************************************/
	public void PrintMe()
	{
		/**************************************/
		/* AST NODE TYPE = AST NEWEXP */
		/**************************************/
		System.out.print("AST NODE NEWEXP\n");

		/*************************************/
		/* RECURSIVELY PRINT HEAD + TAIL ... */
		/*************************************/
		if (e != null) e.PrintMe();

		/**********************************/
		/* PRINT to AST GRAPHVIZ DOT file */
		/**********************************/
		if (e != null) AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NEW %s(exp...)\n", type));
		else AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("NEW %s\n", type));
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (e != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,e.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		SYMBOL_TABLE_ENTRY searchRes = SYMBOL_TABLE.getInstance().findDataType(type);
		if (searchRes == null)
			throw new SemanticRuntimeException(lineNum, colNum, String.format("non existing type (%s) for (NEWEXP)\n", type));
		TYPE t = searchRes.type;
		/*******************************************************/
		/* when e == null, NEWEXP should be: 'NEW <CLASSNAME>' */
		/*******************************************************/
		//allow all types not including primitive types
		if (e == null)
		{
			if (type.equals("int") || type.equals("string"))
				throw new SemanticRuntimeException(lineNum, colNum, String.format("an attempt to use 'NEW <CLASSNAME>' with primitive type (%s)\n", type));
			this.classTypeEntry = searchRes;
			return t;
		}
		/******************************************************************/
		/* when e != null, NEWEXP should be: 'NEW <TYPE>[<integral exp>]' */
		/******************************************************************/
		//allow all existing types
		//when encountring TYPE_ARRAY in AST_STMT_ASSIGN, should check if both sides are TYPE_ARRAY (or right side = nil) and also if both sides have the same type
		if (e.SemantMe() != TYPE_INT.getInstance())
			throw new SemanticRuntimeException(lineNum, colNum, "expression (exp) of 'NEW <TYPE>[<exp>]' is not an integral type\n");
		return new TYPE_ARRAY(null,t,type); //anonymous array
	}
	
	private void initializeDataMembersIR(Map<String, Integer> dataMembersMap, TYPE_CLASS classToInitialize,TEMP allocatedAddress) {
		if (classToInitialize == null)
			return;
		initializeDataMembersIR(dataMembersMap, classToInitialize.father, allocatedAddress);
		AST_DEC_VAR curHeadVar;
		for (AST_CFIELDLIST it = classToInitialize.classOriginASTNode.class_fields; it != null; it = it.tail) {
			curHeadVar = it.headVar;
			if (curHeadVar != null) {
				int offset = IR.getInstance().WORD_SIZE * dataMembersMap.get(curHeadVar.name);
				curHeadVar.IRmeFromClass(offset, allocatedAddress);
			}
		}
	}
	
	public TEMP IRme() {
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		/********************/
		/* [1] class NEWEXP */
		/********************/
		if (e == null) {
			TYPE_CLASS classToInstantiate = (TYPE_CLASS) this.classTypeEntry.type;
			AST_CFIELDLIST class_fields = ((AST_DEC_CLASS) this.classTypeEntry.entryOriginASTNode).class_fields;
			int dataMembersNum = classToInstantiate.dataMembersMap.size();

			// malloc dataMembersNum + 1 words for new class data members and vftable address
			IR.getInstance().Add_codeSegmentIRcommand(new IRcommand_Malloc(dataMembersNum + 1, t));

			// load VFTable address into a new TEMP
			TEMP vftable_address = TEMP_FACTORY.getInstance().getFreshTEMP();
			String vftable_label = String.format("VFTable_%s", classToInstantiate.name);
			IR.getInstance().Add_codeSegmentIRcommand(new IRcommand_Load_Address(vftable_label, vftable_address));

			// insert (store word) VFTable address to first cell in allocated space
			IR.getInstance().Add_codeSegmentIRcommand(new IRcommand_Store_Word_Offset(vftable_address, 0, t));

			// initialize data members
			initializeDataMembersIR(classToInstantiate.dataMembersMap, classToInstantiate, t);
		}

		/********************/
		/* [2] Array NEWEXP */
		/********************/
		else {
			TEMP arraySizeTemp = e.IRme();

			// multiply required size by 4
			IR.getInstance().Add_codeSegmentIRcommand(new IRcommand_Shiftleft(arraySizeTemp, arraySizeTemp, 2));
			// allocate space, save address of allocated space in t
			IR.getInstance().Add_codeSegmentIRcommand(new IRcommand_Malloc(arraySizeTemp, t));
		}
		return t;
	}
	
}
