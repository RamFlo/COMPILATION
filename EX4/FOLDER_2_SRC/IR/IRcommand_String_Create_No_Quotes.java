package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_String_Create_No_Quotes extends IRcommand {

	String s;
	int stringNum;
	
	public IRcommand_String_Create_No_Quotes(String s,int stringNum)
	{
		this.s = s;
		this.stringNum = stringNum;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().string_creation_no_quotes(stringNum, s);
	}

}
