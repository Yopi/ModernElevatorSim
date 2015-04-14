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
		if (checkIndexes(from, to))
			return graph[from].getWeight(graph[to]);
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

		System.out.println(getNumNodes());
		for(int i = 0; i < getNumNodes(); i++) {
			for(int j = 0; j < getNumNodes(); j++) {
				if(i != j) {
					System.out.println("From: " + i + " to " + j);
					Integer[] p = dijkstra(i, j);
					if(p.length > 0) {
						shortestPath[i][j] = p[0];
					} else {
						shortestPath[i][j] = -1;
					}
				}
			}
		}
		
		// Print
		for(int[] i : shortestPath) {
			System.out.println(Arrays.toString(i));
		}
	}
	/**
  function Dijkstra(Graph, source):

      dist[source] ← 0                       // Distance from source to source
      prev[source] ← undefined               // Previous node in optimal path initialization

      for each vertex v in Graph:  // Initialization
          if v ≠ source            // Where v has not yet been removed from Q (unvisited nodes)
              dist[v] ← infinity             // Unknown distance function from source to v
              prev[v] ← undefined            // Previous node in optimal path from source
          end if 
          add v to Q                     // All nodes initially in Q (unvisited nodes)
      end for
      
      while Q is not empty:
          u ← vertex in Q with min dist[u]  // Source node in first case
          remove u from Q 
          
          for each neighbor v of u:           // where v is still in Q.
              alt ← dist[u] + length(u, v)
              if alt < dist[v]:               // A shorter path to v has been found
                  dist[v] ← alt 
                  prev[v] ← u 
              end if
          end for
      end while

      return dist[], prev[]

  end function
	 */
	private Integer[] dijkstra(int source, int to) {
		//ArrayList<Integer> dist = new ArrayList<Integer>();
		//ArrayList<Integer> prev = new ArrayList<Integer>();
		int[] dist = new int[getNumNodes()];
		Node[] prev = new Node[getNumNodes()];
		ArrayList<Node> Q = new ArrayList<Node>();

		for(Node v_n : graph) {
			int v = v_n.getID();
			if(v != source) {
				dist[source] = Integer.MAX_VALUE;
				prev[source] = null;
			}
			
			Q.add(v_n);
		}

		Node u = null;
		while(!Q.isEmpty()) {
			u = getLowestValue(Q, dist);
			Q.remove(u);
						
			int counter = 0;
			if(u == null) break;
			for(Node v : u.getNeighbours()) {
				int alt = dist[u.getID()] + graph[u.getID()].getWeight(v);
				if (alt < dist[v.getID()]) {
					dist[v.getID()] = alt;
					prev[v.getID()] = u;
				}
			}
		}
				
		ArrayList<Integer> S = new ArrayList<Integer>();
		while(prev[u.getID()] != null) {
			S.add(u.getID());
			u = prev[u.getID()];
		}
		Collections.reverse(S);
		return S.toArray(new Integer[S.size()]);
	}
	
	private Node getLowestValue(ArrayList<Node> Q, int[] dist) {
		int minDist = Integer.MAX_VALUE;
		Node u = null;

		for(Node i : Q) {
			if(dist[i.getID()] < minDist) {
				minDist = dist[i.getID()];
				u = i;
			}
		}
		
		return u;
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
	}
}
