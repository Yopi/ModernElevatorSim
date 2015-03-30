/*
 * The building in the elevator project.
 * Is updated with all the elevators and persons positions.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */
import java.util.ArrayList;


public class Building {
	
	Graph graph;
	ArrayList<Elevator> elevators;
	ArrayList<Person> persons;
	
	public Building(Graph graph) {
		this.graph = graph;
		elevators = new ArrayList<Elevator>();
		persons = new ArrayList<Person>();
	}
	
	public void addElevator(Elevator e) {
		elevators.add(e);
	}
	
	public void addPerson(Person p) {
		persons.add(p);
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
