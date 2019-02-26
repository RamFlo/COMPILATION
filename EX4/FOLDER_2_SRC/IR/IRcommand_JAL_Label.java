package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_JAL_Label extends IRcommand {

	String label_name;
	
	public IRcommand_JAL_Label(String label_name)
	{
		this.label_name = label_name;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().jump_and_link(label_name);
	}

}
