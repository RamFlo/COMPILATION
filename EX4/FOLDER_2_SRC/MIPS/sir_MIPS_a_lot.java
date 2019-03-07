/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;

import CFG.CFGBuilder;
import CFG.CommandBlock;
import CFG.CommandData;
import IR.IRcommand;
/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class sir_MIPS_a_lot
{
	public static int WORD_SIZE=4;
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;

	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile()
	{
		fileWriter.close();
	}
	public void mips_exit()
	{
		fileWriter.print("\tli $v0,10\n");
		fileWriter.print("\tsyscall\n");
		CommandBlock cb = new CommandBlock(new CommandData("exit",null,null,null,null));
		CFGBuilder.insertCommandBlock(cb);
		CFGBuilder.doNotLinkNextCommand();
		CFGBuilder.linkToButtomElement(cb);
	}
	public void print_int(TEMP t)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tmove $a0,Temp_%d\n",idx);
		fileWriter.format("\tli $v0,1\n");
		fileWriter.format("\tsyscall\n");
		//print space
		fileWriter.format("\tli $a0,32\n");
		fileWriter.format("\tli $v0,11\n");
		fileWriter.format("\tsyscall\n");
		
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("PrintInt",null,idx,null,null)));
	}
	public void print_string_by_address(TEMP strAdd)
	{
		int idx=strAdd.getSerialNumber();
		fileWriter.format("\tmove $a0,Temp_%d\n",idx);
		fileWriter.format("\tli $v0,4\n");
		fileWriter.format("\tsyscall\n");
		
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("PrintString",null,idx,null,null)));
	}
	public void print_trace()
	{
		TEMP t = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP curStringAdd = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		int idx=t.getSerialNumber();
		int idStrAdd = curStringAdd.getSerialNumber();
		
		fileWriter.format("\tmove Temp_%d,$fp\n",idx);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("move",idx,null,null,null)));
		
		String loop_start = IRcommand.getFreshLabel("print_trace_loop_start");
		this.label(loop_start);
		
		fileWriter.format("\tlw Temp_%d,4(Temp_%d)\n",idStrAdd,idx);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("lw",idStrAdd,idx,null,null)));
		
		this.print_string_by_address(curStringAdd);
		
		
		fileWriter.format("\tlw Temp_%d,0(Temp_%d)\n",idx,idx);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("lw",idx,idx,null,null)));
		
		this.bnez(t, loop_start);
	}
	//public TEMP addressLocalVar(int serialLocalVarNum)
	//{
	//	TEMP t  = TEMP_FACTORY.getInstance().getFreshTEMP();
	//	int idx = t.getSerialNumber();
	//
	//	fileWriter.format("\taddi Temp_%d,$fp,%d\n",idx,-serialLocalVarNum*WORD_SIZE);
	//	
	//	return t;
	//}
	
	public void initializeDataSegment()
	{
		fileWriter.format(".data\n");
	}
	public void initializeTextSegment()
	{
		fileWriter.format(".text\n");
		fileWriter.format("main:\n");
	}
	public void string_creation(int stringNum, String s)
	{
		fileWriter.format("\tstring_%d: .asciiz \"%s\"\n",stringNum,s);
	}
	public void string_creation_no_quotes(int stringNum, String s)
	{
		fileWriter.format("\tstring_%d: .asciiz %s\n",stringNum,s);
	}
	public void allocate(String var_name)
	{
		fileWriter.format("\tglobal_%s: .word 0\n",var_name);
	}
	public void allocate_on_stack(int words)
	{
		fileWriter.format("\taddiu $sp,$sp,-%d\n", words * WORD_SIZE);
	}
	public void deallocate_on_stack(int words)
	{
		fileWriter.format("\taddiu $sp,$sp,%d\n", words * WORD_SIZE);
	}
	public void save_reg_on_stack_by_offset(String regName,int offset)
	{
		fileWriter.format("\tsw $%s,%d($sp)\n", regName, offset);
	}
	public void save_word_on_stack_by_offset(TEMP word,int offset)
	{
		int idxdst=word.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,%d($sp)\n", idxdst, offset);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sw",null,idxdst,null,null)));
	}
	public void fp_to_zero()
	{
		fileWriter.format("\tmove $fp,$zero\n");
	}
	public void move_to_v0(TEMP src)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tmove $v0,Temp_%d\n",idxsrc);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("move",null,idxsrc,null,null)));
	}
	public void move_from_v0(TEMP dst)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tmove Temp_%d,$v0\n",idxdst);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("move",idxdst,null,null,null)));
	}
	public void initiate_function(int numOfLocals)
	{
		fileWriter.format("\taddiu $sp,$sp,-%d\n",WORD_SIZE);
		fileWriter.format("\tsw $fp,0($sp)\n");
		fileWriter.format("\tmove $fp,$sp\n");
		
		int reqSpace = numOfLocals * WORD_SIZE;
		if (numOfLocals != 0)
			fileWriter.format("\taddiu $sp,$sp,-%d\n",reqSpace);
	}
	public void end_function(int numOfLocals)
	{
		int localSpace = numOfLocals * WORD_SIZE;
		if (numOfLocals != 0)
			fileWriter.format("\taddiu $sp,$sp,%d\n",localSpace);
		
		//load prevfp into fp
		fileWriter.format("\tlw $fp,0($fp)\n");
		//pop prevfp and func name address
		fileWriter.format("\taddiu $sp,$sp,%d\n",WORD_SIZE*2);
	}
	public void move(TEMP dst,TEMP src){
		int idxdst=dst.getSerialNumber(), idxsrc = src.getSerialNumber();
		fileWriter.format("\tmove Temp_%d,Temp_%d\n",idxdst,idxsrc);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("move",idxdst,idxsrc,null,null)));
	}
	public void malloc(TEMP dst, int numWordsToAllocate)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tli $a0,%d\n",numWordsToAllocate*WORD_SIZE);
		fileWriter.format("\tli $v0,9\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tmove Temp_%d,$v0\n",idxdst);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("move",idxdst,null,null,null)));
	}
	public void malloc_size_in_temp(TEMP dst, TEMP size)
	{
		int idxdst=dst.getSerialNumber(), size_serial = size.getSerialNumber();
		fileWriter.format("\tmove $a0,Temp_%d\n",size_serial);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("move",null,size_serial,null,null)));
		fileWriter.format("\tli $v0,9\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tmove Temp_%d,$v0\n",idxdst);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("move",idxdst,null,null,null)));
	}
	public void la(TEMP dst, String label)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tla Temp_%d,%s\n",idxdst,label);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("la",idxdst,null,null,null)));
	}
	public void load(TEMP dst,String var_name)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,global_%s\n",idxdst,var_name);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("lw",idxdst,null,null,null)));
	}
	public void load_offset(TEMP dst,TEMP src, int offset)
	{
		int idxdst=dst.getSerialNumber(), idxsrc = src.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,%d(Temp_%d)\n",idxdst,offset,idxsrc);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("lw",idxdst,idxsrc,null,null)));
	}
	public void lw_to_reg_from_stack_by_offset(String regName,int offset)
	{
		fileWriter.format("\tlw $%s,%d($sp)\n", regName, offset);
	}
	public void load_byte(TEMP dst,TEMP src, int offset)
	{
		int idxdst=dst.getSerialNumber(), idxsrc = src.getSerialNumber();
		fileWriter.format("\tlb Temp_%d,%d(Temp_%d)\n",idxdst,offset,idxsrc);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("lb",idxdst,idxsrc,null,null)));
	}
	public void stack_load(TEMP dst, int offset)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,%d($sp)\n",idxdst,offset);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("lw",idxdst,null,null,null)));
	}
	public void frame_load(TEMP dst, int offset)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,%d($fp)\n",idxdst,offset);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("lw",idxdst,null,null,null)));
	}
	public void sll(TEMP dst,TEMP src, int shiftAmount)
	{
		int idxdst=dst.getSerialNumber(), idxsrc = src.getSerialNumber();
		fileWriter.format("\tsll Temp_%d,Temp_%d,%d\n",idxdst,idxsrc,shiftAmount);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sll",idxdst,idxsrc,null,null)));
	}
	public void store(String var_name,TEMP src)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,global_%s\n",idxsrc,var_name);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sw",null,idxsrc,null,null)));
	}
	public void store_word(TEMP word, int offset, TEMP address)
	{
		int word_serial=word.getSerialNumber(), address_serial = address.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,%d(Temp_%d)\n",word_serial,offset,address_serial);	
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sw",null,word_serial,address_serial,null)));
	}
	public void store_byte(TEMP byteTemp, int offset, TEMP address)
	{
		int byte_serial=byteTemp.getSerialNumber(), address_serial = address.getSerialNumber();
		fileWriter.format("\tsb Temp_%d,%d(Temp_%d)\n",byte_serial,offset,address_serial);	
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sb",null,byte_serial,address_serial,null)));
	}
	public void store_byte_zero(int offset, TEMP address)
	{
		int address_serial = address.getSerialNumber();
		fileWriter.format("\tsb $zero,%d(Temp_%d)\n",offset,address_serial);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sb",null,null,address_serial,null)));
	}
	public void frame_store(TEMP src, int offset)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,%d($fp)\n",idxsrc,offset);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sw",null,idxsrc,null,null)));
	}
	public void li(TEMP t,int value)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tli Temp_%d,%d\n",idx,value);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("li",idx,null,null,null)));
	}
//	public void li_null_terminator(TEMP t)
//	{
//		int idx=t.getSerialNumber();
//		fileWriter.format("\tli Temp_%d,'\0'\n",idx);
//	}
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tadd Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("add",dstidx,i1,i2,null)));
	}
	public void sub(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tsub Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("sub",dstidx,i1,i2,null)));
	}
	public void addi(TEMP dst, TEMP src, int i)
	{
		int idxdst=dst.getSerialNumber(), idxsrc = src.getSerialNumber();
		fileWriter.format("\taddi Temp_%d,Temp_%d,%d\n",idxdst,idxsrc,i);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("addi",idxdst,idxsrc,null,null)));
	}
	public void addi_to_fp(TEMP dst, int i)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\taddi Temp_%d,$fp,%d\n",idxdst,i);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("addi",idxdst,null,null,null)));
	}
	public void mul(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tmul Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("mul",dstidx,i1,i2,null)));
	}
	public void div(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tdiv Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("div",dstidx,i1,i2,null)));
	}
	public void label(String inlabel) 
	{
		fileWriter.format("%s:\n", inlabel);
		CommandBlock cb = new CommandBlock(new CommandData("label", null, null, null, inlabel));
		CFGBuilder.insertCommandBlock(cb);
		CFGBuilder.insertToLabelMap(inlabel, cb);
	}
	public void vftable_label(String className)
	{
			fileWriter.format("VFTable_%s: .word ",className);
	}
	public void vftable_method(String methodName, String originClass)
	{
			fileWriter.format("method_%s_%s",originClass,methodName);
	}
	public void add_new_line()
	{
		fileWriter.format("\n");
	}
	public void add_comma()
	{
		fileWriter.format(",");
	}
	public void jump(String inlabel)
	{
		fileWriter.format("\tj %s\n",inlabel);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("j", null, null, null, inlabel)));
		CFGBuilder.doNotLinkNextCommand();
	}
	public void jump_to_ra()
	{
		fileWriter.format("\tjr $ra\n");
		CommandBlock cb = new CommandBlock(new CommandData("jr", null, null, null, null));
		CFGBuilder.insertCommandBlock(cb);
		CFGBuilder.doNotLinkNextCommand();
		CFGBuilder.linkToButtomElement(cb);
	}
	public void jump_register(TEMP regTemp)
	{
		//method call
		int regSer =regTemp.getSerialNumber();
		fileWriter.format("\tjalr Temp_%d\n",regSer);
		CommandBlock cb = new CommandBlock(new CommandData("jalr", null,regSer, null, null));
		CFGBuilder.insertCommandBlock(cb);
	}
	public void jump_and_link(String inlabel)
	{
		fileWriter.format("\tjal %s\n",inlabel);
		//CommandBlock cb = new CommandBlock(new CommandData("jal", null, null, null, inlabel));
	}	
	public void blt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tblt Temp_%d,Temp_%d,%s\n",i1,i2,label);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("blt",null, i1, i2, label)));
	}
	public void bltz(TEMP oprnd1,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		
		fileWriter.format("\tblt Temp_%d,$zero,%s\n",i1,label);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("bltz", null, i1, null, label)));
	}
	public void bge(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbge Temp_%d,Temp_%d,%s\n",i1,i2,label);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("bge", null,i1, i2, label)));
	}
	public void bgt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbgt Temp_%d,Temp_%d,%s\n",i1,i2,label);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("bgt", null,i1, i2, label)));
	}
	public void bne(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbne Temp_%d,Temp_%d,%s\n",i1,i2,label);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("bne", null,i1, i2, label)));
	}
	public void bnez(TEMP oprnd1,String label)
	{
		int i1 =oprnd1.getSerialNumber();
				
		fileWriter.format("\tbne Temp_%d,$zero,%s\n",i1,label);	
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("bnez",null, i1, null, label)));
	}
	public void beq(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbeq Temp_%d,Temp_%d,%s\n",i1,i2,label);
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("beq", null,i1, i2, label)));
	}
	public void beqz(TEMP oprnd1,String label)
	{
		int i1 =oprnd1.getSerialNumber();
				
		fileWriter.format("\tbeq Temp_%d,$zero,%s\n",i1,label);		
		CFGBuilder.insertCommandBlock(new CommandBlock(new CommandData("beqz", null,i1, null, label)));
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static sir_MIPS_a_lot instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected sir_MIPS_a_lot() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static sir_MIPS_a_lot getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new sir_MIPS_a_lot();

			try
			{
				/*********************************************************************************/
				/* [1] Open the MIPS text file and write data section with error message strings */
				/*********************************************************************************/
				String dirname="./FOLDER_5_OUTPUT/";
				String filename=String.format("MIPS.txt");

				/***************************************/
				/* [2] Open MIPS text file for writing */
				/***************************************/
				instance.fileWriter = new PrintWriter(dirname+filename);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			/*****************************************************/
			/* [3] Print data section with error message strings */
			/*****************************************************/
			instance.fileWriter.print(".data\n");
			instance.fileWriter.print("\tstring_access_violation: .asciiz \"Access Violation\"\n");
			instance.fileWriter.print("\tstring_illegal_div_by_0: .asciiz \"Division By Zero\"\n");
			instance.fileWriter.print("\tstring_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
		}
		return instance;
	}
}
