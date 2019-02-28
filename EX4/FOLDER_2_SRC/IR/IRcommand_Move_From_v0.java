package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Move_From_v0 extends IRcommand {

	public TEMP dst;
	
	public IRcommand_Move_From_v0(TEMP dst)
	{
		this.dst = dst;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().move_from_v0(dst);
	}

}
