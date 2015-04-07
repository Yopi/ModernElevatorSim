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
		
	}
	
	/*
	 * Takes care of the pending actions of this person.
	 * @param, time of the day in ticks.
	 * @returns: void.
	 */
	public void tick(int time) {
		if (time == beginWork) {
			// Request elevator from entrance floor to work floor.
		} else if (time == endWork) {
			// Request elevator from current position to entrance floor.
		} else if (doneMeetings < meetings.length && time == meetings[doneMeetings].getTime()) {
			doneMeetings++;
			// request elevator from current position to meeting floor.
		} else if (time == lunchTime) {
			// request elevator from current position to work floor.
		} else if (time == backFromLunch) {
			// request elevator from (hopefully) ground floor to work floor.
			// Case might be though, that the person was called to a meeting before
			// backFromLunch. 
		}
	}
	
}
