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
	 * Tick method for the controller.
	 * This is needed because the controller needs to run some
	 * code independently, in order to take care of
	 * cabins that have been asked to move by another cabin.
	 * @param: time variable, not sure if it is needed though.
	 * @returns: void
	 */
	public void tick(int time) {
		int moving;
		ArrayList<Integer> neighbors;
		int moveTo = -1;
		for (int i = 0; i < elevators.length; i++) {
			if (!elevators[i].idle) {
				// The elevator is working on stuff. Let it do so.
				continue;
			}
			moving = building.elevatorMovedBy(elevators[i].id);
			if (moving >= 0) {
				neighbors = building.getNodeNeighbours(elevators[i].getNextNode());
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
			}
			// Move the elevator to moveTo
			ArrayList<Job> jobs = elevators[i].getJobs();
			jobs.add(new Job(-1, moveTo, -1));
		}
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
		ArrayList<Job> jobs = elevators[mindex].getJobs();
	}
	
	/*
	 * Minimizes the travel distance by rearanging the jobs for the elevator.
	 * @param: ArrayList with the jobs.
	 * @returns: The rearanged arraylist.
	 */
	public ArrayList<Job> minimizeTravel(ArrayList<Job> jobs, int eid) {
		/*
		 * Det som gäller är inte att testa alla ordningar av element,
		 * utan det kritiska är egentligen att testa låta alla vara först.
		 * Det första elementet är det som hissen kommer ta fart mot och bestämma
		 * sin rutt, men på vägen så kommer den vid varje besökt nod att ta reda
		 * på om det finns jobb där att göra, vilket gör att om den första
		 * är den mest omfattande arbetet så kommer de andra att lösa sig på vägen.
		 * Så totalsökningen kan se ut som följer:
		 * Testa med alla först ->
		 * 		de som är kvar, låt dem turas om att vara först
		 * 
		 * blir typ rekursivt. Koolt!
		 * 
		 * Så.. rekursera?
		 * 
		 * Kan börja skriva grundkoden. Hur avgör jag distance med en given lista.
		 * 
		 * Just nu går tanken såhär: Skicka iväg listan från en for med element i som först,
		 * och på stället den skickar det till går den igenom hela färden för element i och
		 * kollar vad som försvinnerpå vägen, när den är klar kallar den på den här med det
		 * som är kvar av listan för att få det minimerat. Eller.. kanske om det fanns
		 * en metod som bara ändrade ordningen i listan. Denna metod tänker jag returnerar sträckan
		 * den färdas innan den är klar.
		 * 
		 * 
		 * Jaaaa, det är för att bajset värmer benen.
		 * Kopiera listan -> editera skiten ur den, spara
		 * en referens om den var najs med minimering av distance
		 */
		if (jobs.size() == 1) {
			return jobs;
		}
		ArrayList<Job> copyJobs = new ArrayList<Job>(jobs.size());
		Job job;
		ArrayList<Job> minJobs;
		int dist;
		int min = Integer.MAX_VALUE;
		ArrayList<Job> minList;
		for (int i = 0; i < jobs.size(); i++) {
			for (int j = 0; j < copyJobs.size(); j++) {
				job = new Job(jobs.get(j).from, jobs.get(j).to, jobs.get(j).id);
				copyJobs.add(i, job);
			}
			// copyJobs now have a copy of jobs.
			Job tmpJob = (Job)copyJobs.remove(i);
			copyJobs.add(0, tmpJob);
			dist = distanceJobs(copyJobs, eid);
			if (min > dist) {
				min = dist;
				minJobs = copyJobs;
			}
		} 
		
		return null;
	}
	
	private int distanceJobs(ArrayList<Job> jobs, int eid) {
		int distance = 0;
		int position = elevators[eid].nextNode;
		int target;
		if (jobs.get(0).from > 0) {
			target = jobs.get(0).from;
		} else if (jobs.get(0).to > 0){
			target = jobs.get(0).to;
		} else {
			// Illegal job, should be deleted
			// neeee händer aldrig.
			target = 0;
			System.out.println("Woow, slow down there satan.");
		}
		
		int next = building.getNextNodeInPath(position, jobs.get(0).from);
		distance += building.getDistance(position, next);
		while (next != target) {
			/*
			 * Kolla vad som ska göras vid next-noden,
			 * modifiera ev. jobb i listan (Juste, därför jag kopierade den. Kan jag lösa detta på annat vis?)
			 * kör vidare, vad är nästa nod på vägen.
			 */
			for (int i = 0; i < jobs.size(); i++) {
				if (next == jobs.get(i).from) {
					jobs.get(i).from = -1;
				} else if (next == jobs.get(i).to && jobs.get(i).from < 0) {
					jobs.get(i).to = -1;
				}
			}
			int tmp = next;
			next = building.getNextNodeInPath(next, target);
			distance += building.getDistance(tmp, next);
		}
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		ArrayList<Job> remainingJobs = new ArrayList<Job>();
		for (int i = 0; i < jobs.size(); i++) {
			if (jobs.get(i).from < 0 && jobs.get(i).to < 0) {
				continue;
			}
			indexes.add(i);
			remainingJobs.add(new Job(jobs.get(i).from, jobs.get(i).to, jobs.get(i).id));
		}
		if (remainingJobs.size() > 0) {
			remainingJobs = minimizeTravel(remainingJobs, eid);
			// Copy in the remaining to the old list. so that it is good.. i guess.
			// Delete the objects in the old list that the remaining jobs contain,
			// and insert the remaining jobs objects in that order.
			for (int i = 0; i < indexes.size(); i++) {
				jobs.remove(indexes.get(i));
				jobs.add(remainingJobs.get(i));
			}
		}
		distance = distance + distanceJobs(remainingJobs, eid);
		return distance;
	}
	
}
