package CFG;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CFGBuilder {

	static CommandBlock buttomElement = new CommandBlock(new CommandData("buttomElement",null,null,null,null));
	static CommandBlock prevBlock = null;
	static boolean link = true;
	static Map<String,CommandBlock> labelMap = new HashMap<String,CommandBlock>();
	
	static List<CommandBlock> CFG = new LinkedList<CommandBlock>();
	
	public static void insertCommandBlock(CommandBlock cb)
	{
		CFG.add(cb);
		if (link)
			cb.commandLeadingToThisCommand.add(prevBlock);
		prevBlock = cb;
		link = true;
	}
	
	public static void insertToLabelMap(String label, CommandBlock cb)
	{
		labelMap.put(label, cb);
	}
	
	public static void doNotLinkNextCommand()
	{
		link = false;
	}
	
	public static void linkToButtomElement(CommandBlock cb)
	{
		buttomElement.commandLeadingToThisCommand.add(cb);
	}
}
