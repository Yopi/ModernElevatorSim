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
	
	public Simulator(int numElevators, int numPersons, String filename) {
		// Read a graph from a text file.
		graph = new Graph(8);
		graph.addEdge(0, 1, 4);
		graph.addEdge(1, 2, 4);
		graph.addEdge(2, 3, 4);
		graph.addEdge(3, 4, 1);
		graph.addEdge(4, 5, 4);
		graph.addEdge(5, 6, 4);
		graph.addEdge(6, 7, 4);
		
		building = new Building(graph);
		
		
	}
	
	public static void main(String[] args) {
		// Sanitize input and then start the simulation.
		if (args.length >= 3) {
			try {
				new Simulator(Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
			} catch (Exception e) {
				System.err.println("Simulation failed to start, error: " + e);
			}
		} else {
			System.err.println("Bad parameters, correct use: java Simulator filename_graph number_elevators number_persons");
		}
	}

}
