/*
 * Class to represent the elevator cabins in the simulation.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-24
 */
public class Elevator {
	
	// Private fields
	int nextNode;	// The next node that the elevator will reach.
	int prevNode;	// The previous node that the elevator visited.
	double position;	// The progress of the elevator between two nodes.
	int goal;		// The goal of the elevator, where it is travelling to.
	int moving;		// In which direction the elevator is moving. We will have four possible directions.
	
	public Elevator() {
		
	}
	
	/*
	 * Returns the next Node for this cabin.
	 * A positive integer indicates that it is moving towards a new node,
	 * -1 is when it is standing still.
	 * @param: none
	 * @return: int, the next node.
	 */
	public int getNextNode() {
		return nextNode;
	}
	
	/*
	 * Returns the previous node for this cabin.
	 * A positive integer indicates that it is moving away from this node,
	 * -1 is when it is standing still.
	 * @param: none
	 * @return: int, the previous node.
	 */
	public int getPrevNode() {
		return prevNode;
	}
	
	/*
	 * Returns the position of the elevator.
	 * The position is a double that indicates
	 * the progress between two nodes, the previous node and the next node.
	 * The position will take values between zero and whatever the weight 
	 * for the edge is between the nodes.
	 * @param: none
	 * @returns: double, the position.
	 */
	public double getPosition() {
		return position;
	}
	
	/*
	 * Returns the goal node that the elevator is moving towards via other nodes.
	 * @param: none
	 * @returns: int, the goal node.
	 */
	public int getGoal() {
		return goal;
	}
	
	/*
	 * Returns the moving field of the elevator.
	 * *description of how we want the moving field to work*
	 * @param: none
	 * @returns: the moving field.
	 */
	public int getMoving() {
		return moving;
	}
}
