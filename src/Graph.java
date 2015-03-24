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
	
	
}
