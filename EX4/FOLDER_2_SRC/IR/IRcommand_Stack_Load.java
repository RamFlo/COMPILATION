package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Stack_Load extends IRcommand {

	TEMP dst;
	int offset;
	
	public IRcommand_Stack_Load(TEMP dst,int offset)
	{
		this.dst      = dst;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().stack_load(dst, offset);
	}

}
