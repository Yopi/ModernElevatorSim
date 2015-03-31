/*
 * Class to represent the elevator cabins in the simulation.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-24
 */
import java.util.ArrayList;

public class Elevator {
	
	// Private fields
	int nextNode;	// The next node that the elevator will reach.
	int prevNode;	// The previous node that the elevator visited.
	int target;		// The goal of the elevator, where it is travelling to.
	
	double position;	// The progress of the elevator between two nodes.
	
	boolean moving;		// If the elevator is moving or not. Where it is moving can be found by nextNode and prevNode.
	
	ArrayList<Job> jobs;	// The active jobs for the elevator
	
	Building building;	// The building that the elevator is in.
	
	/*
	 * Constructor for the elevator class.
	 * @param: The number of nodes within the system
	 */
	public Elevator(int numDestinations, Building building) {
		nextNode = -1;
		prevNode = -1;
		position = 0;
		target = -1;
		moving = false;
		jobs = new ArrayList<Job>();
		this.building = building;
	}
	
	private class Job {
		
		int from, to;
		int[] path;	// An idea i have about letting each path in the graph 
					// be calculated upon startup instead of doing it over 
					// and over and over. Or perhaps just store it and 
					// calculate each one when needed.
					/*
					 * On second thought, the path might change if
					 * the elevator gets a new job in the middle of it,
					 * so it will probably be useless.
					 */
		
		
		public Job(int from, int to) {
			this.from = from;
			this.to = to;
		}
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
	 * Since the elevator calls are made with full details
	 * about where the traveller wants to go, the targets for the
	 * elevator will always be coupled with a from and to in the building.
	 * This method adds a new job to the elevator.
	 * @param: int from and to
	 * @returns: true or false depending on if the add was successful.
	 */
	public boolean addJob(int from, int to) {
		if (!(validTarget(from) && validTarget(to))) {
			return false;
		}
		jobs.add(new Job(from, to));
		return true;
	}
	
	/*
	 * Use addJob(2) to add targets for the elevator.
	 */
	public boolean addTarget(int target) {
		if (validTarget(target)) {
			targets[target] = 1;
			return true;
		}
 		return false;
	}
	
	private boolean validTarget(int target) {
		if (target < targets.length && target > 0) {
			return true;
		} else {
			return false;
		}
	}
	
}
