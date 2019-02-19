package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Store_Word_Offset extends IRcommand {

	TEMP word,address;
	int offset;
	
	public IRcommand_Store_Word_Offset(TEMP word, int offset, TEMP address)
	{
		this.word      = word;
		this.address = address;
		this.offset      = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().store_word(word,offset,address);
	}

}
