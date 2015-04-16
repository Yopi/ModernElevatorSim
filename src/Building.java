/*
 * The building in the elevator project.
 * Is updated with all the elevators and persons positions.
 * 
 * TODO: 
 * 1. check - Elevator picking up person
 * 2. check - Person read how far they have traveled
 * 3. check - Tell elevator to MOVE
 * 4. check - Fix the adding of elevators, what information is needed.
 * 
 * 
 * @author Viktor Björkholm & Jesper Bränn
 * @version 2015-03-26
 */
import java.util.ArrayList;
import java.util.HashMap;


public class Building {
	
	Graph graph;
	Elevator[] elevators;
	ArrayList<Person> persons;
	int[] nodes;
	
	public Building(Graph graph, int numPersons, int numElevators) {
		this.graph = graph;
		elevators = new Elevator[numElevators];
		persons = new ArrayList<Person>(numPersons);
		nodes = new int[this.graph.getNumNodes()];
	}
	
	/*
	 * Add an elevator to the building to keep track of.
	 * @param: Elevator
	 * @returns: void
	 */
	public void addElevator(int eid, double position) {
		elevators[eid] = new Elevator(position, eid);
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
	 * Unlocks the node that the elevator is leaving.
	 * @param: Elevator, position, nextNode and previous Node.
	 * @returns: true if it was successful, false if the elevator does not exist.
	 */
	public void updateElevatorPosition(int eid, double position, int nextNode, int prevNode) {
		elevators[eid].position = position;
		elevators[eid].nextNode = nextNode;
		elevators[eid].prevNode = prevNode;
		if (position > (0d + 0.01)) { // 0.01 because doubles
			// The elevator is leaving a node, so unlock it.
			unlockNode(prevNode, eid);
		}
	}
	
	public double readElevatorPosition(int eid) {
		return elevators[eid].position;
	}
	
	public ArrayList<Integer> getNodeNeighbours(int node) {
		return graph.getNodeNeighbours(node);
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
			return true;
		}
		return false;
	}
	
	
	/*
	 * Method to get the traveled distance for a person
	 * when they are finished elevatoring.
	 * @param: id for the person
	 * @return: the distance traveled as a double.
	 */
	public double getTraveledDistance(int pid) {
		return persons.get(pid).distance;
	}
	
	/*
	 * Called by elevator to signal that the person
	 * is dropped of.
	 * @param: id for the person
	 * @return: void
	 */
	public void dropOfPerson(int pid, double distance) {
		persons.get(pid).elevatoring = -1;
		persons.get(pid).distance = distance;
		// Hur tänker vi att distance ska fungera? Ska den kontinuerligt växa eller nolställas emellanåt?
		// DIrrrekt efter statistics så kan vi nollställa den. Det blir bäst då.
	}
	
	
	/*
	 * Returns the distance between two floors in the graph.
	 * Returns -1 if there is no edge between these nodes.
	 */
	public int getDistance(int from, int to) {
		int dist = graph.getEdgeWeight(from, to);
		if (dist < 1) {
			return -1; // No edge.
		}
		return dist;
	}
	
	/*
	 * Called to by elevator to signal that the person
	 * is now busy elevatoring.
	 * @param: id for the person
	 * @returns: void
	 */
	public void pickUpPerson(int pid) {
		persons.get(pid).elevatoring = 1;
	}
	
	public int getPersonDistance(int pid) {
		return (int)Math.ceil(persons.get(pid).distance);
	}
	
	/*
	 * checks ahead towards the next node to see if the elevator
	 * has to slow down not to crash with another one.
	 * Also tells the next elevator to move if it is idle.
	 * @param: nodes from and to, and position between them.
	 * @returns: true if the elevator can continue, false if it should slow down.
	 */
	public boolean checkEmptyAhead(int from, int to, double position, int eid) {
		if (graph.getEdgeWeight(from, to) - position <= 1.0) {
			int owner = lockNode(to, eid);
			if (owner == eid) {
				return true;
			} else {
				move(owner);
				return false;
			}
		}
		for (int i = 0; i < elevators.length; i++) {
			if (elevators[i].nextNode == to && elevators[i].prevNode == from) {
				// This elevator is on the same edge
				if (elevators[i].position < position + 2.1) {
					// This elevator is is the way of the checking elevator.
					move(i);
					return false;
				}
			}
		}
		return true;
	}
	
	/*
	 * Resets the moving field in the elevator
	 * class by setting it to false.
	 * Appropriate to do when the elevator
	 * has responded to a move request.
	 * @param: id for elevator
	 * @returns: void
	 */
	public void ResetMove(int eid) {
		elevators[eid].move = false;
	}
	
	/*
	 * Checks whether or not an elevator has been
	 * asked to move.
	 * @param: elevator id
	 * @returns: whether or not it is true that it should.
	 */
	public boolean shouldIMove(int eid) {
		return elevators[eid].move;
	}
	
	/*
	 * Returns a list with information on what elevators that should move.
	 * @param: none
	 * @returns: ArrayList of the integers of elevator-ids
	 */
	public ArrayList<Integer> getShouldMoves() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < elevators.length; i++) {
			if (elevators[i].move) {
				list.add(i);
				ResetMove(elevators[i].id);
			}
		}
		return list;
	}
	
	/*
	 * Returns the next node for an elevator
	 * given its target and current node.
	 * @param: Current node and target node
	 * @returns: The next node in the path.
	 */
	public int getNextNodeInPath(int currentNode, int targetNode) {
		return graph.shortestPath[currentNode][targetNode];
	}
	
	/*
	 * Tells the elevator with the specific id to move.
	 * @þaram: id
	 * @returns: void
	 */
	private void move(int eid) {
		elevators[eid].move = true;
	}
	
	/*
	 * Public method to see if a parameter is within
	 * bounds for the nodes in the graph.
	 * @param: int as node
	 * @return: true if node exists, false if not.
	 */
	public boolean legalNode(int node) {
		if (node >= 0 && node < graph.getNumNodes()) {
			return true;
		}
		return false;
	}
	
	/*
	 * locks a node in the building.
	 * An elevator has to lock a node that it
	 * is traveling to, when it is close enough,
	 * to avoid mutual exclusion from them, in other
	 * words to avoid crashes.
	 * @param: the node and the elevators id
	 * @return: id of elevator that locked it, own id.
	 */
	private int lockNode(int node, int eid) {
		if (nodes[node] < 0) {
			nodes[node] = eid;
			return eid;
		}
		return nodes[node];
	}
	
	/*
	 * Unlocks the specified node.
	 * The node has to be own by the calling elevator
	 * to be allowed to unlock.
	 * @param: node
	 * @return: void
	 */
	private void unlockNode(int node, int eid) {
		if (nodes[node] == eid) {
			nodes[node] = -1;
		} else {
			// No unlocks for you.
		}
	}
	
	private class Elevator {
		
		public double position;
		public int id;
		public int nextNode;
		public int prevNode;
		public boolean move;
		
		public Elevator(double position, int id) {
			move = false;
			nextNode = -1;
			prevNode = -1;
			this.position = position;
			this.id = id;
		}
	}
	
	private class Person {
		int floor;
		int id;
		public int elevatoring;
		public double distance;
		public Person(int id) {
			this.id = id;
			distance = 0d;
			elevatoring = -1;
			floor = 0;
		}
	}
}
