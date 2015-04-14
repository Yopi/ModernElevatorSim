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
import java.util.HashMap;


/*
 * TODO:
 * 1. Fortsätt på tick-metoden
 * check - 1.1 Kolla moving-fältet för att se om den rör på sig.
 * check - 1.1.1 Kolla om den är framme vid sin target
 * check - 1.1.1.1 Simulera dörröppning
 * check - 1.1.1.2 Eller ta reda på nästa nod. Du kodade något sådant va jesper?
 * check, ish - 1.1.2 Fortsätt åka, kolla framåt och anpassa hastigheten
 * check, ish - 1.2 Om idle - vad ska den göra? Chilla? Kontrollera om den 
 * 		fått en move-order från controller för att den står stilla. Tänker mig att då får den bara ett target.
 * 		Alternativt ett jobb som är där den är nu till vilken nod controllern nu tycker är lämplig.
 * que - 2. Vad som nu dyker upp när tick-metoden skrivits.
 * check - 3. Kontrollera hur vi ska sätta intervallet som avgör att en hiss är vid target.
 * 		Det måste vara mindre än vad building använder för att låsa en nod.
 * 4. Just nu saktar hissen in när den stannar, men tar fart på noll tid -> errybody ded.
 * 		Finns det något värde för oss att simulera inbromsning och accelerering?
 * 		Vi kan ju resonera att detta sker mellan ticksen, ish. Annars kanske det
 * 		bara inte har ett värde, vi kan ju påstå oss simulera medelhastigheten! Såklart.
 * 5. Ta bort den simulerade start- och stopp-sträckan.
 * check - 6. Lägg till stödet för personer.
 * check - 7. Lagra distance för personerna som åker.
 * 		Detta kan kanske göras enkelt genom att hissen själv
 * 		håller reda på sin totala resväg och lagrar två värden
 * 		för varje person, en när den klev på och sedan
 * 		nästa när den kliver av.
 * 8. add way for controller to be able to modify jobs list.
 * check - 9. Implement different behaviour when id in job is -1, noone to drop of. That or check
 * length of passenger list, both give same result i guess.
 */

public class Elevator {
	
	final int MAX_PASSENGERS = 7;	// Maximum number of passengers
	
	// Private fields
	int id;		// Id for the elevator
	int nextNode;	// The next node that the elevator will reach.
	int prevNode;	// The previous node that the elevator visited.
	int target;		// The goal of the elevator, where it is traveling to.
	int numDestinations; // The number of nodes. One node = 1 destination.
	int passengers;	// Number of passengers in the elevator.
	int slowDown;	// Used to slowly slow down, and not just stop on the 5-öring -> ded.
	
	int doorOpening;
	
	double step;		// The length of a step
	double position;	// The progress of the elevator between two nodes.
	double second;		// How many ticks per second, from the simulator class
	double distance;	// How far the elevator has traveled.
	
	boolean idle;	// True if the elevator has no active jobs and is standing still.
	boolean moving;		// If the elevator is moving or not. Where it is moving can be found by nextNode and prevNode.
	
	Job currentJob;	// The current job. The elevator won't know if the controller changes the queue, so in
					// order not do delete the wrong job it can reference to it via this field.
	
	ArrayList<Job> jobs;	// The active jobs for the elevator
	HashMap<Integer, Double> persons;	// The persons aboard the elevator.
	
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
		persons = new HashMap<Integer, Double>();
		distance = 0d;
		this.building = building;
	}
	
	private class Job {
		
		public int from;	// From what node the job origins
		public int to;		// To what node the job is finished.
		public int id;		// The person that called for the job.
		
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
		if (doorsOpen()) {
			// The doors are open, the elevator shouldn't do anything.
			return;
		}
		// TODO: Överväg att börja använda moving-fältet för att avgöra rörelse.
		// Kan göra att jag slipper dubbel kod för att röra skiten framåt.
		if (moving) {
			// The elevator is moving, keep moving.
			idle = false;
			if (building.checkEmptyAhead(prevNode, nextNode, position, id)) {
				// Clear ahead, proceed with movings.
				resetSlowDown();	// In case there was a slowdown before.
				if (((double)building.getDistance(prevNode, nextNode) - (position + step)) <= 0 ) {
					// The elevator is less than a step away from the next node.
					// This step will take it to the target.
					distance = distance + ((double)building.getDistance(prevNode, nextNode) - position);
					position = 0d; // reset position, it is now relatively 0 to the nextNode.
					prevNode = nextNode;	// The previous node is now the next node.
					
					// Is there any job here for the elevator?
					// Loop through the jobs.
					for (int i = 0; i < jobs.size(); i++) {
						if (jobs.get(i).from == nextNode) {
							// Pick up a person!
							// TODO: increment number of persons in elevator.
							if (persons.size() < MAX_PASSENGERS) {
								jobs.get(i).from = -1;
								building.pickUpPerson(jobs.get(i).id);
								persons.put(jobs.get(i).id, distance);
								openDoors();
								moving = false;
							}
						} else if (jobs.get(i).to == nextNode && jobs.get(i).from < 0) {
							// Drop of a person!
							// TODO: Decrease the number of persons in the elevator.
							if (jobs.get(i).id < 0) {
								// This was a job added by controller to get the elevator to move.
								// The elevator is empty.
								jobs.remove(i);
								moving = false;
								continue;
							}
							building.dropOfPerson(jobs.get(i).id, (distance - persons.get(jobs.get(i).id)));
							persons.remove((Integer)jobs.get(i).id);	// Removes the person as an object.
							openDoors();
							moving = false;
							jobs.remove(i);
						}
					}
					
					// Set the next target.
					if (jobs.size() > 0) {
						// More jobs available!
						if (jobs.get(0).from >= 0) {
							nextNode = building.getNextNodeInPath(nextNode, jobs.get(0).from);
						} else {
							nextNode = building.getNextNodeInPath(nextNode, jobs.get(0).to);
						}
					} else {
						// The elevator is now idle.
						idle = true;
					}
					
				} else {
					// Next node is more than a step away from the elevator.
					// Just keep moving.
					distance = distance + step;
					position = position + step;
				}
			} else {
				// The path ahead was not clear.
				increaseSlowDown();
				if (slowDown < 8) {
					distance = distance + (step / slowDown);
					position = position + (step / slowDown);
				} else {
					// The elevator stops.
				}
			}
			building.updateElevatorPosition(id, position, nextNode, prevNode);
		} else {
			// The elevator is idle or just closed doors.
			// Same procedure either way, check for most urgen job and continue.
			if (jobs.size() > 0) {
				// There are available jobs for the elevator.
				if (jobs.get(0).from < 0) {
					nextNode = building.getNextNodeInPath(prevNode, jobs.get(0).to);
				} else {
					nextNode = building.getNextNodeInPath(prevNode, jobs.get(0).from);
				}
				idle = false;
				moving = true;
			} else {
				// No available jobs, just sitting here.
				// I think that the checking wether to move or not should be handled
				// By the controller in the largest extent. This is otherwise where
				// that would have been implemented
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
		if (!(validTarget(to))) {
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
