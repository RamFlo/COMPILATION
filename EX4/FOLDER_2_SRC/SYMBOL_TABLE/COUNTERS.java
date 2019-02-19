package SYMBOL_TABLE;

import TYPES.TYPE_CLASS;

public class COUNTERS {
	public static int inputArgsRecieved = 1, funcLocalVariables = 1;
	public static int classMethod = 1, classDataMember = 1;
	public static int stringCounter = 1;
	
	public static void resetFunctionCounters()
	{
		inputArgsRecieved = 1;
		funcLocalVariables = 1;
	}
	
	public static void resetClassCounters(TYPE_CLASS superClass)
	{
		if (superClass == null)
		{
			classMethod = 1;
			classDataMember = 1;
		}
		else
		{
			
			classDataMember = superClass.dataMembersMap.size() + 1;
		}
	}

}
