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

class Debug {
	public boolean Building = false;
	public boolean Controller = true;
	public boolean Elevator = true;
	public boolean Graph = false;
	public boolean Meeting = false;
	public boolean Person = false;
}

public class Simulator {
	public static final Debug DEBUG = new Debug();

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
	static int days = 365;

	static final int ALGORITHM_NC = 1;
	static final int ALGORITHM_ZONE = 2;
	static final int ALGORITHM_SEARCH = 3;	
	public Simulator(String filename, int numPersons, int numElevators, int algorithm, int graphNo) {
		rand = new Random();
		switch(graphNo) {
			case 1:
				graph = createGraphOne();
				break;
			case 2:
				graph = createGraphTwo();
				break;
			case 3:
				graph = createGraphThree();
				break;
		}

		String fileName = "database_graph" + graphNo + "_days" + days + "_alg" + algorithm + "_persons" + numPersons + "_elevators" + numElevators + ".db";
		stats = new Statistics(fileName, second, algorithm, numPersons, numElevators);

		building = new Building(graph, numPersons, numElevators);
		elevators = new Elevator[numElevators];
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(i, building, i, second);
		}
		
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
		time = (int)(7 * hour);
		for (; time < limit; time++) {
			int localTime = time % (int)(hour * 24);
			//System.out.println("The time is: " + (int)(localTime/hour) + ":" + (int)(localTime/hour * 60 % 60) + ":" + (int)(localTime/hour * 3600 % 60));
			controller.tick(localTime);
			
			for(Elevator e : elevators) { e.tick(localTime); }
			for(Person p : persons) { p.tick(localTime); }

			
			try { Thread.sleep(1); } catch (Exception e) {}
			
		}

		try {
			stats.db.exec("COMMIT");
		} catch (Exception e) {}
		stats.db.dispose();
	}
	
	public static void main(String[] args) {
		/*for(int e = 1; e < 8; e++) {
			long startTime = System.currentTimeMillis();
			for(int i = 0; i < days; i++) {
				new Simulator("args[0]", 100, e, ALGORITHM_SEARCH);
			}
			long endTime = System.currentTimeMillis();
			System.out.println((endTime - startTime) + "ms");
			System.out.println("Nu är " + e + " DONE");
			try { Thread.sleep(5000); } catch (Exception fail) {}
		}*/

		// Testing
		long startTime = System.currentTimeMillis();
		new Simulator("args[0]", 2, 2, ALGORITHM_ZONE, 3);
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
		/* Simple ass graph */
		/*
		  _________________
		 |                 |
		 |   3 > 4         |
		 |   ^   v         |
		 |   2 > 5         |
		 |   ^   v         |
		 |   1 < 6 		   |
		 |   ^   v         |
		 |   0 < 7         |
		 |_________________|
		 
		 
		*/

		Graph graph = new Graph(8);

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

		//graph.addLoop(new int[]{0, 1, 2, 3, 4, 5, 6, 7});
		graph.addLoop(new int[]{0, 1, 2, 5, 6, 7});
		graph.addLoop(new int[]{1, 2, 3, 4, 5, 6});
		graph.addLoop(new int[]{1, 2, 5, 6});

		return graph;
	}
	
	private Graph createGraphTwo() {
		/* Expressen graph */
		/*_______________________
		 | 23> 8 >9 >10 >11 > 12 |
		 | ^   v  ^   v    ^   v | 
		 | 22< 7 <17 <16 <15 <13 |
		 | ^   v  ^   v    ^   v |
		 | 21> 6> 18> 19> 20> 14 |
		 | ^   v  ^   v    ^   v |
		 | 5 < 4 < 3 < 2 < 1 < 0 |
		 
		*/
		Graph graph = new Graph(24);
		
		graph.addEdge(0, 1, 3);
		graph.addEdge(1, 2, 3);
		graph.addEdge(2, 3, 3);
		graph.addEdge(3, 4, 3);
		graph.addEdge(4, 5, 3);
		graph.addEdge(5, 21, 3);
		graph.addEdge(21, 22, 3);
		graph.addEdge(22, 23, 3);
		graph.addEdge(23, 8, 3);
		graph.addEdge(6, 4, 3);
		graph.addEdge(7, 6, 3);
		graph.addEdge(8, 7, 3);
		graph.addEdge(8, 9, 3);
		graph.addEdge(9, 10, 3);
		graph.addEdge(10, 11, 3);
		graph.addEdge(11, 12, 3);
		graph.addEdge(12, 13, 3);
		graph.addEdge(13, 14, 3);
		graph.addEdge(14, 0, 3);
		graph.addEdge(13, 15, 3);
		graph.addEdge(15, 16, 3);
		graph.addEdge(16, 17, 3);
		graph.addEdge(17, 7, 3);
		graph.addEdge(7, 22, 3);
		graph.addEdge(21, 6, 3);
		graph.addEdge(6, 18, 3);
		graph.addEdge(18, 19, 3);
		graph.addEdge(19, 20, 3);
		graph.addEdge(20, 14, 3);

		graph.addLoop(new int[]{0, 1, 20, 14});
		graph.addLoop(new int[]{2, 3, 18, 19});
		graph.addLoop(new int[]{4, 5, 21, 6});
		graph.addLoop(new int[]{22, 23, 8, 7});
		graph.addLoop(new int[]{9, 10, 16, 17});
		graph.addLoop(new int[]{11, 12, 13, 15});
		graph.addLoop(new int[]{7, 6, 18, 17});
		graph.addLoop(new int[]{19, 20, 15, 16});
		graph.addLoop(new int[]{6, 18, 17, 7});
		graph.addLoop(new int[]{19, 20, 15, 16});

		return graph;
	}
	
	private Graph createGraphThree() { // High rise
		Graph graph = new Graph(28);
		
		graph.addEdge(0, 1, 3);
		graph.addEdge(1, 2, 3);
		graph.addEdge(2, 3, 3);
		graph.addEdge(3, 4, 3);
		graph.addEdge(4, 5, 3);
		graph.addEdge(5, 6, 3);
		graph.addEdge(6, 7, 3);
		graph.addEdge(7, 8, 3);
		graph.addEdge(8, 9, 3);
		graph.addEdge(9, 10, 3);
		graph.addEdge(10, 11, 3);
		graph.addEdge(11, 12, 3);
		graph.addEdge(12, 13, 3);
		graph.addEdge(13, 14, 3);
		graph.addEdge(14, 15, 3);
		graph.addEdge(15, 16, 3);
		graph.addEdge(16, 17, 3);
		graph.addEdge(17, 18, 3);
		graph.addEdge(18, 19, 3);
		graph.addEdge(19, 20, 3);
		graph.addEdge(20, 21, 3);
		graph.addEdge(21, 22, 3);
		graph.addEdge(22, 23, 3);
		graph.addEdge(23, 24, 3);
		graph.addEdge(24, 25, 3);
		graph.addEdge(25, 26, 3);
		graph.addEdge(26, 27, 3);
		graph.addEdge(27, 0, 3);
		
		// Horizontal yo
		graph.addEdge(1, 26, 3);
		graph.addEdge(25, 2, 3);
		graph.addEdge(3, 24, 3);
		graph.addEdge(23, 4, 3);
		graph.addEdge(5, 22, 3);
		graph.addEdge(21, 6, 3);
		graph.addEdge(7, 20, 3);
		graph.addEdge(19, 8, 3);
		graph.addEdge(9, 18, 3);
		graph.addEdge(17, 10, 3);
		graph.addEdge(11, 16, 3);
		graph.addEdge(15, 12, 3);
		
		graph.addLoop(new int[]{0, 1, 26, 27});
		graph.addLoop(new int[]{2, 3, 24, 25});
		graph.addLoop(new int[]{4, 5, 22, 23});
		graph.addLoop(new int[]{6, 7, 20, 21});
		graph.addLoop(new int[]{8, 9, 18, 19});
		graph.addLoop(new int[]{10, 11, 16, 17});
		graph.addLoop(new int[]{12, 13, 14, 15});
		return graph;
	}
}
