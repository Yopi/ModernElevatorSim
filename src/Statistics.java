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
	double hour;
	int algorithmID;
	int counter;
	int numPeople;
	int numElevators;
	
	public Statistics(String dbPath, double second, int algorithm, int numPeople, int numElevators) {
		counter = 0;
		this.second = second;
		this.hour = second*3600;
		this.numPeople = numPeople;
		this.numElevators = numElevators;
		algorithmID = algorithm;

		java.util.logging.Logger.getLogger("com.almworks.sqlite4java").setLevel(java.util.logging.Level.OFF);

		File dbFile = new File(dbPath);
		boolean newDB = (dbFile.exists()) ? false : true;
		db = new SQLiteConnection(dbFile);
		try {
		//	db.setBusyTimeout(800);
			db.open(true);
			if(newDB) {
				System.out.println("Creating new DB");
				db.exec("BEGIN TRANSACTION");
				db.exec("CREATE TABLE statistics (id INTEGER PRIMARY KEY AUTOINCREMENT, algorithm_id INTEGER, timestamp TEXT, num_elevators INTEGER, num_people INTEGER, person_id INTEGER, type VARCHAR(255), duration REAL, distance INTEGER, from_node INTEGER, to_node INTEGER)");
				db.exec("COMMIT");
			}

			//db.exec("BEGIN");
		} catch(SQLiteException e) {
			System.err.println("Could not connect to the database");
		}
	}
	
	public void addWaitingTime(int personID, int ticksDuration, int time) {
		checkCounter();
		SQLiteStatement st = null;
		String timestamp = getTimestamp(time);
		try {
			st = db.prepare("INSERT INTO statistics (person_id, algorithm_id, timestamp, num_elevators, num_people, type, duration) VALUES (?, ?, ?, ?, ?, ?, ?)");
			st.bind(1, personID);
			st.bind(2, algorithmID);
			st.bind(3, timestamp);
			st.bind(4, numElevators);
			st.bind(5, numPeople);
			st.bind(6, "wait");
			st.bind(7, ticksDuration * second);
			st.stepThrough();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}
	}
	
	public void addTravelTime(int personID, int ticksDuration, int distance, int from, int to, int time) {
		checkCounter();
		SQLiteStatement st = null;
		String timestamp = getTimestamp(time);
		try {
			st = db.prepare("INSERT INTO statistics (person_id, algorithm_id, timestamp, num_elevators, num_people, type, duration, distance, from_node, to_node) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			st.bind(1, personID);
			st.bind(2, algorithmID);
			st.bind(3, timestamp);
			st.bind(4, numElevators);
			st.bind(5, numPeople);
			st.bind(6, "travel");
			st.bind(7, ticksDuration * second);
			st.bind(8, distance);
			st.bind(9, from);
			st.bind(10, to);
			st.stepThrough();

		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}
	}
	
	public void checkCounter() {
		counter++;
		if(counter > 50) {
			//System.err.println("Writing to the log");
			try {
				db.exec("COMMIT");
			} catch(Exception e){}

			try {
				db.exec("BEGIN");
			} catch(Exception e){}
			counter = 0;
		}
	}

	private String getTimestamp(int time) {
		//  (int)(localTime/hour) + ":" + (int)(localTime/hour * 60 % 60) + ":" + (int)(localTime/hour * 3600 % 60))
		// hour : minutes : second

		StringBuilder sb = new StringBuilder();
		int hours = (int)(time / (hour));
		if (hours < 10) {
			sb.append("0");
		}
		sb.append(hours);
		sb.append(":");
		int minutes = (int)(((time / hour) * 60) % 60);
		if (minutes < 10) {
			sb.append("0");
		}
		sb.append(minutes);
		sb.append(":");

		int seconds = (int)(time % 60);
		if (seconds < 10) {
			sb.append("0");
		}
		sb.append(seconds);

		return sb.toString();
	}
}
