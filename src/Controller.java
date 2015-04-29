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
	public static boolean DEBUG = Simulator.DEBUG.Controller;
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
		if(DEBUG) System.out.println("Elevators: " + Arrays.toString(elevators));
	}
	
	/*
	 * Method to call for an elevator.
	 * Might be the only public method of this class.
	 * @param: source and destination for the traveller
	 * @returns: true if the parameters are good and false if the parameters are out of bounds.
	 */
	public boolean requestElevator(int from, int to, int id, int time) {
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
			if(DEBUG) System.out.println("Nearest car heuristic.");
			nearestCar(from, to, id, time);
		} else if (ACTIVE_ALGORITHM == ALGORITHM_SEARCH) {
			if (DEBUG) System.out.println("Search based heuristic.");
			searchBased(from, to, id, time);
		} else if (ACTIVE_ALGORITHM == ALGORITHM_ZONE) {
			if (DEBUG) System.out.println("Zone based heuristic.");
			if (DEBUG) System.out.println("Current zone assignment: " + Arrays.toString(elevatorInZone));
			zoneBased(from, to, id, time);
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
				if(DEBUG) System.out.println("elevator " + elevators[i].id + " was asked to move by elevator " + moving);
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
				jobs.add(new Job(-1, moveTo, -1, time));
				/*
				 * Här skulle vi kunna lägga in kod som ser till att den överger
				 * sin zon om det är så att moveTo ligger utanför den.
				 * TODO
				 */
				building.resetMove(elevators[i].id);

			}
		}
	}
	
	private void nearestCarTick(int time) {}
	private void searchTick(int time) {}

	// elevatorInZone[elevator ID] = zone ID
	// The tick method will assign elevators to zones, make sure no zones are empty 
	// ZONE SURVEILANCE -- WOOPOP WOOOP ITS THE SOUND OF THE POLICE
	private void zoneTick(int time) {
		// Verify that all elevators have zones and are within those zones
		
		// Find out which zones are not taken
		/*ArrayList<Integer> freeZones = originalZones;
		for(int z : elevatorInZone) {
			if(z >= 0) {
				freeZones.remove((Integer)z);
			}
		}*/
		
		ArrayList<Integer> elevatorsHasZones = new ArrayList<Integer>();
		boolean[] owns = new boolean[building.graph.getLoops().size()];
		for(int i = 0; i < building.graph.getLoops().size(); i++) {
			for(int z = 0; z < elevatorInZone.length; z++) {
				int zone = elevatorInZone[z];
				if(zone == i) { // There is an elevator that OWNS in this zone (i)
					elevatorsHasZones.add(z);
					// Check if elevator z is in their zone
					owns[i] = true;
				}
			}
		}
			
		for(int i = 0; i < building.graph.getLoops().size(); i++) {
			int[] nodesInZone = building.graph.getLoops().get(i);
			if (!owns[i]) {
				ArrayList<Elevator> elevatorToUse = new ArrayList<Elevator>();
				// Check all idle elevators
				for(Elevator e : elevators) {
					if(e.idle && e.jobs.isEmpty() && !elevatorsHasZones.contains(e.id)) {
						elevatorToUse.add(e);
					}
				}
				
				if(elevatorToUse.isEmpty()) return;
				
				int to = nodesInZone[0];
				int assigned = searchBased(-1, to, -1, elevatorToUse.toArray(new Elevator[elevatorToUse.size()]), time);
				elevatorInZone[assigned] = i;
				elevatorsHasZones.add((Integer)assigned);
				if (DEBUG) System.out.println("Elevator " + assigned + " was assigned to zone " + i);
			}
		}
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
	private void searchBased(int from, int to, int id, int time) {
		searchBased(from, to, id, elevators, time);
	}
	
	/**
	 * 
	 * @return the elevator ID of the assigned elevator
	 */
	private int searchBased(int from, int to, int id, Elevator[] elevators, int time) {
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
		Job job = new Job(from, to, id, time);
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
				int persons = elevators[i].persons.size();
				while (next != to) {
					int pen = penalty;
					for (int j = 0; j < jobs.size(); j++) {
						if (jobs.get(j).from == next || jobs.get(j).to == next) {
							
							if (from == next && persons >= elevators[i].MAX_PASSENGERS) {
								penalty += building.nodes.length;
							}
							
							if (jobs.get(j).from == next) {
								if (persons < elevators[i].MAX_PASSENGERS) {
									persons++;
								}
							} else {
								persons--;
							}
							
							if (pen < penalty) {
								penalty++;
							} else {
								penalty += 3;
							}
						}
					}
					next = building.getNextNodeInPath(next, to);
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
				int target;
				if (from < 0) {
					target = to;
				} else {
					target = from;
				}
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
		System.out.println(Arrays.toString(elevators));
		ArrayList<Job> jobs = elevators[mindex].getJobs();
		jobs.add(job);
		jobs = minimizeTravel(jobs, null, elevators[mindex].id, 0);
		elevators[mindex].setJobs(jobs);
		
		return elevators[mindex].id;
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
	private void zoneBased(int from, int to, int id, int time) {
		// Find out if person wants to go from/to one single zone

		// Get all the zones that this person is going from
		ArrayList<Integer> InZone = new ArrayList<Integer>();
		for (int i = 0; i < building.graph.getLoops().size(); i++) {
			int[] zone = building.graph.getLoops().get(i);
			for (int z : zone) {
				if (z == from) {
					InZone.add(i);
				}
			}
		}

		// This node did not belong to any zones
		// Just add all of the zones
		if (InZone.isEmpty()) {
			for (int i = 0; i < building.graph.getLoops().size(); i++) {
				InZone.add(i);
			}
		}

		// Check which elevators are assigned to the zones
		// elevatorInZone[elevator ID] = zone ID
		int elevatorID = -1;
		ArrayList<Elevator> elevatorToUse = new ArrayList<Elevator>();
		for (int i : InZone) { // i == zone
			for (int z = 0; z < elevatorInZone.length; z++) {
				if (elevatorInZone[z] == i) {
					elevatorToUse.add(elevators[z]);
				}
			}
		}

		// If no elevator is in a good place
		// take all of the elevators
		if (elevatorToUse.isEmpty()) {
			for (Elevator e : elevators) {
				elevatorToUse.add(e);
			}
		}

		int assigned = searchBased(from, to, id, elevatorToUse.toArray(new Elevator[elevatorToUse.size()]), time) ;
		elevatorInZone[assigned] = -1;
	}
	
	/*
	 * The nearest car heurestic.
	 * When a new request arises, it simply checks for 
	 * the nearest car and adds the job to that elevator.
	 */
	private void nearestCar(int from, int to, int id, int time) {
		
		Job job = new Job(from, to, id, time);
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
				/*while (next != to) {
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
					next = building.getNextNodeInPath(next, to);
				}*/

				if (DEBUG) System.out.println("Elevator "+elevators[i].id+" penalty for stopping: " + penalty);
				if (minDist > (postDist - preDist)) {
					minDist = (postDist - preDist);
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
		
		/*
		 * ==================================
		 */
		/*
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
		*/
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
				
				if(position == target) {
					arrived = true;
					continue;
				}
				int nextNode = building.getNextNodeInPath(position, target);
				//if (DEBUG) System.out.println("j[0][1]: " + jobs[0][1] + " j[0][2]: " + jobs[0][2] + "position: "+position+ " preDist: "+ distance);
				distance += building.getDistance(position, nextNode);
				//if (DEBUG) System.out.println("j[0][1]: " + jobs[0][1] + " j[0][2]: " + jobs[0][2] + "position: "+position+ " postDist: "+ distance);
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
			list.add(new Job(jobs.get(i).from, jobs.get(i).to, jobs.get(i).id, jobs.get(i).time));
		}
		return list;
	}
	
	private int[][] copyJobsToMatrix(ArrayList<Job> jobs) {
		int[][] copy = new int[jobs.size()][4];
		for (int j = 0; j < jobs.size(); j++) {
			copy[j][0] = jobs.get(j).id;
			copy[j][1] = jobs.get(j).from;
			copy[j][2] = jobs.get(j).to;
			copy[j][3] = jobs.get(j).time;
		}

		return copy;
	}
	
	private ArrayList<Job> matrixToJobs(int[][] jobs) {
		ArrayList<Job> copy = new ArrayList<Job>();
		for (int[] j : jobs) {
			Job job = new Job(j[1], j[2], j[0], j[3]);
			copy.add(job);
		}

		return copy;
	}
	
	/*
	 * Prints an array of jobs, to be able to see what happens.
	 */
	private void printJobList(ArrayList<Job> jobs) {
		if(!DEBUG) return;
		System.out.println("Jobs:");
		for (int i = 0; i < jobs.size(); i++) {
			System.out.println("==");
			System.out.println("from: " + jobs.get(i).from);
			System.out.println("to: " + jobs.get(i).to);
			System.out.println("id: " + jobs.get(i).id);
			System.out.println("time: " + jobs.get(i).time);
			System.out.println("==");
		}
	}
}
