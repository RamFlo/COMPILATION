package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Move extends IRcommand {

	public TEMP dst;
	public TEMP src;
	
	public IRcommand_Move(TEMP dst,TEMP src)
	{
		this.dst = dst;
		this.src = src;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().move(dst,src);
	}

}
