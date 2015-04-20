import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/*
 * Class to control the elevators and give the person class
 * an API to call for an elevator.
 * 
 * TODO:
 * 1. Figure out how the heuristics are used in the simulation (Code-wise)
 * check - 1a. How 2 hook into elevator / person
 * 		This is fixed within the elevator.
 * 2. Implement a first heuristic
 * 3. Make sure to re-arange an elevators current jobs to prioritize among them.
 * 4. Figure out how to ensure fairness (a job is always taken care of)
 * 5. Tell an elevator to get a move on by adding a new job with only a target and
 * 		-1 as from, the elevator will be set to ignore these.
 * 6. Write tick method to check for elevators that should move.
 * 
 * Vad ska kontrollern göra.
 * 1. Den ska para ihop request med hiss efter heurestiker.
 * 		- När en hiss får ett nytt jobb måste kontrollern ordna
 * 		så att den placerar jobben i ordningen som hissen ska färdas.
 * 		Detta borde gå att lösa med en slags sökning över hur sträckan
 * 		minimeras. Typ en omväg för att lämna av en person för att det
 * 		ska gå lättare på annat håll. Eller, omvägar kan behövas
 *		tas för att den totala sträckan ska bli lägre. Om den har
 *		koppling till building (som den måste ha för att läsa om
 *		en hiss ska flytta på sig, se punkt 2) så kan den
 *		ta reda på alla avstånd som den behöver kunna ta reda på.
 * 2. Den ska kontrollera om några hissar fått förfrågan för att flytta på sig
 * 
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-30
 */
public class Controller {
	public static boolean DEBUG = true;
	private int ACTIVE_ALGORITHM = -1;
	static final int ALGORITHM_NC = 1;
	static final int ALGORITHM_ZONE = 2;
	static final int ALGORITHM_SEARCH = 3;	

	
	Elevator[] elevators;
	Building building;
	int[] elevatorInZone;
	ArrayList<Integer> originalZones = new ArrayList<Integer>();
	
	public Controller(Elevator[] elevators, Building building, int algorithm) {
		this.elevators = elevators;
		this.building = building;
		ACTIVE_ALGORITHM = algorithm;
		elevatorInZone = new int[building.graph.getLoops().size()];
		for(int i = 0; i < elevatorInZone.length; i++) { elevatorInZone[i] = -1;  }
		for(int i = 0; i < building.graph.getLoops().size(); i++) { originalZones.add(i); }
	}
	
	/*
	 * Method to call for an elevator.
	 * Might be the only public method of this class.
	 * @param: source and destination for the traveller
	 * @returns: true if the parameters are good and false if the parameters are out of bounds.
	 */
	public boolean requestElevator(int from, int to, int id) {
		if (!(building.legalNode(from) && building.legalNode(to))) {
			System.err.println("Illegal nodes from person "+ id);
			return false;
		}
		if(DEBUG) {
			System.out.println("===============");
			System.out.println("Adding JOB: " + from + " -> " + to);
			System.out.println("===============");
		}
		if (ACTIVE_ALGORITHM == ALGORITHM_NC) {
			System.out.println("Nearest car heuristic.");
			nearestCar(from, to, id);
		} else if (ACTIVE_ALGORITHM == ALGORITHM_SEARCH) {
			if (DEBUG) System.out.println("Search based heuristic.");
			searchBased(from, to, id);
		}
		return true;
	}
	
	/*
	 * Tick method for the controller.
	 * This is needed because the controller needs to run some
	 * code independently, in order to take care of
	 * cabins that have been asked to move by another cabin.
	 * @param: time variable, not sure if it is needed though.
	 * @returns: void
	 */
	public void tick(int time) {
		if(ACTIVE_ALGORITHM == ALGORITHM_NC) {
			nearestCarTick(time);
		} else if(ACTIVE_ALGORITHM == ALGORITHM_ZONE) {
			zoneTick(time);
		} else if(ACTIVE_ALGORITHM == ALGORITHM_SEARCH) {
			searchTick(time);
		}
		
		int moving;
		ArrayList<Integer> neighbors;
		int moveTo = -1;
		for (int i = 0; i < elevators.length; i++) {
			if (!elevators[i].idle) {
				// The elevator is working on stuff. Let it do so.
				moving = building.elevatorMovedBy(elevators[i].id);
				if (moving >= 0) {
					building.resetMove(elevators[i].id);
				}
				continue;
			}
			
			moving = building.elevatorMovedBy(elevators[i].id);
			if (moving >= 0) {
				System.out.println("elevator " + elevators[i].id + " was asked to move by elevator " + moving);
				neighbors = building.getNodeNeighbours(elevators[i].getNextNode());
				if (elevators[moving].getJobs().size() == 0) {
					building.resetMove(elevators[i].id);
					continue;
				}
	
				for (int j = 0; j < neighbors.size(); j++) {
					if (elevators[moving].getJobs().get(0).from >= 0) {
						if (building.getNextNodeInPath(elevators[i].nextNode, elevators[moving].getJobs().get(0).from) != neighbors.get(j)) {
							moveTo = neighbors.get(j);
							break;
						}
					} else {
						if (building.getNextNodeInPath(elevators[i].nextNode, elevators[moving].getJobs().get(0).to) != neighbors.get(j)) {
							moveTo = neighbors.get(j);
							break;
						}
					}
				}
				if (moveTo < 0) {
					moveTo = neighbors.get(0);
				}
				// Move the elevator to moveTo
				ArrayList<Job> jobs = elevators[i].getJobs();
				jobs.add(new Job(-1, moveTo, -1));
				building.resetMove(elevators[i].id);

			}
		}
	}
	
	private void nearestCarTick(int time) {}
	private void searchTick(int time) {}

	// elevatorInZone[elevator ID] = zone ID
	private void zoneTick(int time) {
		// Verify that all elevators have zones and are within those zones
		
		// Find out which zones are not take
		ArrayList<Integer> freeZones = originalZones;
		for(int z : elevatorInZone) {
			if(z >= 0) {
				freeZones.remove((Integer)z);
			}
		}
		
		for (int i = 0; i < elevators.length; i++) {
			Elevator e = elevators[i];
			int next = e.getNextNode();
			if(elevatorInZone[e.id] == -1) {
				if(freeZones.isEmpty()) {
					// This should not happen :-P
					System.err.println("You have too many elevators for ye zones"); 
					break; 
				}

				// We must get them a zone
				if(e.getJobs().size() == 0) {
					// Assign the first free zone to them
					elevatorInZone[e.id] = freeZones.get(0);
					freeZones.remove(0);
				}
			}
			
			
			// Make sure elevator is in their zone
			boolean inLoop = false;
			for(int n : building.graph.getLoops().get(elevatorInZone[e.id])) {
				if(next == n) {
					inLoop = true;
				}
			}
			
			if(!inLoop) {
				// Find out closest node in zone
				HashMap<Integer, Integer> nodeFreq = new HashMap<Integer, Integer>();
				for(int z : building.graph.getLoops().get(elevatorInZone[e.id])) {
					int nextPath = building.getNextNodeInPath(e.nextNode, z);
					int freq = nodeFreq.get(nextPath);
					nodeFreq.put(nextPath, freq + 1);
				}
				Comparator<Map.Entry<Integer, Integer>> comparator =
					new Comparator<Map.Entry<Integer, Integer>>() {
						public int compare(
							Map.Entry<Integer, Integer> e1, Map.Entry<Integer, Integer> e2) {
					      return e1.getValue().compareTo(e2.getValue());
					    }
					};
				int goTo = Collections.max(nodeFreq.entrySet(), comparator).getKey();
				
				// Go there
				e.addJob(-1, goTo, -1);
			}
		}*/
	}
	
	
	
	
	/**
	 * Search based heuristic.
	 * It aims to minimize the waiting times for the persons
	 * by minimizing travel distances and parallelising the 
	 * jobs.
	 * @param from
	 * @param to
	 * @param id
	 */
	private void searchBased(int from, int to, int id) {
		searchBased(from, to, id, elevators);
	}
	
	private void searchBased(int from, int to, int id, Elevator[] elevators) {
		/*
		 * Look for the difference in distance for all elevators
		 * for their jobs if they get the new job. 
		 * Den prioriteringne ligger iofs i att minimera rörelse.
		 * Bättre borde ju vara att ge det till hissen som med
		 * lite arbete kan ta sig till platsen, och sedan utföra
		 * det ganska så fort, för att minimera arbetet för åkaren.
		 * Samtidigt så kan ju närmaste hissen bli fett keff
		 * om den har massa jobb köade så att personen behöver
		 * vänta vid våningar på omvägar.
		 * 
		 * Så...
		 * (Ta hänsyn till min. distance att utföra jobbet)
		 * Om jobb -> Se hur distance påverkas av att lägga till
		 * extra jobbet.
		 * Om inget jobb -> Se vad distance är för att hämta upp personen
		 * och ge det en positiv fördel för att utföra det, eftersom
		 * det paralelliserar processen och inte låter andra vänta.
		 * Ingen positiv vikt nu, det är bättre om ett existerande
		 * jobb gör saker eftersom det annars kan riskera att det köas
		 * upp hissar vilket kan ge dumma väntetider när dörrar är öppna.
		 * 
		 * Diskussion: Rätt svårt att avgöra däremot hur det kommer
		 * leda till köer i hissystemet. Det går att avgöra just nu hur
		 * det kan se ut, att däremot i ett rörande system göra det
		 * skulle innebära att låta ticks gå och förutse vad varje
		 * hiss ska göra (görbart) men ja, då närmar vi oss någon slags total-
		 * sökning.
		 */
		Job job = new Job(from, to, id);
		int minDist = Integer.MAX_VALUE;
		int mindex = 0;
		for (int i = 0; i < elevators.length; i++) {
			ArrayList<Job> jobs = copyJobs(elevators[i].getJobs());
			if (jobs.size() > 0) {
				// The elevator has active jobs.
				int[][] jobsMatrix = copyJobsToMatrix(jobs);
				int preDist = distanceJobs(jobsMatrix, elevators[i].getNextNode());
				if (DEBUG) System.out.println("Elevator "+elevators[i].id+" distance to perform current work: " + preDist);
				jobs.add(job);
				jobsMatrix = copyJobsToMatrix(jobs);
				jobs = minimizeTravel(jobs, null, elevators[i].id, 0);
				int postDist = distanceJobs(jobsMatrix, elevators[i].getNextNode());
				if (DEBUG) System.out.println("Elevator "+elevators[i].id+" distance to perform added work: " + postDist);
				int penalty = 0;
				int next = elevators[i].getNextNode();
				while (next != to) {
					int pen = penalty;
					for (int j = 0; j < jobs.size(); j++) {
						if (jobs.get(j).from == next || jobs.get(j).to == next) {
							if (pen < penalty) {
								penalty++;
							} else {
								penalty += 3;
							}
						}
					}
				}

				if (DEBUG) System.out.println("Elevator "+elevators[i].id+" penalty for stopping: " + penalty);
				if (minDist > (postDist - preDist) + penalty) {
					minDist = (postDist - preDist) + penalty;
					mindex = i;
				}
			} else {
				// The elevator has no active jobs.
				int dist = 0;
				int next = elevators[i].getNextNode();
				int target = from;
				while (next != target) {
					int tmp = next;
					next = building.getNextNodeInPath(next, target);
					dist += building.getDistance(tmp, next);
				}
				if (DEBUG) System.out.println("Elevator "+elevators[i].id+" is idle, distance to 'from': " + dist);
				// Distance is the distance for the elevator to travel to 'from'
				if (minDist > dist) {
					minDist = dist;
					mindex = i;
				}
			}
		}
		ArrayList<Job> jobs = elevators[mindex].getJobs();
		jobs.add(job);
		jobs = minimizeTravel(jobs, null, elevators[mindex].id, 0);
		elevators[mindex].setJobs(jobs);
	}
	
	/**
	 * Zone based heuristic.
	 * Divides the elevators into different zones in the graph and only 
	 * lets them accept jobs from within the zones. If they leave the zone
	 * another elevator should take its place, if possible.
	 * 
	 * @param from
	 * @param to
	 * @param id
	 */
	private void zoneBased(int from, int to, int id) {
		// Find out if person wants to go from/to one single zone
		/*boolean halfInZone = false;
		ArrayList<Integer> fullyInZone = new ArrayList<Integer>();
		for(int i = 0; i < building.graph.getLoops().size(); i++) {
			halfInZone = false;
			int[] zone = building.graph.getLoops().get(i);
			for(int z : zone) {
				if((z == from || z == to) && halfInZone) {
					fullyInZone.add(i);
					break;
				}

				if(z == from) {
					halfInZone = true;
				} else if(z == to) {
					halfInZone = true;
				}
			}
		}
		
		// If not, get the zone they're going from
		if(fullyInZone.isEmpty()) {
			for(int i = 0; i < building.graph.getLoops().size(); i++) {
				int[] zone = building.graph.getLoops().get(i);
				for(int z : zone) {
					if(z == from) {
						fullyInZone.add(i);
						break;
					}
				}
			}
		}
		
		if(fullyInZone.isEmpty()) {
			for(Elevator e : elevators) {
				fullyInZone.add(e.id);
			}
		}
		
		// Check which elevators are assigned to the zones
		// elevatorInZone[zoneID] = elevatorID
		for(int i : fullyInZone) {
			int elevatorIndex = -1;
			for (int j = 0; j < elevators.length; j++) {
				if (elevators[j].id == elevatorInZone[i]) {
					elevatorIndex = elevators[j].id;
					break;
				}
			}
			if (elevators[elevatorIndex].getJobs().size() > 0) {
				// It has jobs.
				
			}
			if(elevatorInZone[i]) {
				// Check if they have a job
				
			}
		}*/
	}
	
	/*
	 * The nearest car heurestic.
	 * When a new request arises, it simply checks for 
	 * the nearest car and adds the job to that elevator.
	 */
	private void nearestCar(int from, int to, int id) {
		double min = Double.MAX_VALUE;
		int mindex = 0;
		// Find the elevator with the lowest distance to the caller.
		for (int i = 0; i < elevators.length; i++) {
			int next = elevators[i].getNextNode();
			double dist = 0d;
			// If the elevator is between nodes, this is relevant. Add it to the distance.
			if (elevators[i].position > 0.01) {
				dist = building.getDistance(elevators[i].getPrevNode(), elevators[i].getNextNode()) - elevators[i].position;
			}
			// Now get the total distance from the position of the elevator and the caller
			while (next != from) {
				dist += building.getDistance(next, building.getNextNodeInPath(next, from));
				next = building.getNextNodeInPath(next, from);
			}
			// Check if it is the smallest so far.
			if (min > dist) {
				min = dist;
				mindex = i;
			}
		}
		// We now have the elevator with the smallest distance to the caller.
		// Add it to its jobs.
		elevators[mindex].addJob(from, to, id);

		// If there are multiple jobs, sort them
		if (elevators[mindex].getJobs().size() > 1) {
			if (DEBUG) {
				System.out.println("Old jobs:");
				printJobList(elevators[mindex].getJobs());
			}
			ArrayList<Job> minJobs = minimizeTravel(elevators[mindex].getJobs(), null, mindex, 0);
			elevators[mindex].setJobs(minJobs);
			if (DEBUG) {
				System.out.println("New (sorted) jobs:");
				printJobList(minJobs);
			}
			//elevators[mindex].addJob(from, to, id);
			//ArrayList<Job> jobs = elevators[mindex].getJobs();
		}
	}
	
	/*
	 * Minimizes the travel distance by rearanging the jobs for the elevator.
	 * @param: ArrayList with the jobs.
	 * @returns: The rearanged arraylist.
	 * TODO OPTIMIZE
	 */
	public ArrayList<Job> minimizeTravel(ArrayList<Job> jobs, int[][] sortedJobs, int eid, int nextIndex) {
		//System.out.println("---");
		//System.out.println("minimize travel");
		//System.out.println("---");
		// Base case 1
		if(jobs.size() <= 1) {
			return jobs;
		}
		
		// Base case
		if(nextIndex >= jobs.size() - 1) {
			// Sortera
			for(int i = 0; i < sortedJobs.length; i++) {
				int id = sortedJobs[i][0];
				
				for(int j = 0; j < jobs.size(); j++) {
					Job job = jobs.get(j);
					if (job.id == id) {
						jobs.remove(job);
						jobs.add(i, job);
					}
				}
			}
			return jobs;
		}
		
		// Recursive case
		int[][] minJobs = new int[0][0];
		int dist;
		int min = Integer.MAX_VALUE;
		for (int i = 0; i < jobs.size(); i++) {
			int[][] jobMatrix = copyJobsToMatrix(jobs);

			int[] tmpJob = jobMatrix[i];
			jobMatrix[i] = jobMatrix[nextIndex];
			jobMatrix[nextIndex] = tmpJob;

			dist = distanceJobs(jobMatrix, eid);
			printJobList(jobs);
			if (min > dist) {
				min = dist;
				minJobs = jobMatrix;
			}
		}

		for(int i = nextIndex; i < minJobs.length; i++) {
			nextIndex = i;
			if(minJobs[i][1] >= 0 || minJobs[i][2] >= 0) { 
				break;
			} 
		}

		return minimizeTravel(jobs, minJobs, eid, nextIndex);
	}
	
	private int distanceJobs(int[][] jobs, int position) {
		//System.out.println("---");
		//System.out.println("Distance jobs");
		//System.out.println("---");
		//try { Thread.sleep(100); } catch (Exception e) {}
		int distance = 0;
		int counter = 0;
		int currentJob = 0;
		for(int[] j : jobs) {
			if(j[1] < 0 && j[2] < 0) { // Both to and from = -1
				counter++;
			} else {
				break;
			}
			
			currentJob++;
		}
		
		// Base case
		if(counter == jobs.length) {
			return 0;
		}
		
		// Recursive case
		while(jobs[currentJob][1] >= 0 || jobs[currentJob][2] >= 0) {
			int target = (jobs[currentJob][1] >= 0) ? jobs[currentJob][1] : jobs[currentJob][2];
			boolean arrived = false;
			while(position != target || !arrived) {
				for(int[] j : jobs) {
					// If position is at one of the elevators from
					if(j[1] == position) {
						j[1] = -1;
					}
					
					// If position is at one of the elevators to, and the passenger is picked up
					if(j[2] == position && j[1] == -1) {
						j[2] = -1;
					}
				}
				
				if(position == target) arrived = true;
				int nextNode = building.getNextNodeInPath(position, target);
				distance += building.getDistance(position, nextNode);
				position = nextNode;
			}
		}

		/*
		System.out.println("===");
		for(int[] j : jobs) {
			System.out.println(Arrays.toString(j));
		}
		System.out.println("===");
		*/
		return distance + distanceJobs(jobs, position);
	}
	
	private ArrayList<Job> copyJobs(ArrayList<Job> jobs) {
		ArrayList<Job> list = new ArrayList<Job>();
		for (int i = 0; i < jobs.size(); i++) {
			list.add(new Job(jobs.get(i).from, jobs.get(i).to, jobs.get(i).id));
		}
		return list;
	}
	
	private int[][] copyJobsToMatrix(ArrayList<Job> jobs) {
		int[][] copy = new int[jobs.size()][3];
		for (int j = 0; j < jobs.size(); j++) {
			copy[j][0] = jobs.get(j).id;
			copy[j][1] = jobs.get(j).from;
			copy[j][2] = jobs.get(j).to;
		}

		return copy;
	}
	
	private ArrayList<Job> matrixToJobs(int[][] jobs) {
		ArrayList<Job> copy = new ArrayList<Job>();
		for (int[] j : jobs) {
			Job job = new Job(j[1], j[2], j[0]);
			copy.add(job);
		}

		return copy;
	}
	
	/*
	 * Prints an array of jobs, to be able to see what happens.
	 */
	private void printJobList(ArrayList<Job> jobs) {
		System.out.println("Jobs:");
		for (int i = 0; i < jobs.size(); i++) {
			System.out.println("==");
			System.out.println("from: " + jobs.get(i).from);
			System.out.println("to: " + jobs.get(i).to);
			System.out.println("id: " + jobs.get(i).id);
			System.out.println("==");
		}
	}
}
