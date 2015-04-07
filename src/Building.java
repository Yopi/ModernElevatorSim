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
	boolean[] nodes;
	
	public Building(Graph graph, int numPersons) {
		this.graph = graph;
		elevators = new ArrayList<Elevator>();
		persons = new ArrayList<Person>(numPersons);
		nodes = new boolean[this.graph.getNumNodes()];
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
	public void addPerson(int id) {
		persons.add(id, new Person(id));
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
	
	/*
	 * Method to see if a person is in an elevator.
	 * @param: person id
	 * @return: true if the person is in an elevator, false otherwise.
	 */
	public boolean isInElevator(int pid) {
		if (persons.get(pid).elevatoring >= 0){
			return false;
		}
		return true;
	}
	
	public boolean checkEmptyAhead(int from, int to, double position) {
		if (graph.getEdgeWeight(from, to) - position <= 1.0) {
			if (lockNode(to)) {
				return true;
			} else {
				/*
				 * Hörnet var låst, här borde den säga
				 * till den framför to GET MOVIN
				 */
				return false;
			}
		}
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
	
	private boolean lockNode(int node) {
		if (nodes[node]) {
			nodes[node] = false;
			return true;
		}
		return false;
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
		public int elevatoring;
		public Person(int id) {
			this.id = id;
		}
	}
}
