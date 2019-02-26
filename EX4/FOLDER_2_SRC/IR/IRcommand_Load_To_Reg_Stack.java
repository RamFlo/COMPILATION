package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_Load_To_Reg_Stack extends IRcommand {

	String regName;
	int offset;
	
	public IRcommand_Load_To_Reg_Stack(String regName, int offset)
	{
		this.regName      = regName;
		this.offset      = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().lw_to_reg_from_stack_by_offset(regName, offset);
	}

}
