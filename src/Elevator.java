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
 * 1.1 Kolla om hissen har en position hägre än noll -> mellan två våningar
 * 1.1.1 Kolla om den är framme vid sin target
 * 1.1.1.1 Simulera dörröppning
 * 1.1.1.2 Eller ta reda på nästa nod. Du kodade något sådant va jesper?
 * 1.1.2 Fortsätt åka, kolla framåt och anpassa hastigheten
 * 1.2 Om idle - vad ska den göra? Chilla? Kontrollera om den 
 * 		fått en move-order från controller för att den står stilla. Tänker mig att då får den bara ett target.
 * 		Alternativt ett jobb som är där den är nu till vilken nod controllern nu tycker är lämplig.
 * 2. Vad som nu dyker upp när tick-metoden skrivits.
 * 3. Kontrollera hur vi ska sätta intervallet som avgör att en hiss är vid target.
 * 		Det måste vara mindre än vad building använder för att låsa en nod.
 */

public class Elevator {
	
	final int maxPassengers = 7;	// Maximum number of passengers
	
	// Private fields
	int id;		// Id for the elevator
	int nextNode;	// The next node that the elevator will reach.
	int prevNode;	// The previous node that the elevator visited.
	int target;		// The goal of the elevator, where it is traveling to.
	int numDestinations; // The number of nodes. One node = 1 destination.
	int passengers;	// Number of passengers in the elevator.
	int slowDown;	// Used to slowly slow down, and not just stop on the 5-öring -> ded.
	
	int doorOpening;
	
	double step;
	double position;	// The progress of the elevator between two nodes.
	double second;
	
	boolean idle;	// True if the elevator has no active jobs and is standing still.
	boolean moving;		// If the elevator is moving or not. Where it is moving can be found by nextNode and prevNode.
	
	ArrayList<Job> jobs;	// The active jobs for the elevator
	
	Building building;	// The building that the elevator is in.
	
	/*
	 * Constructor for the elevator class.
	 * @param: The number of nodes within the system
	 */
	public Elevator(int numDestinations, Building building, int id, double second) {
		this.id = id;
		nextNode = -1;
		prevNode = -1;
		this.second = second;
		step = setStep(this.second);
		slowDown = 2;
		position = 0;
		target = -1;
		passengers = 0;
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
		// Since the MES is single-directed, the elevator always has to travel to the next node.
		// So if it is in between nodes, travel!
		if (position > (step/8)) {
			// The elevator is moving, check if at target or keep moving.
			/*if ((building.getDistance(prevNode, nextNode) - position) < step/8 ) {	
				// The elevator is at the next node.
				// Check the current job, and see if this is the target
				// or if the next node should be aquired.
				if (jobs.get(0).to == nextNode) {
					// TODO: At the target. Time to simulate drop-of
				} else {
					// Get the next node from the building class.
				}
			}*/
			if (building.checkEmptyAhead(prevNode, nextNode, position, id)) {
				// The elevator is not at the target, keep moving
				if ((building.getDistance(prevNode, nextNode) - (position + step)) < 0 ) {
					// The elevator is less than a step away from the next node.
					/*
					 * This could become a little weird. If it takes a step
					 * small enough to reach the next node only, it could result
					 * in a really tiny step while the chaft might be completely
					 * straight here, in a curve it would be logical but not here.
					 * 
					 * Solution: feck et, take small step. Not important for our data.
					 */
					position = 0d;
					// TODO: Aquire next target.
				}
				position = position + step;
				resetSlowDown();	// In case there was a slowdown before.
				building.updateElevatorPosition(id, position, nextNode, prevNode);
			} else {
				increaseSlowDown();
				if (slowDown < 8) {
					position = position + (step / slowDown);
				} else {
					// The elevator stops.
				}
			}
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
	 * Returns true if the doors are open and
	 * then decreases the counter. Returns false
	 * when the counter has reached zero.
	 * @param: none
	 * @returns: true if the doors are open, false if they are closed
	 */
	private boolean doorsOpen() {
		if (doorOpening > 0) {
			doorOpening--;
			return true;
		}
		return false;
	}
	
	/*
	 * Sets the door opening variable
	 * to 5 seconds in relation to ticks.
	 * @param: none
	 * @returns: void
	 */
	private void openDoors() {
		doorOpening = (int)(second * 5);
	}
	
	/*
	 * Increases the slowDown variable.
	 * When the path is not clear for a few ticks in a row,
	 * the slow down variable will ensure that the
	 * elevator stops.
	 * @param: none
	 * @returns: none
	 */
	private void increaseSlowDown() {
		slowDown = slowDown * 2;
	}
	
	/*
	 * Resets the slowdown-variable.
	 * The slowdown variable is used to decrease the
	 * speed of the elevator when there is another elevator ahead.
	 * Resetting it means that the shaft ahead is clear.
	 * @param: none
	 * @returns: void
	 */
	private void resetSlowDown() {
		slowDown = 2;
	}
	
	/*
	 * Sets the step speed of the elevator, 1 distance is set to one meter,
	 * and here it is set how many meters per second the elevator will
	 * be traveling with use of the 'second' variable.
	 * Returns a step lower than 1.0.
	 * @param: how many ticks per second
	 * @returns: The length of a step.
	 */
	private double setStep(double second) {
		// The elevator will travel at 0.5 m/s
		double step = 0.5 / second;
		while (step > 1.0) {
			step = step - 0.2;
		}
		return step;
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
