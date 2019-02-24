package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Add_Immediate extends IRcommand {
	public TEMP dst;
	public TEMP src;
	public int i;
	
	public IRcommand_Add_Immediate(TEMP dst,TEMP src,int i)
	{
		this.dst = dst;
		this.src = src;
		this.i = i;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().addi(dst,src,i);
	}

}
