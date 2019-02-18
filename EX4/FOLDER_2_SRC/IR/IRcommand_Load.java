/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;

public class IRcommand_Load extends IRcommand
{
	TEMP dst,src=null;
	String var_name;
	int offset;
	
	public IRcommand_Load(TEMP dst,String global_var_name)
	{
		this.dst      = dst;
		this.var_name = global_var_name;
	}
	
	public IRcommand_Load(TEMP dst,TEMP src, int offset)
	{
		this.dst = dst;
		this.src = src;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if (src == null)
			sir_MIPS_a_lot.getInstance().load(dst,var_name);
		else
			sir_MIPS_a_lot.getInstance().load_offset(dst, src, offset);
	}
}
