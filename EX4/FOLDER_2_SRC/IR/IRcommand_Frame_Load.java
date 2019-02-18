package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Frame_Load extends IRcommand {

	TEMP dst;
	int offset;
	
	public IRcommand_Frame_Load(TEMP dst,int offset)
	{
		this.dst      = dst;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().frame_load(dst, offset);
	}

}
