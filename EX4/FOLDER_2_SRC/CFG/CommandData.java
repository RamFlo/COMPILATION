package CFG;

public class CommandData {
	String label = null;
	String command = null;
	Integer t1 = null,t2 = null,t3 = null;
	
	public CommandData(String cmdName,Integer t1, Integer t2, Integer t3, String label)
	{
		this.command = cmdName;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.label = label;
	}
}
