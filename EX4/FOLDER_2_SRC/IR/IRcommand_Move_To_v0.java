package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Move_To_v0 extends IRcommand {

	public TEMP src;
	
	public IRcommand_Move_To_v0(TEMP src)
	{
		this.src = src;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().move_to_v0(src);
	}

}
