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
	private List<IRcommand> fullIRCommandList = null;
	
	
//	private IRcommand head=null;
//	private IRcommandList tail=null;
	
	private List<IRcommand> dataSegmentIRCommandList = null;
	private List<IRcommand> textSegmentIRCommandList = null;
	


	/******************/
	/* Add IR command */
	/******************/
	public void Add_IRcommand(IRcommand cmd)
	{
		if (fullIRCommandList == null)
			fullIRCommandList = new LinkedList<IRcommand>();
		fullIRCommandList.add(cmd);
	}
	
	/******************/
	/* Add IR command for data segment */
	/******************/
	public void Add_dataSegmentIRcommand(IRcommand cmd)
	{
		if (dataSegmentIRCommandList == null)
			dataSegmentIRCommandList = new LinkedList<IRcommand>();
		dataSegmentIRCommandList.add(cmd);
	}

	
	/******************/
	/* Add IR command for text segment */
	/******************/
	public void Add_textSegmentIRcommand(IRcommand cmd)
	{
		if (textSegmentIRCommandList == null)
			textSegmentIRCommandList = new LinkedList<IRcommand>();
		textSegmentIRCommandList.add(cmd);
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().initializeDataSegment();
		for (IRcommand dataSegmentIRCommand:dataSegmentIRCommandList)
			dataSegmentIRCommand.MIPSme();
		
		sir_MIPS_a_lot.getInstance().initializeTextSegment();
		for (IRcommand textSegmentIRCommand:textSegmentIRCommandList)
			textSegmentIRCommand.MIPSme();
		
		sir_MIPS_a_lot.getInstance().initializeDataSegment();
		for (IRcommand dataSegmentIRCommand:dataSegmentIRCommandList)
			dataSegmentIRCommand.MIPSme();
		
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
