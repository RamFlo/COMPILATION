package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Store_Byte_Zero_Offset extends IRcommand {

	TEMP address;
	int offset;
	
	public IRcommand_Store_Byte_Zero_Offset(int offset, TEMP address)
	{
		this.address = address;
		this.offset      = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().store_byte_zero(offset, address);
	}

}
