/*
 * Main class for the simulator, creates all the other classes and triggers the ticks
 * in the eventloop.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */
import java.util.Random;

public class Simulator {
	
	Elevator[] elevators;
	Person[] persons;
	Building building;
	Graph graph;
	Random rand;
	double second = 1;	// How many ticks required for a second. What am i doing?
	
	public Simulator(String filename, int numPersons, int numElevators) {
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
		
		elevators = new Elevator[numElevators];
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(8, building);
		}
		
		rand = new Random();
		persons = new Person[numPersons];
		for (int i = 0; i < persons.length; i++) {
			double beginWork = rand.nextGaussian() * (900 * second);
			double endWork = rand.nextGaussian() * (900 * second);
			double lunchTime = rand.nextGaussian() * (3600 * second);
			int numMeetings = rand.nextInt(5);
			int[] meetings = new int[numMeetings];
			int interval = 9*60*60 / numMeetings; 
			for (int j = 0; j < meetings.length; j++) {
				meetings[j] = rand.nextInt(interval) + (interval * j);
			}
			persons[i] = new Person((int)beginWork, (int)endWork, (int)lunchTime, meetings);
		}
		
		
	}
	
	public static void main(String[] args) {
		// Sanitize input and then start the simulation.
		if (args.length >= 3) {
			try {
				new Simulator(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} catch (Exception e) {
				System.err.println("Simulation failed to start, error: " + e);
			}
		} else {
			System.err.println("Bad parameters, correct use: java Simulator filename_graph number_elevators number_persons");
		}
	}

}
