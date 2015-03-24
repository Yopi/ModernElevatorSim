/*
 * Class to represent a single directed graph with weighted edges to simulate length.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-24
 */
import java.util.*;

public class Graph {
	
	int numNodes;
	int numEdges;
	ArrayList<ArrayList<Integer>> graph;
	
	public Graph(int n) {
		numEdges = n;
		graph = new ArrayList<ArrayList<Integer>>(numEdges);
	}
	
	public boolean addEdge(int from, int to, int weight)  {
		// Sanitize the input.
		if (from < 0 || from > (numNodes - 1)) {
			System.err.println("Bad value of from: " + from + " in addEdge.");
			return false;
		} else if (to < 0 || to > (numNodes - 1)) {
			System.err.println("Bad value of to: " + to + " in addEdge.");
			return false;
		} else if (weight < 0) {
			System.err.println("Bad value of weight: " + weight + " in addEdge.");
			return false;
		}
		//graph.get(to).get(from) = 
		return true;
	}
	
}
