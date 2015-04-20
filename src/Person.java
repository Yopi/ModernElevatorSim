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
	static final boolean DEBUG = false;
	
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
	Random rand;
	
	public Person(int id, Building building, Controller controller, Statistics stats, Random rand, double second) {
		double hour = 3600 * second;
		this.id = id;
		this.building = building;
		this.controller = controller;
		this.stats = stats;
		this.second = second;
		this.rand = rand;
		
		beginWork = (int)((hour * 8) + (gaussian() * (900 * second)));	// Random time for arrival at work, +- 15 minutes, 900 seconds.
		endWork = (int)((hour * 17) + gaussian() * (900 * second));	// Random time for leaving work, +- 15 minutes.
		lunchTime = (int)((hour * 12) + (gaussian() * hour));			// Random time for lunch, +- 1 hour.
		backFromLunch = (int)(lunchTime + 2700 * second); // 45 minutes lunch
		workFloor = rand.nextInt(building.graph.getNumNodes() - 1) + 1;
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
	
	private double gaussian() {
		double g = rand.nextGaussian();
		if(g > 2) g = 2 + rand.nextDouble()*0.5;
		if(g < -2) g = -(2 + rand.nextDouble()*0.5);
		
		return g;
	}
	
	/*
	 * Takes care of the pending actions of this person.
	 * @param, time of the day in ticks.
	 * @returns: void.
	 */
	public void tick(int time) {
		if (status == STATUS_WAITING) {
			//try { Thread.sleep(5000); } catch (Exception e){}
			
			if(building.isInElevator(id)) {
				stats.addWaitingTime(id, (time - startTime));
				startTime = time;
				status = STATUS_ELEVATORING;
			} else {
				if(currentFloor == nextFloor) {
					status = STATUS_IDLE;

					System.err.println("Trying to go to same floor");
				}
			}
		} else if (status == STATUS_ELEVATORING) {
			if(!building.isInElevator(id)) {
				//try { Thread.sleep(5000); } catch (Exception e) { System.err.println(e); System.exit(-1); }
				stats.addTravelTime(id, (time - startTime), building.getPersonDistance(id), currentFloor, nextFloor);
				if(DEBUG) System.out.println("Person " + id + " has gone from floor " + currentFloor + " -> " + nextFloor);

				currentFloor = nextFloor;
				status = STATUS_IDLE;
			}
		}
		
		if (status == STATUS_IDLE) {
			if(DEBUG)
				System.out.println("time: " + time + " == " + beginWork);
			
			if (time == beginWork) {
				if(DEBUG) System.out.println("PERSON ("+ id +") ARRIVES AT WORK");
				startElevator(time);
				controller.requestElevator(currentFloor, workFloor, id);
				nextFloor = workFloor;
			} else if(time == endWork) {
				if(DEBUG) System.out.println("PERSON ("+ id +") GOES HOME");
				startElevator(time);
				controller.requestElevator(currentFloor, 0, id);
				nextFloor = 0;
			} else if(time == lunchTime) {
				if(DEBUG) System.out.println("PERSON ("+ id +") GOES TO LUNCH");
				startElevator(time);
				controller.requestElevator(currentFloor, 0, id);
				nextFloor = 0;
			} else if(time == backFromLunch) {
				if(DEBUG) System.out.println("PERSON ("+ id +") IS BACK FROM LUNCH");
				startElevator(time);
				controller.requestElevator(currentFloor, workFloor, id);
				nextFloor = workFloor;
			}
		}
		
		if(DEBUG) {
			System.out.print("PERSON ("+id+") [s|cf|wf] : [" + status + "|" + currentFloor + "|" + workFloor + "]    ");
		}
	}

	private void startElevator(int time) {
		status = STATUS_WAITING;
		startTime = time;
	}
}
