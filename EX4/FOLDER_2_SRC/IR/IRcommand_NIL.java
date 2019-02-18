package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.*;

public class IRcommand_NIL extends IRcommand {
	TEMP t;
	int nullAddressValue;
	
	public IRcommand_NIL(TEMP t)
	{
		this.t = t;
		this.nullAddressValue = 0;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().li(t,nullAddressValue);
	}
}
