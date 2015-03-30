/*
 * Person class.
 * All you need to represent a person.
 * Truly.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */


public class Person {
	
	int beginWork;	// The time that the person begins work.
	int endWork;	// The time when the person ends work. (leaves building)
	int lunchTime;	// Time for the person to eat lunch (movement in building)
	Meeting[] meetings;	// Meetings (movement in building)
	int currentFloor;	// The position of the person.
	int workFloor; // The floor that the person works on.
	int idleWait;
	int travelWait;
	int travelDist;
	int currentTarget;
	
	
	
	public Person(int beginWork, int endWork, int lunchTime, Meeting[] meetings, int workFloor) {
		this.beginWork = beginWork;
		this.endWork = endWork;
		this.lunchTime = lunchTime;
		this.meetings = meetings;
		this.workFloor = workFloor;
		int idleWait = 0;
		int travelWait = 0;
		int travelDist = 0;
		int currentTarget = -1;
	}
	
	/*
	 * Takes care of the pending actions of this person.
	 */
	public void tick(int time) {
		
	}
	
}
