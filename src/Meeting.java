/*
 * Class for a gathered meeting for persons in the building.
 * This is used to simulate a specific kind of movement within the building
 * when many people from random floors gather on one floor.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-30
 */

public class Meeting {
	
	int time;
	int location;
	
	public Meeting(int floor, int location) {
		this.time = time;
		this.location = location;
	}
	
	/*
	 * Method to get the time.
	 * @param: none
	 * @return: time
	 */
	public int getTime() {
		return time;
	}
	
	/*
	 * Method to get the location of the meeting.
	 * @param: none
	 * @returns: location, a node in the graph. 
	 */
	public int getLocation() {
		return location;
	}
}

