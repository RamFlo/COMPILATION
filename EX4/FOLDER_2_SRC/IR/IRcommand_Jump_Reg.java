package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Jump_Reg extends IRcommand {

	TEMP reg;
	
	public IRcommand_Jump_Reg(TEMP reg)
	{
		this.reg = reg;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().jump_register(reg);
	}

}
