import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;


/*
 * Class to represent a single directed graph with weighted edges to simulate length.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-24
 */


public class Graph {
	public static boolean DEBUG = true;
	
	int numNodes;
	int numEdges;
	Node[] graph;
	int[][] shortestPath;
	
	
	/*
	 * Creates a graph with numNodes number of nodes with IDs ranging from 0 to numNodes - 1.
	 */
	public Graph(int numNodes) {
		this.numNodes = numNodes;
		shortestPath = new int[this.numNodes][this.numNodes]; // [From][To]

		graph = new Node[this.numNodes];
		for(int i = 0; i < numNodes; i++) {
			graph[i] = new Node(i);
		}
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
				graph[from].addEdge(graph[to], weight);
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
		if(from == to) return 0;
		
		if (checkIndexes(from, to))
			return graph[from].getWeight(graph[to]);
		else
			return -1;
	}
	
	public void addLoop(int[] nodes) {
				
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
		if (from < 0 || from >= graph.length) {
			System.err.println("Bad value of from: " + from + " in addEdge.");
			return false;
		} else if (to < 0 || to >= graph.length) {
			System.err.println("Bad value of to: " + to + " in addEdge.");
			return false;
		}
		return true;
	}
	
	
	/**
	 * Calculate the shortest path from/to each node and fill shortestPath matrix.
	 */
	public void calculateShortestPath() {
		for(int i = 0; i < getNumNodes(); i++) {
			for(int j = 0; j < getNumNodes(); j++) {
				if(i != j) {
					System.out.println("From: " + i + " to " + j);
					Integer[] p = dijkstra(i, j);
					if(p != null) {
						shortestPath[i][j] = p[0];
					} else {
						shortestPath[i][j] = -1;
					}
				} else {
					shortestPath[i][j] = i;
				}
			}
		}
		
		if(DEBUG) {
			for(int i = 0; i < getNumNodes(); i++) {
				System.out.println(i + ": " + Arrays.toString(shortestPath[i]));
			}
		}
	}
	
	private Integer[] dijkstra(int source, int to) {
		//ArrayList<Integer> dist = new ArrayList<Integer>();
		//ArrayList<Integer> prev = new ArrayList<Integer>();
		int[] dist = new int[getNumNodes()];
		Node[] prev = new Node[getNumNodes()];
		ArrayList<Node> Q = new ArrayList<Node>();

		for(Node v_n : graph) {
			int v = v_n.getID();
			if(v != source) {
				dist[v] = Integer.MAX_VALUE - 1;
				prev[v] = null;
			}
			
			Q.add(v_n);
		}
		dist[source] = 0;

		Node v_n = null;
		while(!Q.isEmpty()) {
			int v = -1;
			int min = Integer.MAX_VALUE - 1;
			for(Node vert : Q) {
				v = vert.getID();
				if(dist[v] < min) {
					v_n = vert;
					min = dist[v];
				}
			}
			
			v = v_n.getID();
			if(v == to) break;
			Q.remove(v_n);

			for(Node node : v_n.getNeighbours().keySet()) {
				int newDist = dist[v] + graph[v].getWeight(node);
				if (newDist < dist[node.getID()]) {
					dist[node.getID()] = newDist;
					prev[node.getID()] = v_n;
				}
			}
		}
				
		ArrayList<Integer> S = new ArrayList<Integer>();
		int v = to;
		while(prev[v] != null) {
			S.add(v);
			v_n = prev[v];
			v = v_n.getID();
		}
		Collections.reverse(S);
		return S.toArray(new Integer[S.size()]);
	}
	
	public ArrayList<Integer> getNodeNeighbours(int node) {
		Set<Node> nodes = graph[node].getNeighbours().keySet();
		ArrayList<Integer> nodeList = new ArrayList<Integer>();
		for(Node n : nodes) {
			nodeList.add(n.getID());
		}
		
		return nodeList;
	}
	
	private class Node {
		int id;
		HashMap<Node, Integer> neighbours = new HashMap<Node, Integer>();
		public Node(int i) {
			id = i;
		}
		
		public int getID() {
			return id;
		}
		
		public HashMap<Node, Integer> getNeighbours() {
			return neighbours;
		}
		
		
		public void addEdge(Node n, int weight) {
			neighbours.put(n, weight);
		}
		
		public int getWeight(Node n) {
			return neighbours.get(n);
		}
		
		public String toString() {
			return "" + id;
		}
	}
}
