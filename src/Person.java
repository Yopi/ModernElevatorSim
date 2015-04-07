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
	
	Building building;
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
	
	int maxMeetings = 5;
	int status;
	int startTime;
	
	public Person(int id, Building building, Statistics stats, Random rand, double second) {
		double hour = 3600 * second;
		this.id = id;
		this.building = building;
		this.stats = stats;
		this.second = second;
		
		beginWork = (int)((hour * 8) + (rand.nextGaussian() * (900 * second)));	// Random time for arrival at work, +- 15 minutes, 900 seconds.
		endWork = (int)((hour * 17) + rand.nextGaussian() * (900 * second));	// Random time for leaving work, +- 15 minutes.
		lunchTime = (int)((hour * 12) + (rand.nextGaussian() * hour));			// Random time for lunch, +- 1 hour.
		backFromLunch = lunchTime + (int)(2700 * second); // 45 minutes lunch
		workFloor = rand.nextInt(building.graph.getNumNodes());
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
				stats.addTravelTime(id, (time - startTime), 0);
				status = STATUS_IDLE;
			}
		}
		
		if (status == STATUS_IDLE) {
			if (time == beginWork) {
				startElevator(time);
				// Call for elevator
			} else if(time == endWork) {
				startElevator(time);
			} else if(time == lunchTime) {
				startElevator(time);
			} else if(time == backFromLunch) {
				startElevator(time);
			}
		}
	}

	private void startElevator(int time) {
		status = STATUS_WAITING;
		startTime = time;
	}
}
