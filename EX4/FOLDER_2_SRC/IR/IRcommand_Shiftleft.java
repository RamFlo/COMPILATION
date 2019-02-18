package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Shiftleft extends IRcommand {

	public TEMP dst;
	public TEMP src;
	public int shiftAmount;
	
	public IRcommand_Shiftleft(TEMP dst,TEMP src,int shiftAmount)
	{
		this.dst = dst;
		this.src = src;
		this.shiftAmount = shiftAmount;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().sll(dst, src, shiftAmount);
	}

}
