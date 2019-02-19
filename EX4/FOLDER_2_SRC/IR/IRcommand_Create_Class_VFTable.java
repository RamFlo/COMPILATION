package IR;

import TEMP.*;

import java.util.Map;

import MIPS.*;
import MyClasses.ClassMethodDetails;

public class IRcommand_Create_Class_VFTable extends IRcommand {

	Map<String, ClassMethodDetails> methodsMap;
	String className;
	
	public IRcommand_Create_Class_VFTable(Map<String, ClassMethodDetails> methodsMap, String className)
	{
		this.methodsMap = methodsMap;
		this.className = className;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if (this.methodsMap.size() == 0)
			return;
		sir_MIPS_a_lot.getInstance().vftable_label(this.className);
		
		String methodName, originClassName;
		int i=1;
		for(Map.Entry<String, ClassMethodDetails> methodEntry : this.methodsMap.entrySet()){
			methodName = methodEntry.getKey();
			originClassName = methodEntry.getValue().methodOriginClassName;
			sir_MIPS_a_lot.getInstance().vftable_method(methodName, originClassName);
			if (i!=this.methodsMap.size())
				sir_MIPS_a_lot.getInstance().add_comma();
			i++;
		}
		sir_MIPS_a_lot.getInstance().add_new_line();
	}

}
