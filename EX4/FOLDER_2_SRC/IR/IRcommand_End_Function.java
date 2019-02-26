package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_End_Function extends IRcommand {

	int numOfLocals;

	public IRcommand_End_Function(int numOfLocals)
	{
		this.numOfLocals = numOfLocals;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().end_function(numOfLocals);
	}

}
