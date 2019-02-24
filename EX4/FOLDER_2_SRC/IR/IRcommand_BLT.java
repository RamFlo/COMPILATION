package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_BLT extends IRcommand {

	public TEMP t1, t2;
	public String label;
	
	public IRcommand_BLT(TEMP t1,TEMP t2,String label)
	{
		this.t1 = t1;
		this.t2 = t2;
		this.label = label;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().blt(t1,t2,label);
	}

}
