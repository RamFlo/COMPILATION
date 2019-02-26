package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Store_Word_Stack_Offset extends IRcommand {

	TEMP word;
	int offset;
	
	public IRcommand_Store_Word_Stack_Offset(TEMP word, int offset)
	{
		this.word      = word;
		this.offset      = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().save_word_on_stack_by_offset(word, offset);
	}

}
