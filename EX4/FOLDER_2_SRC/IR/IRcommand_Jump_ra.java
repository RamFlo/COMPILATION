package IR;

import MIPS.sir_MIPS_a_lot;

public class IRcommand_Jump_ra extends IRcommand {

	@Override
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().jump_to_ra();

	}

}
