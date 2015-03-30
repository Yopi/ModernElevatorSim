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
	
	/*
	 * Creates a graph with numNodes number of nodes with IDs ranging from 0 to numNodes - 1.
	 */
	public Graph(int numNodes) {
		this.numNodes = numNodes-1;
		graph = new int[this.numNodes][this.numNodes];
	}
	
	/*
	 * Adds an edge to the graph.
	 * Returns true or false depending on if the add was successful.
	 * An add to a previous set edge (adding an edge that already exists) will overwrite the older one.
	 * @param: from (node), to (node) and weight.
	 * @returns: true or false depending on if the add was successful.
	 */
	public boolean addEdge(int from, int to, int weight)  {
		if (checkIndexes(from, to)) {
			if (weight < 1) {
				System.err.println("Bad value of weight: " + weight + " in addEdge.");
				return false;
			} else {
				graph[from][to] = weight;
				return true;
			}
		} else {
			return false;
		}
	}
	
	/*
	 * Returns the weight for an edge, and 0 if the edge doesn't exist.
	 * Returns -1 in the case of illegal indexes.
	 * @param: from and to (nodes)
	 * @returns: the weight of the edge or zero in the case of no edge.
	 */
	public int getEdgeWeight(int from, int to) {
		if (checkIndexes(from, to))
			return graph[from][to];
		else
			return -1;
	}
	
	/*
	 * Method to get the number of nodes in the graph.
	 * @param: None
	 * @Returns: The number of nodes in the graph.
	 */
	public int getNumNodes() {
		return numNodes;
	}
	
	/*
	 * Checks the indexes given to the graph to see that they are valid.
	 * @param: from, to
	 * @returns: true or false depending on if the indexes are in bounds.
	 */
	private boolean checkIndexes(int from, int to) {
		if (from < 0 || from > graph.length) {
			System.err.println("Bad value of from: " + from + " in addEdge.");
			return false;
		} else if (to < 0 || to > graph[0].length) {
			System.err.println("Bad value of to: " + to + " in addEdge.");
			return false;
		}
		return true;
	}
	
}
