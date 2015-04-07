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
	
	public Statistics(String dbPath, double second) {
		this.second = second;
		
		File dbFile = new File(dbPath);
		boolean newDB = (dbFile.exists()) ? false : true; 
		db = new SQLiteConnection(dbFile);
		try {	
			db.open(true);
			if(newDB) {
				db.exec("BEGIN TRANSACTION");
				db.exec("CREATE TABLE statistics (id INTEGER PRIMARY KEY AUTOINCREMENT, person_id INTEGER, type VARCHAR(255), duration REAL, distance INTEGER)");
				db.exec("COMMIT");
			}
		} catch(SQLiteException e) {
			System.err.println("Could not connect to the database");
		}
	}
	
	public void addWaitingTime(int personID, int ticksDuration) {
		SQLiteStatement st = null;
		try {
			st = db.prepare("INSERT INTO statistics (person_id, type, duration) VALUES (?, ?, ?)");
			st.bind(1, personID);
			st.bind(2, "wait");
			st.bind(3, ticksDuration * second);
			st.step();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}
	}
	
	public void addTravelTime(int personID, int ticksDuration, int distance) {
		SQLiteStatement st = null;
		try {
			st = db.prepare("INSERT INTO statistics (person_id, type, duration, distance) VALUES (?, ?, ?, ?)");
			st.bind(1, personID);
			st.bind(2, "travel");
			st.bind(3, ticksDuration * second);
			st.step();
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			st.dispose();
		}
	}
}
