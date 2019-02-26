package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_Dealloc_Stack extends IRcommand {

	int words;
	
	public IRcommand_Dealloc_Stack(int words)
	{
		this.words = words;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().deallocate_on_stack(words);
	}

}
