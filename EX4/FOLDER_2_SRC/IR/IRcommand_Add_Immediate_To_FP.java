package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Add_Immediate_To_FP extends IRcommand {

	public TEMP dst;
	public int i;
	
	public IRcommand_Add_Immediate_To_FP(TEMP dst,int i)
	{
		this.dst = dst;
		this.i = i;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().addi_to_fp(dst, i);
	}
}
