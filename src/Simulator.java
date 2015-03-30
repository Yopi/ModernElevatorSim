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
	Controller controller;
	Random rand;
	double second = 1;	// How many ticks required for a second. What am i doing?
	double hour = 3600 * second;
	int maxMeetings = 5;
	int time;
	int hours = 0;
	int days = 1;
	
	public Simulator(String filename, int numPersons, int numElevators) {
		
		time = 0;
		
		graph = createGraphOne(); //new Graph(8);
		
		building = new Building(graph);
		
		elevators = new Elevator[numElevators];
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(8, building);
		}
		
		rand = new Random();
		controller = new Controller(elevators, building);
		
		persons = new Person[numPersons];
		Meeting[] meetings = new Meeting[maxMeetings];
		int interval = ((int)hour * (17-8)) / maxMeetings;
		int first = (int)(hour * 8) + interval / 2;
		for (int i = 0; i < meetings.length; i++) {
			meetings[i] = new Meeting((first + (interval * i)), rand.nextInt(graph.getNumNodes()));
		}
		
		for (int i = 0; i < persons.length; i++) {
			int beginWork = (int)((hour * 8) + (rand.nextGaussian() * (900 * second)));	// Random time for arrival at work, +- 15 minutes, 900 seconds.
			int endWork = (int)((hour * 17) + rand.nextGaussian() * (900 * second));		// Random time for leaving work, +- 15 minutes.
			int lunchTime = (int)((hour * 12) + (rand.nextGaussian() * hour));	// Random time for lunch, +- 1 hour.
			int numMeetings = rand.nextInt(maxMeetings);	// Random number of meetings for a worker.
			Meeting[] myMeetings = new Meeting[numMeetings];
			for (int j = 0; j < meetings.length; j++) {
				myMeetings[j] = meetings[j];
			}
			persons[i] = new Person(beginWork, endWork, lunchTime, meetings, rand.nextInt(graph.getNumNodes()), second);
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
				new Simulator(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
			} catch (Exception e) {
				System.err.println("Simulation failed to start, error: " + e);
			}
		} else {
			System.err.println("Bad parameters, correct use: java Simulator filename_graph number_elevators number_persons");
		}
	}
	
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
