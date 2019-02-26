/***********/
/* PACKAGE */
/***********/
package IR;

import java.util.LinkedList;
import java.util.List;

import MIPS.sir_MIPS_a_lot;

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
	
	private List<IRcommand> dataSegmentIRCommandList = null;
	
	private List<IRcommand> globalsInitIRCommandList = null;
	
	private List<IRcommand> codeSegmentIRCommandList = null;
	
	private List<IRcommand> currentIRCommandList = this.codeSegmentIRCommandList;
	


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
		if (dataSegmentIRCommandList == null)
			dataSegmentIRCommandList = new LinkedList<IRcommand>();
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
		if (currentIRCommandList == null)
			currentIRCommandList = new LinkedList<IRcommand>();
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
		
		//start .data
		sir_MIPS_a_lot.getInstance().initializeDataSegment();
		for (IRcommand dataSegmentIRCommand:dataSegmentIRCommandList)
			dataSegmentIRCommand.MIPSme();
		
		//start .text
		
		sir_MIPS_a_lot.getInstance().initializeTextSegment();
		for (IRcommand globalsInitIRCommand:globalsInitIRCommandList)
			globalsInitIRCommand.MIPSme();
		
		//TO-DO: add jump to main, then exit()
		
		sir_MIPS_a_lot.getInstance().initializeTextSegment();
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
