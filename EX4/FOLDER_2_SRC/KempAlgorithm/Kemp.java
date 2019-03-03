package KempAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.sun.javafx.collections.MappingChange.Map;

public class Kemp {
	KempGraph graph = null;
	Stack<Integer> stackOfVertices = new Stack<Integer>();
	HashMap <Integer, Integer> coloring = new HashMap<Integer, Integer>();
	
	public Kemp(KempGraph graphToColor) {
		this.graph = graphToColor;
	}
	
	public List<Integer> getLiveNeighbors(int vertexIndex) {
		List<Integer> liveNeighbors = new ArrayList<Integer>();
		List<Integer> allVertexNeighbors = this.graph.edges.get(vertexIndex);
		List<Integer> allLiveVertices = this.graph.vertices;
		for (Integer liveVertexIndex : allLiveVertices) {
			if (allVertexNeighbors.contains(liveVertexIndex))
				liveNeighbors.add(liveVertexIndex);
		}
		return liveNeighbors;
	}
	
	public int countLiveNeighbors(int vertexIndex) {
		List<Integer> listOfLiveNeighbors = getLiveNeighbors(vertexIndex);
		return listOfLiveNeighbors.size();
	}
	
	public boolean canSimplifyVertex(int vertexIndex) {
		int numOfLiveNeighbors = countLiveNeighbors(vertexIndex);
		boolean canSimplifyVertex = (numOfLiveNeighbors < 8) ? true : false;
		return canSimplifyVertex;
	}
	
	public boolean simplify() {
		List<Integer> curVerticesInGraph = this.graph.vertices;
		boolean hasRemovedVertex = false;
		for (Integer vertexIndex: curVerticesInGraph) {
			if (canSimplifyVertex(vertexIndex)) {
				stackOfVertices.push(vertexIndex);
				this.graph.vertices.remove(vertexIndex);
				hasRemovedVertex = true;
				break;
			}
		}
		return hasRemovedVertex;
	}
	
	public List<Integer> getListOfAllColors() {
		List<Integer> listOfAllColors = new ArrayList<Integer>();
		for (int i=0; i<8; i++)
			listOfAllColors.add(i);
		return listOfAllColors;
	}
	
	public int findColorForVertex(int vertexIndex) {
		List<Integer> liveNeighborsOfVertex = getLiveNeighbors(vertexIndex);
		List<Integer> listOfAvailableColors = getListOfAllColors();
		for (Integer neighborVertexIndex : liveNeighborsOfVertex) {
			int neighborColor = this.coloring.get(neighborVertexIndex);
			listOfAvailableColors.remove(neighborColor);
		}
		int foundAvailableColor = listOfAvailableColors.get(0);
		return foundAvailableColor;
	}
	
	public HashMap<Integer, Integer> kempAlg() {
		boolean couldSimplify = true;
		while (couldSimplify) {
			couldSimplify = simplify();
		}
		if (!this.graph.isGraphEmpty())
			//throw error at this point, could not simplify so Adi must bake brownies
		
		while (!stackOfVertices.isEmpty()) {
			int curPoppedVertex = stackOfVertices.pop();
			int availableColor = findColorForVertex(curPoppedVertex);
			this.coloring.put(curPoppedVertex, availableColor);
		}
		return this.coloring;
	}
}
