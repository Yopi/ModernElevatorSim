/*
 * Class to represent a single directed graph with weighted edges to simulate length.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-24
 */


public class Graph {
	
	int numNodes;
	int numEdges;
	
	public Graph(int n) {
		numEdges = n;
	}
	
	/*
	 * Vi behöver utvärdera hur vi lagrar kanter. Matris är kanske en bra ide eftersom det kan lagra
	 * vikter på att naturligt sätt. Det kommer ju aldrig att vara stora grafer (lär alltid vara <100 hörn)
	 * så en matris kommer ju aldrig bli dålig i minnet typ.
	 * 
	 * Den här saniterar just nu bara input. Kan kanske göras bättre med matrisen, eftersom vi då
	 * direkt kan se var den skulle bli out of bounds.
	 */
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
		
		return true;
	}
	
}
