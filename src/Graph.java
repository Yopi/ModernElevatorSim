/*
 * Class to represent a single directed graph with weighted edges to simulate length.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-24
 */


public class Graph {
	
	int numNodes;
	int numEdges;
	int[][] graph;
	
	public Graph(int n) {
		numEdges = n;
		graph = new int[numNodes][numNodes];
	}
	
	/*
	 * Adds an edge to the graph.
	 * Returns true or false depending on if the add was successful.
	 * @param: from (node), to (node) and weight.
	 * @returns: true or false depending on if the add was successful.
	 */
	public boolean addEdge(int from, int to, int weight)  {
		// Sanitize the input.
		if (from < 0 || from > graph.length) {
			System.err.println("Bad value of from: " + from + " in addEdge.");
			return false;
		} else if (to < 0 || to > graph[0].length) {
			System.err.println("Bad value of to: " + to + " in addEdge.");
			return false;
		} else if (weight < 1) {
			System.err.println("Bad value of weight: " + weight + " in addEdge.");
			return false;
		}
		graph[from][to] = weight;
		return true;
	}
	
	/*
	 * Returns the weight for an edge, and 0 if the edge doesn't exist.
	 * Returns -1 in the case of illegal indexes.
	 * @param: from and to (nodes)
	 * @returns: the weight of the edge or zero in the case of no edge.
	 */
	public int getEdgeWeight(int from, int to) {
		return graph[from][to];
	}
	
}
