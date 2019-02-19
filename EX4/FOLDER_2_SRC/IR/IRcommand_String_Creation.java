package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_String_Creation extends IRcommand {

	String s;
	int stringNum;
	
	public IRcommand_String_Creation(String s,int stringNum)
	{
		this.s = s;
		this.stringNum = stringNum;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().string_creation(stringNum, s);
	}

}
