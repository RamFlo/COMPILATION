package AST;

import IR.IR;
import IR.IRcommandConstInt;
import IR.IRcommand_Load_Address;
import IR.IRcommand_String_Create_No_Quotes;
import IR.IRcommand_String_Creation;
import SYMBOL_TABLE.COUNTERS;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;
import TYPES.TYPE;
import TYPES.TYPE_STRING;

public class AST_EXP_STRING extends AST_EXP {
	
	public String value;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_STRING(String value)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		System.out.format("====================== exp -> STRING( %s )\n", value);
		this.value = value;
	}

	/******************************************************/
	/* The printing message for a STRING EXP AST node */
	/******************************************************/
	public void PrintMe()
	{
		/*******************************/
		/* AST NODE TYPE = AST STRING EXP */
		/*******************************/
		System.out.format("AST NODE STRING( %s )\n",value);

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("STRING\n%s",value.replace('"','\''))); // String.format("STRING\n%s",value.replace('"','\'')
	}
	
	public TYPE SemantMe() {
		return TYPE_STRING.getInstance();
	}
	
	public TEMP IRme() {
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();

		// create string in data segment
		IR.getInstance().Add_dataSegmentIRcommand(new IRcommand_String_Create_No_Quotes(value, COUNTERS.stringCounter));

		// load string address (by it's label) into temp t
		IR.getInstance().Add_currentListIRcommand(
				new IRcommand_Load_Address(String.format("string_%d", COUNTERS.stringCounter), t));

		// increment string counter
		COUNTERS.stringCounter++;

		// return string address
		return t;
	}
}
