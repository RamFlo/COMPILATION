package CFG;

import java.util.ArrayList;
import java.util.List;


public class CommandBlock {
	List<Integer> live_in = new ArrayList<Integer>();
	List<Integer> live_out = new ArrayList<Integer>();
	
	CommandData cd = null;
	
	List<CommandBlock> commandLeadingToThisCommand = new ArrayList<CommandBlock>();
	
	public CommandBlock(CommandData cd)
	{
		this.cd = cd;
	}
	
}
