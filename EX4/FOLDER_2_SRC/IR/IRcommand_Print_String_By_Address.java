package IR;

import MIPS.sir_MIPS_a_lot;
import TEMP.TEMP;

public class IRcommand_Print_String_By_Address extends IRcommand {

	TEMP strAddress;

	public IRcommand_Print_String_By_Address(TEMP strAddress) {
		this.strAddress = strAddress;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme() {
		sir_MIPS_a_lot.getInstance().print_string_by_address(strAddress);
	}

}
