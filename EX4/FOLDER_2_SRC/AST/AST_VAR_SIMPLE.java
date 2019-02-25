package AST;

import IR.IR;
import IR.IRcommand_Add_Immediate;
import IR.IRcommand_Add_Immediate_To_FP;
import IR.IRcommand_Frame_Load;
import IR.IRcommand_Load;
import IR.IRcommand_Load_Address;
import IR.IRcommand_Move;
import MyExceptions.SemanticRuntimeException;
import SYMBOL_TABLE.SYMBOL_TABLE;
import SYMBOL_TABLE.SYMBOL_TABLE_ENTRY;
import SYMBOL_TABLE.ENUM_OBJECT_CONTEXT.ObjectContext;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
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
		SYMBOL_TABLE_ENTRY searchRes = null;
		TYPE t = findVarNameInClassAndItsSupers(name,SYMBOL_TABLE.getInstance().curClassExtends);
		if (t != null) //found object in class's supers!
		{
			this.objIndexInContext = SYMBOL_TABLE.getInstance().curClassExtends.dataMembersMap.get(name);
			this.objScopeType = SYMBOL_TABLE.getInstance().curScopeType;
			this.objContext = ObjectContext.classDataMember;
		}
		else //not found, search in global scope
		{
			searchRes = SYMBOL_TABLE.getInstance().findObject(name);
			if (searchRes != null){ 
				t = searchRes.type;
				this.objIndexInContext = searchRes.entryOriginASTNode.objIndexInContext;
				this.objScopeType = SYMBOL_TABLE.getInstance().curScopeType;
				this.objContext = searchRes.entryOriginASTNode.objContext;
			
			}
		}
		if (t == null || t instanceof TYPE_FUNCTION)
			throw new SemanticRuntimeException(lineNum, colNum, String.format("(%s) cannot be resolved to a variable\n",name));
		return t;
	}
	
	public TEMP get_L_Value()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (this.objContext == ObjectContext.classDataMember) {
			//TEMP classObjAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
			// classObj is the first method's parameter: fp+12
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Frame_Load(t, 12));

			int offset = IR.getInstance().WORD_SIZE * this.objIndexInContext;
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate(t, t, offset));
		} 
		else if (this.objContext == ObjectContext.inputArgumentRecieved) {
			int offset = IR.getInstance().WORD_SIZE * 2 + ( IR.getInstance().WORD_SIZE * this.objIndexInContext); // +4 since fp[0] is prevfp, fp[4] is function name string,
															// fp[8] is ra
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate_To_FP(t, offset));
		} 
		else if (this.objContext == ObjectContext.local) {
			int offset = -1* IR.getInstance().WORD_SIZE * this.objIndexInContext; // first local is in fp[-4]
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Add_Immediate_To_FP(t, offset));
		} 
		else { // global
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Load_Address(String.format("global_%s", this.name),t));
		}

		return t;
	}
	
	public TEMP IRme() {
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (this.objContext == ObjectContext.classDataMember) {
			TEMP classObjAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
			// classObj is the first method's parameter: fp+12
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Frame_Load(classObjAddress, 12));

			int offset = IR.getInstance().WORD_SIZE * this.objIndexInContext;
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Load(t, classObjAddress, offset));
		} 
		else if (this.objContext == ObjectContext.inputArgumentRecieved) {
			int offset = IR.getInstance().WORD_SIZE * 2 + ( IR.getInstance().WORD_SIZE * this.objIndexInContext); // +4 since fp[0] is prevfp, fp[4] is function name string,
															// fp[8] is ra
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Frame_Load(t, offset));
		} 
		else if (this.objContext == ObjectContext.local) {
			int offset = -1* IR.getInstance().WORD_SIZE * this.objIndexInContext; // first local is in fp[-4]
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Frame_Load(t, offset));
		} 
		else { // global
			IR.getInstance().Add_currentListIRcommand(new IRcommand_Load(t, this.name));
		}

		return t;
	}
}
