package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Store_Reg_On_Stack_Offset extends IRcommand {

	String regName;
	int offset;
	
	public IRcommand_Store_Reg_On_Stack_Offset(String regName, int offset)
	{
		this.regName      = regName;
		this.offset      = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().save_reg_on_stack_by_offset(regName, offset);
	}

}
