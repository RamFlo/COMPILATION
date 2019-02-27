/***********/
/* PACKAGE */
/***********/
package IR;

import java.util.LinkedList;
import java.util.List;

import MIPS.sir_MIPS_a_lot;
import SYMBOL_TABLE.COUNTERS;
import TEMP.TEMP;
import TEMP.TEMP_FACTORY;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class IR
{
//	private List<IRcommand> fullIRCommandList = null;
	
	
//	private IRcommand head=null;
//	private IRcommandList tail=null;
	
	public int curFunctionParamNum = 0;
	
	public int WORD_SIZE = 4;
	
	private List<IRcommand> dataSegmentIRCommandList = new LinkedList<IRcommand>();
	
	private List<IRcommand> globalsInitIRCommandList = new LinkedList<IRcommand>();
	
	private List<IRcommand> codeSegmentIRCommandList = new LinkedList<IRcommand>();
	
	private List<IRcommand> currentIRCommandList = this.codeSegmentIRCommandList;
	


	private void createMainFuncNameStringAndPushToStack()
	{
		// create string in data segment
		IR.getInstance().Add_dataSegmentIRcommand(new IRcommand_String_Creation("main", COUNTERS.stringCounter));

		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		// load string address (by it's label) into temp t
		IR.getInstance().Add_currentListIRcommand(
				new IRcommand_Load_Address(String.format("string_%d", COUNTERS.stringCounter), t));

		// increment string counter
		COUNTERS.stringCounter++;
		
		// allocate space for main funcname string address on stack
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Allocate_On_Stack(1));
						
		// save string address on stack
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Store_Word_Stack_Offset(t,0));
	}
	
	private void initializeProgram()
	{
		this.switchList_globalInitList();
		
		this.createMainFuncNameStringAndPushToStack();
		IR.getInstance().Add_currentListIRcommand(new IRcommand_FP_To_Zero());
		IR.getInstance().Add_currentListIRcommand(new IRcommand_JAL_Label("global_function_main"));
		//remove main's name from stack
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Dealloc_Stack(1));
		//exit
		IR.getInstance().Add_currentListIRcommand(new IRcommand_Exit());
		
		this.switchList_codeList();
		
	}
	/******************/
	/* Add IR command */
	/******************/
//	public void Add_IRcommand(IRcommand cmd)
//	{
//		if (fullIRCommandList == null)
//			fullIRCommandList = new LinkedList<IRcommand>();
//		fullIRCommandList.add(cmd);
//	}
	
	/******************/
	/* Add IR command for data segment */
	/******************/
	public void Add_dataSegmentIRcommand(IRcommand cmd)
	{
//		if (dataSegmentIRCommandList == null)
//			dataSegmentIRCommandList = new LinkedList<IRcommand>();
		dataSegmentIRCommandList.add(cmd);
	}
	
//	/******************/
//	/* Add IR command for globals init segment */
//	/******************/
//	public void Add_globalsInitIRcommand(IRcommand cmd)
//	{
//		if (globalsInitIRCommandList == null)
//			globalsInitIRCommandList = new LinkedList<IRcommand>();
//		globalsInitIRCommandList.add(cmd);
//	}
//
//	
//	/******************/
//	/* Add IR command for code segment */
//	/******************/
//	public void Add_codeSegmentIRcommand(IRcommand cmd)
//	{
//		if (codeSegmentIRCommandList == null)
//			codeSegmentIRCommandList = new LinkedList<IRcommand>();
//		codeSegmentIRCommandList.add(cmd);
//	}
	
	/******************/
	/* Add IR command for current list */
	/******************/
	public void Add_currentListIRcommand(IRcommand cmd)
	{
//		if (currentIRCommandList == null)
//			currentIRCommandList = new LinkedList<IRcommand>();
		currentIRCommandList.add(cmd);
	}
	
	/******************/
	/* Switch to global init list */
	/******************/
	public void switchList_globalInitList()
	{
		this.currentIRCommandList = this.globalsInitIRCommandList;
	}
	
	/******************/
	/* Switch to code list */
	/******************/
	public void switchList_codeList()
	{
		this.currentIRCommandList = this.codeSegmentIRCommandList;
	}
	
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		this.initializeProgram();
		
		//start .data
		for (IRcommand dataSegmentIRCommand:dataSegmentIRCommandList)
			dataSegmentIRCommand.MIPSme();
		
		//add .text header
		sir_MIPS_a_lot.getInstance().initializeTextSegment();
		
		for (IRcommand globalsInitIRCommand:globalsInitIRCommandList)
			globalsInitIRCommand.MIPSme();
		for (IRcommand codeSegmentIRCommand:codeSegmentIRCommandList)
			codeSegmentIRCommand.MIPSme();
		
	}

	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static IR instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected IR() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static IR getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new IR();
		}
		return instance;
	}
}
