package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Initiate_Function extends IRcommand {
	int numOfLocals;

	public IRcommand_Initiate_Function(int numOfLocals)
	{
		this.numOfLocals = numOfLocals;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().initiate_function(numOfLocals);
	}
}
