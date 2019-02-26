package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_Print_Trace extends IRcommand {

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().print_trace();
	}

}
