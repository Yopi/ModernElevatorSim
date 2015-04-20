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
	static int days = 10;

	static final int ALGORITHM_NC = 1;
	static final int ALGORITHM_ZONE = 2;
	static final int ALGORITHM_SEARCH = 3;	
	public Simulator(String filename, int numPersons, int numElevators, int algorithm) {
		rand = new Random();
		graph = createGraphOne(); //new Graph(8);
		stats = new Statistics("database.db", second, algorithm);
		building = new Building(graph, numPersons, numElevators);
		elevators = new Elevator[numElevators];
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(i, building, i, second);
		}
		elevators[0].addJob(-1, 6, -1);
		elevators[1].addJob(-1, 7, -1);
		//elevators[0] = new Elevator(6, building, 0, second);
		//elevators[1] = new Elevator(7, building, 1, second);
		
		controller = new Controller(elevators, building, algorithm);
		persons = new Person[numPersons];
		for (int i = 0; i < persons.length; i++) {
			persons[i] = new Person(i, building, controller, stats, rand, second);		
		}
		
		building.graph.calculateShortestPath();
		
		/*
		 * When the building, elevators and persons are created it is time to start ticking.
		 */
		int limit = ((int)hour * hours) + ((int)hour * 24);
		time = (int)(8 * hour) - 30; 
		for (; time < limit; time++) {
			int localTime = time % (int)(hour * 24);
			//System.out.println("The time is: " + (int)(localTime/hour) + ":" + (int)(localTime/hour * 60 % 60) + ":" + (int)(localTime/hour * 3600 % 60));
			controller.tick(localTime);
			
			for(Elevator e : elevators) { e.tick(localTime); }
			for(Person p : persons) { p.tick(localTime); }

			
			try {
				//Thread.sleep(900);
			} catch (Exception e) {}
			
		}
		stats.db.dispose();
	}
	
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < days; i++) {
			new Simulator("args[0]", 2, 2, ALGORITHM_SEARCH);
		}
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime) + "ms");
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
		 |   3 > 4         |
		 |   ^   v         |
		 |   2 > 5         |
		 |   ^   v         |
		 |   1 < 6 		   |
		 |   ^   v         |
		 |   0 > 7         |
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
		
		graph.addLoop(new int[]{0, 1, 2, 5, 6, 7});
		graph.addLoop(new int[]{1, 2, 3, 4, 5, 6});
		graph.addLoop(new int[]{1, 2, 5, 6});

		
		return graph;
	}

}
