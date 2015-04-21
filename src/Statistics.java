import java.io.File;
import com.almworks.sqlite4java.*;

/**
 * 
 * @author Viktor Björkholm & Jesper Bränn
 * @version 2015-04-07 
 *
 */
public class Statistics {
	SQLiteConnection db;
	double second;
	int algorithmID;
	int counter;
	int numPeople;
	int numElevators;
	
	public Statistics(String dbPath, double second, int algorithm, int numPeople, int numElevators) {
		counter = 0;
		this.second = second;
		this.numPeople = numPeople;
		this.numElevators = numElevators;
		algorithmID = algorithm;
		
		File dbFile = new File(dbPath);
		boolean newDB = (dbFile.exists()) ? false : true; 
		db = new SQLiteConnection(dbFile);
		try {	
			db.open(true);
			if(newDB) {
				System.out.println("Creating new DB");
				db.exec("BEGIN TRANSACTION");
				db.exec("CREATE TABLE statistics (id INTEGER PRIMARY KEY AUTOINCREMENT, algorithm_id INTEGER, num_elevators INTEGER, num_people INTEGER, person_id INTEGER, type VARCHAR(255), duration REAL, distance INTEGER, from_node INTEGER, to_node INTEGER)");
				db.exec("COMMIT");
			}

			db.exec("BEGIN");
		} catch(SQLiteException e) {
			System.err.println("Could not connect to the database");
		}
	}
	
	public void addWaitingTime(int personID, int ticksDuration) {
		checkCounter();
		SQLiteStatement st = null;
		try {
			st = db.prepare("INSERT INTO statistics (person_id, algorithm_id, num_elevators, num_people, type, duration) VALUES (?, ?, ?, ?, ?, ?)");
			st.bind(1, personID);
			st.bind(2, algorithmID);
			st.bind(3, numElevators);
			st.bind(4, numPeople);
			st.bind(5, "wait");
			st.bind(6, ticksDuration * second);
			st.stepThrough();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}
	}
	
	public void addTravelTime(int personID, int ticksDuration, int distance, int from, int to) {
		checkCounter();
		SQLiteStatement st = null;
		try {
			st = db.prepare("INSERT INTO statistics (person_id, algorithm_id, num_elevators, num_people, type, duration, distance, from_node, to_node) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			st.bind(1, personID);
			st.bind(2, algorithmID);
			st.bind(3, numElevators);
			st.bind(4, numPeople);
			st.bind(5, "travel");
			st.bind(6, ticksDuration * second);
			st.bind(7, distance);
			st.bind(8, from);
			st.bind(9, to);
			st.stepThrough();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}
	}
	
	public void checkCounter() {
		counter++;
		if(counter > 8000) {
			System.err.println("Writing to the mothafuken log");
			try {
				db.exec("COMMIT");
				db.exec("BEGIN");
			} catch(Exception e){}
			counter = 0;
		}
	}
}
