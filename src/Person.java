/*
 * Person class.
 * All you need to represent a person.
 * Truly.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */


public class Person {
	int id;
	
	int beginWork;	// The time that the person begins work.
	int endWork;	// The time when the person ends work. (leaves building)
	int lunchTime;	// Time for the person to eat lunch (movement in building)
	int backFromLunch;
	Meeting[] meetings;	// Meetings (movement in building)
	int currentFloor;	// The position of the person.
	int workFloor; // The floor that the person works on.
	int idleWait;
	int travelWait;
	int travelDist;
	int currentTarget;
	int doneMeetings;
	
	
	
	public Person(int id, int beginWork, int endWork, int lunchTime, Meeting[] meetings, int workFloor, double second) {
		this.id = id;
		this.beginWork = beginWork;
		this.endWork = endWork;
		this.lunchTime = lunchTime;
		backFromLunch = lunchTime + (int)(2700 * second); // 45 minutes lunch
		this.meetings = meetings;
		this.workFloor = workFloor;
		idleWait = 0;
		travelWait = 0;
		travelDist = 0;
		currentTarget = -1;
		doneMeetings = 0;
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
