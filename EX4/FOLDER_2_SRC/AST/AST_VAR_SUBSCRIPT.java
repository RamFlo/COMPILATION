package AST;

import IR.IR;
import IR.IRcommand;
import IR.IRcommand_BEQZ;
import IR.IRcommand_BGE;
import IR.IRcommand_BGT;
import IR.IRcommand_BLTZ;
import IR.IRcommand_BNEZ;
import IR.IRcommand_Binop_Add_Integers;
import IR.IRcommand_Exit;
import IR.IRcommand_Label;
import IR.IRcommand_Load;
import IR.IRcommand_Load_Address;
import IR.IRcommand_Print_String_By_Address;
import IR.IRcommand_Shiftleft;
import MyExceptions.SemanticRuntimeException;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;
import TYPES.TYPE_ARRAY;
import TYPES.TYPE_INT;
import TYPES.TYPE_LIST;

public class AST_VAR_SUBSCRIPT extends AST_VAR
{
	public AST_VAR var;
	public AST_EXP subscript;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SUBSCRIPT(AST_VAR var,AST_EXP subscript)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== var -> var [ exp ]\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
		this.subscript = subscript;
	}

	/*****************************************************/
	/* The printing message for a subscript var AST node */
	/*****************************************************/
	public void PrintMe()
	{
		/*************************************/
		/* AST NODE TYPE = AST SUBSCRIPT VAR */
		/*************************************/
		System.out.print("AST NODE SUBSCRIPT VAR\n");

		/****************************************/
		/* RECURSIVELY PRINT VAR + SUBSRIPT ... */
		/****************************************/
		var.PrintMe();
		subscript.PrintMe();
		
		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"SUBSCRIPT\nVAR\n...[...]");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,subscript.SerialNumber);
	}
	
	public TYPE SemantMe()
	{
		TYPE t = null;
		TYPE_ARRAY ta = null;
		
		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if (var != null) t = var.SemantMe();
		
		/*********************************/
		/* [2] Make sure type is an array */
		/*********************************/
		if (!(t instanceof TYPE_ARRAY))
			throw new SemanticRuntimeException(lineNum, colNum, "trying to access subscript of a variable that is not an ARRAY\n");
		else
			ta = (TYPE_ARRAY) t;
		
		/***********************************************/
		/* [3] Make sure subscript's exp is an integer */
		/***********************************************/
		if (subscript.SemantMe() != TYPE_INT.getInstance())
			throw new SemanticRuntimeException(lineNum, colNum, "subscript (sub) of 'VAR[<sub>]' is not an integral\n");
		
		
		/*******************************/
		/* [4] return the array's type */
		/*******************************/
		return ta.arrayType;
	}
	
	private void checkIndexOutOfBounds(TEMP arrAddress, TEMP index) {

		String label_in_bounds = IRcommand.getFreshLabel("index_in_bounds");
		String label_out_of_bounds = IRcommand.getFreshLabel("index_out_of_bounds");

		// null pointer handling!
		IR.getInstance().Add_currentListIRcommand(new IRcommand_BEQZ(arrAddress,label_out_of_bounds));

		TEMP arrSize = TEMP_FACTORY.getInstance().getFreshTEMP();

		IR.getInstance()
				.Add_currentListIRcommand(new IRcommand_Load(arrSize, arrAddress, -1 * IR.getInstance().WORD_SIZE));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_BLTZ(index, label_out_of_bounds));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_BGT(arrSize, index, label_in_bounds));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_out_of_bounds));

		String label_access_violation_exception = "string_access_violation";

		TEMP access_exception_str_address = TEMP_FACTORY.getInstance().getFreshTEMP();

		IR.getInstance().Add_currentListIRcommand(
				new IRcommand_Load_Address(label_access_violation_exception, access_exception_str_address));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Print_String_By_Address(access_exception_str_address));

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Exit());

		IR.getInstance().Add_currentListIRcommand(new IRcommand_Label(label_in_bounds));
	}
	
	public TEMP get_L_Value()
	{
		/*******************/
		/* [1] IR array object */
		/*******************/
		TEMP srcAddress = var.IRme();
		
		/*******************/
		/* [2] IR subscript and calculate load address */
		/*******************/
		TEMP offsetTemp = subscript.IRme();
		
		checkIndexOutOfBounds(srcAddress,offsetTemp);
		
		//multiply offset index by 4
		//offsetTemp *= 4
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Shiftleft(offsetTemp, offsetTemp, 2));
		
		//calculate exact address by adding offset value to original array's address
		//srcAddress = srcAddress + offsetTemp
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Binop_Add_Integers(srcAddress, srcAddress, offsetTemp));
		
		return srcAddress;
	}
	
	public TEMP IRme() {
		
		TEMP indexInArrAddress = this.get_L_Value();

		/*************************************/
		/* [3] load array cell value */
		/*************************************/
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Load(t, indexInArrAddress, 0));

		return t;
	}
}
