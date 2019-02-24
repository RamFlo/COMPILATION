package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Frame_Store_Offset extends IRcommand {

	TEMP word;
	int offset;
	
	public IRcommand_Frame_Store_Offset(TEMP word, int offset)
	{
		this.word      = word;
		this.offset      = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().frame_store(word,offset);
	}

}
