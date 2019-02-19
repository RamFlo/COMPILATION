package IR;

import TEMP.*;
import MIPS.*;

public class IRcommand_Malloc extends IRcommand {

	int numWordsToAllocate;
	TEMP dstForAddress, sizeToAllocate = null;
	
	public IRcommand_Malloc(int numWordsToAllocate, TEMP dstForAddress)
	{
		this.numWordsToAllocate = numWordsToAllocate;
		this.dstForAddress = dstForAddress;
	}
	
	public IRcommand_Malloc(TEMP sizeToAllocate, TEMP dstForAddress)
	{
		this.sizeToAllocate = sizeToAllocate;
		this.dstForAddress = dstForAddress;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if (sizeToAllocate == null)
			sir_MIPS_a_lot.getInstance().malloc(dstForAddress, numWordsToAllocate);
		else
			sir_MIPS_a_lot.getInstance().malloc_size_in_temp(dstForAddress, sizeToAllocate);
	}

}
