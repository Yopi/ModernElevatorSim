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
	
	/*
	 * Add an elevator to the building to keep track of.
	 * @param: Elevator
	 * @returns: void
	 */
	public void addElevator(Elevator e) {
		elevators.add(e);
	}
	
	/*
	 * Add a person to the building to keep track of.
	 * @param: Person
	 * @returns: void
	 */
	public void addPerson(Person p) {
		persons.add(p);
	}
	
	/*
	 * Update the position for an elevator.
	 * @param: Elevator, position, nextNode and previous Node.
	 * @returns: true if it was successful, false if the elevator does not exist.
	 */
	public void updateElevatorPosition(int elevatorId, double position, int nextNode, int prevNode) {
		//if (elevators.contains(o))
	}
	
	public double readElevatorPosition(int elevatorId) {
		return 0.0;
	}
	
	public void updatePersonPosition(int personId, int position) {
		
	}
	
	public boolean checkEmptyAhead(int from, int to, double position) {
		for (int i = 0; i < elevators.size(); i++) {
			if (elevators.get(i).nextNode == to && elevators.get(i).prevNode == from) {
				// This elevator is on the same edge
				if (elevators.get(i).position < position + 1.0) {
					// This elevator is is the way of the checking elevator.
					return false;
				}
			}
			
			/*
			 * Alla hissar får pusha vilken nod de är på väg mot
			 * för att det ska bli enkelt att kontrollera
			 * eftersom hissar kan vara påväg emot varandra
			 * om de rör sig mot samma nod.
			 * För att båda inte heller ska sakta in 
			 * så behöver en lämplig hiss låsa noden.
			 * 
			 * Den hiss som når noden med 1 i avstånd får låsa noden
			 * och sedan låsa upp den när den rör sig därifrån.
			 */
		}
		return true;
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
