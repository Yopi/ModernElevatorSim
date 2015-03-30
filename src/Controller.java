/*
 * Class to control the elevators and give the person class
 * an API to call for an elevator.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-30
 */
public class Controller {
	
	Elevator[] elevators;
	
	public Controller(Elevator[] elevators) {
		this.elevators = elevators;
	}
	
	/*
	 * Method to call for an elevator.
	 * Might be the only public method of this class.
	 * @param: source and destination for the traveller
	 * @returns: true if the parameters are good and false if the parameters are out of bounds.
	 */
	public boolean requestElevator(int from, int to) {
		return false;
	}
	
}