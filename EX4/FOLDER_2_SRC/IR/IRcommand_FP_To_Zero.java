package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_FP_To_Zero extends IRcommand {

	@Override
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().fp_to_zero();

	}

}
