package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_BEQZ extends IRcommand {

	public TEMP t1;
	public String label;
	
	public IRcommand_BEQZ(TEMP t1,String label)
	{
		this.t1 = t1;
		this.label = label;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().beqz(t1,label);
	}
}
