/*
 * Class to represent the elevator cabins in the simulation.
 * 
 * TODO:
 * 1. Handle getting a job.
 * 2. Handle idle behaviour (from heuristic?)
 * 3. Max passenger count
 * 4. 
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-24
 */
import java.util.ArrayList;


/*
 * TODO:
 * 1. Fortsätt på tick-metoden
 * 1.1 Kolla efter ändringar från controllern via change-variabeln, och lägga till det. Agera på det? Läsa det och göra vad det säger.
 * 1.2 Ta hand om aktuellt arbete, röra sig framåt.
 * 1.3 Om idle... What do?
 * 2. Vad som nu dyker upp när tick-metoden skrivits.
 */

public class Elevator {
	
	final int ADDED_JOB = 0b1;
	final int maxPassengers = 7;	// Maximum number of passengers
	
	// Private fields
	int id;		// Id for the elevator
	int nextNode;	// The next node that the elevator will reach.
	int prevNode;	// The previous node that the elevator visited.
	int target;		// The goal of the elevator, where it is traveling to.
	int numDestinations; // The number of nodes. One node = 1 destination.
	int passengers;	// Number of passengers in the elevator.
	
	double position;	// The progress of the elevator between two nodes.
	
	boolean idle;	// True if the elevator has no active jobs and is standing still.
	boolean moving;		// If the elevator is moving or not. Where it is moving can be found by nextNode and prevNode.
	
	ArrayList<Job> jobs;	// The active jobs for the elevator
	
	Building building;	// The building that the elevator is in.
	
	/*
	 * Constructor for the elevator class.
	 * @param: The number of nodes within the system
	 */
	public Elevator(int numDestinations, Building building, int id) {
		this.id = id;
		nextNode = -1;
		prevNode = -1;
		position = 0;
		target = -1;
		passengers = 0;
		change = 0;
		moving = false;
		idle = true;
		this.numDestinations = numDestinations;
		jobs = new ArrayList<Job>();
		this.building = building;
	}
	
	private class Job {
		
		int from;	// From what node the job origins
		int to;		// To what node the job is finished.
		int id;		// The person that called for the job.
		
					// The job does not store path since this might change
					// during the job if new jobs are added.
				
		public Job(int from, int to, int id) {
			this.from = from;
			this.to = to;
			this.id = id;
		}
	}
	
	/*
	 * The tick-method for the Elevator class.
	 * Does what work that has to be done for each elevator.
	 * @param: the current time
	 * @returns: void
	 */
	public void tick(int time) {
		// Lets do this.
		
	}
	
	/*
	 * Adds the new value to the change field.
	 * Does this via bit-wise or not to
	 * delete older ones.
	 * @param: the new value to add
	 * @return: void
	 */
	public void addChange(int change) {
		this.change = this.change | change;
	}
	
	/*
	 * Returns the next Node for this cabin.
	 * A positive integer indicates that it is moving towards a new node,
	 * -1 is when it is standing still.
	 * @param: none
	 * @return: int, the next node.
	 */
	public int getNextNode() {
		return nextNode;
	}
	
	/*
	 * Returns the previous node for this cabin.
	 * A positive integer indicates that it is moving away from this node,
	 * -1 is when it is standing still.
	 * @param: none
	 * @return: int, the previous node.
	 */
	public int getPrevNode() {
		return prevNode;
	}
	
	/*
	 * Returns the position of the elevator.
	 * The position is a double that indicates
	 * the progress between two nodes, the previous node and the next node.
	 * The position will take values between zero and whatever the weight 
	 * for the edge is between the nodes.
	 * @param: none
	 * @returns: double, the position.
	 */
	public double getPosition() {
		return position;
	}
	
	/*
	 * Returns the goal node that the elevator is moving towards via other nodes.
	 * @param: none
	 * @returns: int, the goal node.
	 */
	public int getTarget() {
		return target;
	}
	
	/*
	 * Returns the moving field of the elevator.
	 * *description of how we want the moving field to work*
	 * @param: none
	 * @returns: the moving field.
	 */
	public boolean getMoving() {
		return moving;
	}
	
	
	/*
	 * Adds a job to this elevator.
	 * Since the elevator calls are made with full details
	 * about where the traveler wants to go, the targets for the
	 * elevator will always be coupled with a from and to in the building.
	 * This method adds a new job to the elevator.
	 * @param: int from and to
	 * @returns: true or false depending on if the add was successful.
	 */
	public boolean addJob(int from, int to, int id) {
		if (!(validTarget(from) && validTarget(to))) {
			return false;
		}
		if (from == to) {
			return true;	// Already there!
		}
		jobs.add(new Job(from, to, id));
		return true;
	}
	
	/*
	 * Use addJob(2) to add targets for the elevator.
	 */
	public boolean addTarget(int target) {
		/*if (validTarget(target)) {
			targets[target] = 1;
			return true;
		}*/
 		return false;
	}
	
	/*
	 * Checks if the target is valid within the graph.
	 */
	private boolean validTarget(int target) {
		if (target < numDestinations && target > 0) {
			return true;
		} else {
			return false;
		}
	}
	
}
