/*
 * Main class for the simulator, creates all the other classes and triggers the ticks
 * in the eventloop.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */
import java.util.Random;

public class Simulator {
	Statistics stats;
	Elevator[] elevators;
	Person[] persons;
	Building building;
	Graph graph;
	Controller controller;
	Random rand;
	double second = 1;	// How many ticks required for a second. What am i doing?
	double hour = 3600 * second;
	int time;
	int hours = 0;
	int days = 1;

	public Simulator(String filename, int numPersons, int numElevators) {
		rand = new Random();
		graph = createGraphOne(); //new Graph(8);
		stats = new Statistics("/tmp/database.db", second);
		building = new Building(graph);
		elevators = new Elevator[numElevators];
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(8, building);
		}
		
		controller = new Controller(elevators, building);
		persons = new Person[numPersons];
		for (int i = 0; i < persons.length; i++) {
			persons[i] = new Person(i, building, stats, rand, second);		
		}
		
		/*
		 * When the building, elevators and persons are created it is time to start ticking.
		 */
		int limit = ((int)hour * hours) + ((int)hour * 24 * days);
		
		for (time = 0; time < limit; time++) {
			// Here is where the ticks will be made.
			// To consider: The time ticks during the night will be useless and that
			// should perhaps be dealt with to get a faster simulation.
		}
	}
	
	public static void main(String[] args) {
		// Sanitize input and then start the simulation.
		if (args.length >= 3) {
			try {
				new Simulator(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			} catch (Exception e) {
				System.err.println("Simulation failed to start, error: " + e);
			}
		} else {
			System.err.println("Bad parameters, correct use: java Simulator filename_graph number_elevators number_persons");
		}
	}
	
	/*
	 * Creates a predefined graph.
	 * 
	 * Tanke: Ska denna även skapa hur många hissar som ska agera? Nej, det borde vara en separat grej.
	 * Det är ju ett argument ja.. heh. nvm.
	 */
	private Graph createGraphOne() {
		Graph graph = new Graph(8);
		graph.addEdge(0, 1, 4);
		graph.addEdge(1, 2, 4);
		graph.addEdge(2, 3, 4);
		graph.addEdge(3, 4, 1);
		graph.addEdge(4, 5, 4);
		graph.addEdge(5, 6, 4);
		graph.addEdge(6, 7, 4);
		graph.addEdge(7, 0, 1);
		return graph;
	}

}
