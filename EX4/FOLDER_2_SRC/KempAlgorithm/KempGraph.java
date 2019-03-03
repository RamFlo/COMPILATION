package KempAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KempGraph {
	public List<Integer> vertices = new ArrayList<Integer>();
	public HashMap<Integer, List<Integer>> edges = new HashMap<Integer, List<Integer>>();
	
	
	public void addNode(int verticeIndex) {
		if (!vertices.contains(verticeIndex))
			vertices.add(verticeIndex);
	}
	
	public boolean isVerticeInGraph(int index) {
		return vertices.contains(index);
	}
	
	public boolean hasEdge(int verticeA, int verticeB) {
		if (!edges.containsKey(verticeA))
			return false;
		return edges.get(verticeA).contains(verticeB);
	}
	
	public void addEdgeToBToAList(int verticeIndexA, int verticeIndexB) {
		if (!edges.containsKey(verticeIndexA)) {
			List<Integer> neighborListForA = new ArrayList<Integer>();
			edges.put(verticeIndexA, neighborListForA);
		}
		List<Integer> neighborListOfA =  edges.get(verticeIndexA);
		if (!neighborListOfA.contains(verticeIndexB))
			neighborListOfA.add(verticeIndexB);
		edges.put(verticeIndexA, neighborListOfA);
	}
	
	public void addEdge(int verticeIndexA, int verticeIndexB) {
		addEdgeToBToAList(verticeIndexA, verticeIndexB);
		addEdgeToBToAList(verticeIndexB, verticeIndexA);
	}
	
	public boolean isGraphEmpty() {
		return this.vertices.isEmpty();
	}
		
}
