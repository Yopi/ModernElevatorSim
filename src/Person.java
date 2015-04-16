import java.util.Random;

/*
 * Person class.
 * All you need to represent a person.
 * Truly.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */


public class Person {
	// Status fields
	static final int STATUS_IDLE = 0;
	static final int STATUS_WAITING = 1;
	static final int STATUS_ELEVATORING = 2;
	static final boolean DEBUG = true;
	
	Building building;
	Controller controller;
	Statistics stats;
	int id;
	double second;
	
	int beginWork;	// The time that the person begins work.
	int endWork;	// The time when the person ends work. (leaves building)
	int lunchTime;	// Time for the person to eat lunch (movement in building)
	int backFromLunch;
	
	Meeting[] meetings;	// Meetings (movement in building)
	
	int currentFloor;	// The position of the person.
	int workFloor; // The floor that the person works on.
	int nextFloor;
	
	int maxMeetings = 5;
	int status;
	int startTime;
	
	public Person(int id, Building building, Controller controller, Statistics stats, Random rand, double second) {
		double hour = 3600 * second;
		this.id = id;
		this.building = building;
		this.controller = controller;
		this.stats = stats;
		this.second = second;
		
		beginWork = (int)(hour * 8); //(int)((hour * 8) + (rand.nextGaussian() * (900 * second)));	// Random time for arrival at work, +- 15 minutes, 900 seconds.
		endWork = (int)(hour * 8) + 80; //(int)((hour * 17) + rand.nextGaussian() * (900 * second));	// Random time for leaving work, +- 15 minutes.
		lunchTime = (int)(hour * 8) + 30; //(int)((hour * 12) + (rand.nextGaussian() * hour));			// Random time for lunch, +- 1 hour.
		backFromLunch = lunchTime + 25; // (int)(2700 * second); // 45 minutes lunch
		workFloor = id+1; //rand.nextInt(building.graph.getNumNodes());
		int numMeetings = rand.nextInt(maxMeetings);	// Random number of meetings for a worker.
		
		/*Meeting[] meetings = new Meeting[maxMeetings];
		int interval = ((int)hour * (17-8)) / maxMeetings;
		int first = (int)(hour * 8) + interval / 2;
		for (int i = 0; i < meetings.length; i++) {
			meetings[i] = new Meeting((first + (interval * i)), rand.nextInt(graph.getNumNodes()));
		}
		Meeting[] myMeetings = new Meeting[numMeetings];
		for (int j = 0; j < meetings.length; j++) {
			myMeetings[j] = meetings[j];
		}*/
		
		status = STATUS_IDLE;
	}
	
	/*
	 * Takes care of the pending actions of this person.
	 * @param, time of the day in ticks.
	 * @returns: void.
	 */
	public void tick(int time) {
		if (status == STATUS_WAITING) {
			if(building.isInElevator(id)) {
				stats.addWaitingTime(id, (time - startTime));
				startTime = time;
				status = STATUS_ELEVATORING;
			}
		} else if (status == STATUS_ELEVATORING) {
			if(!building.isInElevator(id)) {
				stats.addTravelTime(id, (time - startTime), building.getPersonDistance(id));
				currentFloor = nextFloor;
				status = STATUS_IDLE;
			}
		}
		
		if (status == STATUS_IDLE) {
			if(DEBUG)
				System.out.println("time: " + time + " == " + beginWork);
			
			if (time == beginWork) {
				startElevator(time);
				controller.requestElevator(currentFloor, workFloor, id);
				nextFloor = workFloor;
			} else if(time == endWork) {
				startElevator(time);
				controller.requestElevator(currentFloor, 0, id);
				nextFloor = 0;
			} else if(time == lunchTime) {
				startElevator(time);
				controller.requestElevator(currentFloor, 0, id);
				nextFloor = 0;
			} else if(time == backFromLunch) {
				startElevator(time);
				controller.requestElevator(currentFloor, workFloor, id);
				nextFloor = workFloor;
			}
		}
		
		if(DEBUG) {
			System.out.println("Person: status:" + status + ", current floor:" + currentFloor);
		}
	}

	private void startElevator(int time) {
		status = STATUS_WAITING;
		startTime = time;
	}
}
