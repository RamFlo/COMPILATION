package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_BNEZ extends IRcommand {

	public TEMP t1;
	public String label;

	public IRcommand_BNEZ(TEMP t1, String label) {
		this.t1 = t1;
		this.label = label;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().bnez(t1, label);
	}

}
