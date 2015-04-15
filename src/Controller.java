import java.util.ArrayList;
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
	
	Elevator[] elevators;
	Building building;
	
	public Controller(Elevator[] elevators, Building building) {
		this.elevators = elevators;
		this.building = building;
	}
	
	/*
	 * Method to call for an elevator.
	 * Might be the only public method of this class.
	 * @param: source and destination for the traveller
	 * @returns: true if the parameters are good and false if the parameters are out of bounds.
	 */
	public boolean requestElevator(int from, int to, int id) {
		if (!(building.legalNode(from) && building.legalNode(to))) {
			return false;
		}
		nearestCar(from, to, id);
		return true;
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
		ArrayList jobs = elevators[mindex].getJobs();
	}
	
}
