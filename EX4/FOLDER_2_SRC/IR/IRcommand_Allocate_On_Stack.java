package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_Allocate_On_Stack extends IRcommand {

	int words;
	
	public IRcommand_Allocate_On_Stack(int words)
	{
		this.words = words;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().allocate_on_stack(words);
	}

}
