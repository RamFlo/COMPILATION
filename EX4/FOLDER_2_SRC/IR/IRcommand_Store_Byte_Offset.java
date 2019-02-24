package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Store_Byte_Offset extends IRcommand {

	TEMP byteTemp,address;
	int offset;
	
	public IRcommand_Store_Byte_Offset(TEMP byteTemp, int offset, TEMP address)
	{
		this.byteTemp      = byteTemp;
		this.address = address;
		this.offset      = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().store_byte(byteTemp,offset,address);
	}

}
