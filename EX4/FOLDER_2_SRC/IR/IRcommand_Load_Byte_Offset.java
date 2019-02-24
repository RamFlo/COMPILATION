package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Load_Byte_Offset extends IRcommand {

	TEMP dst,address;
	int offset;
	
	public IRcommand_Load_Byte_Offset(TEMP dst, int offset, TEMP address) {
		this.dst = dst;
		this.address = address;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().load_byte(dst,address,offset);
	}

}
