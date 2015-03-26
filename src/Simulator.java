/*
 * Main class for the simulator, creates all the other classes and triggers the ticks
 * in the eventloop.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */

public class Simulator {
	
	Elevator[] elevators;
	Person[] persons;
	Building building;
	Graph graph;
	
	public Simulator() {
		building = new Building();
		// Read a graph from a text file.
		graph = new Graph(8);
		graph.addEdge(0, 1, 4);
		graph.addEdge(1, 2, 4);
		graph.addEdge(2, 3, 4);
		graph.addEdge(3, 4, 1);
		graph.addEdge(4, 5, 4);
		graph.addEdge(5, 6, 4);
		graph.addEdge(6, 7, 4);
	}
	
	public static void main(String[] args) {
		// feck static.
		new Simulator();
	}

}
