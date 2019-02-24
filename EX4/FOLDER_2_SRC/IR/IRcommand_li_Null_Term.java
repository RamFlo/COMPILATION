package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_li_Null_Term extends IRcommand {

	TEMP t;

	public IRcommand_li_Null_Term(TEMP t) {
		this.t = t;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().li_null_terminator(t);
	}

}

