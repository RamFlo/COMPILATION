package CFG;

import java.util.ArrayList;
import java.util.List;

import KempAlgorithm.KempGraph;

/* Need to add access to real all commands list instead of allCommands
 * Need to change the code a bit considering that now t1 is never important to us. Might still work as is though.
 * if each command has it's own block, we might have a problem in that aspect
*/

public class LivenessAnalyzer {
	CommandBlock startingBlock = null;
	boolean changedAnyBlock = true;
	//Temporary:
	List<CommandBlock> allCommands = null;
	
	public LivenessAnalyzer(CommandBlock startingBlock) {
		this.startingBlock = startingBlock;
	}
	
	List<String> getAllAssignmentCommands() {
		List<String> allAssignCommands = new ArrayList<String>();
		//have I missed anything? anything out of place?
		String[] assignmentCommands = {"add", "sub", "addi", "mul", "div", "li", "la", "move", "lw", "lb"};
		for (String command: assignmentCommands)
			allAssignCommands.add(command);
		return allAssignCommands;
	}
	
	boolean isAssignmentCommand(CommandBlock command) {
		String actualCommand = command.cd.command;
		List<String> allAssignmentCommands = getAllAssignmentCommands();
		return allAssignmentCommands.contains(actualCommand);
	}
	
	public List<Integer> getTempsNeededByCommand(CommandBlock command) {
		List<Integer> tempsUsedByCommand = new ArrayList<Integer>();
		//if command is assignment, the first temp isn't really needed by it
		if (command.cd.t1 != null && !isAssignmentCommand(command)) 
			tempsUsedByCommand.add(command.cd.t1);
		if (command.cd.t2 != null && !tempsUsedByCommand.contains(command.cd.t2))
			tempsUsedByCommand.add(command.cd.t2);
		if (command.cd.t3 != null && !tempsUsedByCommand.contains(command.cd.t3))
			tempsUsedByCommand.add(command.cd.t3);
		return tempsUsedByCommand;
	}
	
	
	
	public void addValuesUsedByCommandToLiveIn(CommandBlock command) {
		List<Integer> tempsToAddToLiveIn = getTempsNeededByCommand(command);
		if (!command.live_in.containsAll(tempsToAddToLiveIn)) {
			changedAnyBlock = true;
			for (Integer temp : tempsToAddToLiveIn)
				if (!command.live_in.contains(temp))
					command.live_in.add(temp);
		}	
	}
	
	public void addCommandLiveInToParentsLiveOut(CommandBlock command) {
		List<CommandBlock> parents = command.commandLeadingToThisCommand;
		for (CommandBlock parentBlock : parents) {
			for (Integer temp : command.live_in) {
				if (!parentBlock.live_out.contains(temp)) {
					changedAnyBlock = true;
					parentBlock.live_out.add(temp);
				}
			}
		}
	}
	
	public void addCommandLiveOutToLiveIn(CommandBlock command) {
		for (Integer temp : command.live_out) {
			if (!command.live_in.contains(temp) && command.cd.t1 != temp) {
				changedAnyBlock = true;
				command.live_in.add(temp);
			}
		}
	}
	
	public void analyzeLiveness() {
		while (changedAnyBlock) {
			changedAnyBlock = false;
			for (CommandBlock command: allCommands) {
				addValuesUsedByCommandToLiveIn(command);
				addCommandLiveInToParentsLiveOut(command);
				addCommandLiveOutToLiveIn(command);
			}
		}
	}
	
	public void addAllTempsInLiveInToGraph(CommandBlock command, KempGraph graph) {
		for (Integer temp: command.live_in) {
			if (!graph.isVerticeInGraph(temp)) {
				graph.vertices.add(temp);
			}
		}
	}
	
	public void addEdgesBetweenTempsUsedBySameCommand(CommandBlock command, KempGraph graph) {
		List<Integer> liveIn = command.live_in;
		for (int i=0; i<liveIn.size(); i++) {
			for (int j=i+1; j<liveIn.size(); j++) {
				int tempIndexA = liveIn.get(i);
				int tempIndexB = liveIn.get(j);
				if (!graph.hasEdge(tempIndexA, tempIndexB))
					graph.addEdge(tempIndexA, tempIndexB);
			}
		}
	}
	
	public KempGraph buildKempGraphFromLiveAnalysis() {
		KempGraph graph = new KempGraph();
		for (CommandBlock command: allCommands) {
			addAllTempsInLiveInToGraph(command, graph);
			addEdgesBetweenTempsUsedBySameCommand(command, graph);
		}
		return graph;
	}
}
