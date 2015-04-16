/*
 * Main class for the simulator, creates all the other classes and triggers the ticks
 * in the eventloop.
 * 
 * 
 * TODO:
 * 1. Call ticks on (elevators, persons)
 * 2. Create web server (Optional)
 * 3. Double check how second and ticks cooperate.
 * 4. Create alternative graphs for different buildings.
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
		stats = new Statistics("database.db", second);
		building = new Building(graph, numPersons, numElevators);
		elevators = new Elevator[numElevators];
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(i, building, i, second);
		}
		
		controller = new Controller(elevators, building);
		persons = new Person[numPersons];
		for (int i = 0; i < persons.length; i++) {
			persons[i] = new Person(i, building, controller, stats, rand, second);		
		}
		
		building.graph.calculateShortestPath();
		
		/*
		 * When the building, elevators and persons are created it is time to start ticking.
		 */
		int limit = ((int)hour * hours) + ((int)hour * 24 * days);
		time = (int)(8 * hour) - 1; 
		for (; time < limit; time++) {
			System.out.println("The time is: " + (int)(time/hour) + ":" + (int)(time/hour * 60 % 60) + ":" + (int)(time/hour * 3600 % 60));
			controller.tick(time);
			
			for(Elevator e : elevators) { e.tick(time); }
			for(Person p : persons) { p.tick(time); }
			
			System.out.println("");

			try {
				//Thread.sleep(30);
			} catch (Exception e) {}
		}
	}
	
	public static void main(String[] args) {
		// Sanitize input and then start the simulation.
		if (args.length >= 3) {
			new Simulator(args[0], 100, 2);
			
			//try {
			//new Simulator(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			//} catch (Exception e) {
			//	System.err.println("Simulation failed to start, error: " + e);
			//}
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
		/* 
		graph.addEdge(0, 1, 2);
		graph.addEdge(1, 2, 2);
		graph.addEdge(2, 3, 2);
		graph.addEdge(3, 4, 2);
		graph.addEdge(4, 5, 2);
		graph.addEdge(5, 6, 2);
		graph.addEdge(6, 7, 2);
		graph.addEdge(7, 8, 2);
		graph.addEdge(7, 10, 1);
		graph.addEdge(10, 2, 1);
		graph.addEdge(8, 9, 2);
		graph.addEdge(9, 0, 2);

		  _________________
		 |                 |
		 |   4       5     |
		 |                 |
		 |   3       6     |
		 |                 |
		 |   2   10  7     |
		 |                 |
		 |   1       8     |
		 |                 |
		 |   0       9     |
		 |_________________|
		 
		 
		*/
		graph.addEdge(0, 1, 3);
		graph.addEdge(1, 2, 3);
		graph.addEdge(2, 3, 3);
		graph.addEdge(2, 5, 2);
		graph.addEdge(3, 4, 2);
		graph.addEdge(4, 5, 3);
		graph.addEdge(5, 6, 3);
		graph.addEdge(6, 7, 3);
		graph.addEdge(6, 1, 2);
		graph.addEdge(7, 0, 2);

		
		return graph;
	}

}
