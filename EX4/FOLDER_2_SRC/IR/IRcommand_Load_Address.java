package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Load_Address extends IRcommand {

	String label;
	TEMP dst;
	
	public IRcommand_Load_Address(String label, TEMP dst)
	{
		this.label = label;
		this.dst = dst;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().la(dst,label);
	}

}
