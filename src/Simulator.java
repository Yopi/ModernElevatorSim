/*
 * Main class for the simulator, creates all the other classes and triggers the ticks
 * in the eventloop.
 * 
 * Authors: Viktor Björkholm & Jesper Bränn
 * Date: 2015-03-26
 */

public class Simulator {
	
	Elevator[] elevators;
	Person[] persons;
	Building building;
	Graph graph;
	
	public Simulator() {
		building = new Building();
		
	}
	
	public static void main(String[] args) {
		// feck static.
		new Simulator();
	}

}
