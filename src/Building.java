/*
 * The building in the elevator project.
 * Is updated with all the elevators and persons positions.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */
public class Building {
	
	public Building() {
		
	}
	
	public void addElevator() {
		
	}
	
	public void addPerson() {
		
	}
	
	public double readPosition(int elevatorId) {
		return 0.0;
	}
	
	public void updateElevatorPosition(int elevatorId, double position, int nextNode, int prevNode) {
		
	}
	
	public double readElevatorPosition(int elevatorId) {
		return 0.0;
	}
	
	public void updatePersonPosition(int personId, int position) {
		
	}
	
	private class Elevator {
		
		double position;
		int nextNode;
		int prevNode;
		
		public Elevator() {
			
		}
	}
	
	private class Person {
		int floor;
		int id;
		public Person() {
			
		}
	}
}
