/** Master.java */

package iot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Master implements Runnable {
	
	private static final double e = 3;		// distorted factor
	private static final int m = 1;			// threshold value
	private static final int delta_th = 1;	// threshold value
	
	private static Connection conn = null;
	private static Statement stmt = null;
	private ResultSet rs = null;
	
	private Map<Integer,List<Integer>> sensorKeyTable = null;
	private Map<Integer,List<Integer>> sensorReadingTable = null;
	private Map<Integer,List<Integer>> distortedSensorReadingTable = null;
	private Map<Integer,List<Integer>> delta = null;
	private Map<Integer,List<Integer>> delta_dash = null;
	private Map<Integer,List<Boolean>> delta_dash_filtered = null;
	private Set<Timestamp> timestamps = null;
	
	private Map<Integer,Set<Integer>> s01 = null;
	private Map<Integer,Set<Integer>> s10 = null;
	
	private int sensorCount = 0;
	private String query = null;
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		
		// Load the driver
		Class.forName(DB.DRIVER);
		
		// Establish connection with the MySQL database.
		conn = DriverManager.getConnection(DB.URL, DB.USER, DB.PASSWORD);
		
		// Create statement object
		stmt = conn.createStatement();
		
		Master masterObj = new Master();
		new Thread(masterObj).start();
		
	}
	
	@Override
	public void run() {
		start();
	}
	
	private void start() {
		while(true) {
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// update the no. of sensors
			updateSensorCount();
			
			// get K recent Time stamps
			updateTimestampTable(10);
			
			// populate Sensor key table
			// based on the K recent Timestamp values read above.
			populateSensorKeyTable();
			
			// populate Sensor Reading Table
			// based on the K recent Timestamp values read above.
			populateSensorReadingTable();
			
			// populate Distorted sensor Reading Table
			// based on the K recent Timestamp values read above.
			populateDistortedSensorReadingTable();
			
//			System.out.println(timestamps);
//			System.out.println(sensorKeyTable);
//			System.out.println(sensorReadingTable);
//			System.out.println(distortedSensorReadingTable);
			
			// Delta Sequence Creation Step
			deltaSequenceCreation();
			
			// Delta Filtration Step
			dataFilter();
			
			// Data Partitioning Step
			dataPartitioning();
			
			// Attack Detection Step
			detectAttack();
		}
	}

	/**
	 * Attack Detection Step
	 */
	private void detectAttack() {
		System.out.println("+------------------------------+");
		for(int i = 1; i <= sensorCount; i++) {
			// If size of filtered Delta-dash is less than a threshold m, then
			// raise alarm to detect attack
			if(getFilteredDeltaDashCountByID(i) < m) {
				// Raise alarm to detect attack
				raiseAlarm(i);
			} else {
				long sum = 0L;
				double avg01 = 0.0D, avg10 = 0.0D;
				for(Integer idx : s01.get(i)) {
					sum += delta_dash.get(i).get(idx);
				}
				avg01 = sum / s01.get(i).size();
				
				sum = 0L;
				for(Integer idx : s10.get(i)) {
					sum += delta_dash.get(i).get(idx);
				}
				avg10 = sum / s10.get(i).size();
				
				avg01 = Math.abs(avg01);
				avg10 = Math.abs(avg10);
				
				if(!(avg01 - avg10 >= 2 * e && avg01 - avg10 <= 6 * e)) {
					raiseAlarm(i);
				} else {
					safe(i);
				}
			}
		}
	}

	private int getFilteredDeltaDashCountByID(int id) {
		int count = 0;
		List<Boolean> list = delta_dash_filtered.get(id);
		for(Boolean item : list) {
			if(item == false) {
				count++;
			}
		}
		return count;
	}

	private void safe(int id) {
		System.out.println("Sensor "+id+" is safe.");
	}
	
	private void raiseAlarm(int id) {
		System.out.println("Sensor "+id+" compromised. RAISE ALARM!!!");
	}
	
	/**
	 * Partitions the data accross sets based on the variation of keys
	 */
	private void dataPartitioning() {
		// set of all indices i such that k[i] = 0 and k[i+1] = 1
		s01 = new HashMap<>(sensorCount);
		// set of all indices i such that k[i] = 1 and k[i+1] = 0
		s10 = new HashMap<>(sensorCount);
		
		for(int i = 1; i <= sensorCount; i++) {
			// insert new entry into the Hash Table
			s01.put(i, new HashSet<>());
			s10.put(i, new HashSet<>());
			// get all keys for this sensor
			List<Integer> keys = sensorKeyTable.get(i);
			for(int j = 0; j < keys.size() - 1; j++) {
				if(keys.get(j) == 0 && keys.get(j + 1) == 1) {
					// if the corresponding value in delta-dash table is not filtered out
//					if(delta_dash_filtered.get(i).get(j) == false) {
						s01.get(i).add(j);
//					}
				} else if(keys.get(j) == 1 && keys.get(j + 1) == 0) {
					// if the corresponding value in delta-dash table is not filtered out
//					if(delta_dash_filtered.get(i).get(j) == true) {
						s10.get(i).add(j);
//					}
				}
			}
		}
	}

	/**
	 * Filter out the Delta-dash values those which does not satisfy
	 */
	private void dataFilter() {
		delta_dash_filtered = new HashMap<>();
		for(int i = 1; i <= sensorCount; i++) {
			delta_dash_filtered.put(i, new ArrayList<>());
			List<Integer> delta_dash_list = delta_dash.get(i);
			for(Integer delta_dash : delta_dash_list) {
				if(delta_dash < delta_th) {
					delta_dash_filtered.get(i).add(true);
				} else {
					delta_dash_filtered.get(i).add(false);
				}
			}
		}
	}
	
	/**
	 * Prepares Delta and Delta_dash Hash Tables
	 */
	private void deltaSequenceCreation() {
		delta = new HashMap<>(sensorCount);
		delta_dash = new HashMap<>(sensorCount);
		for(Map.Entry<Integer, List<Integer>> entry : sensorReadingTable.entrySet()) {
			int id = entry.getKey();
			delta.put(id, new ArrayList<>());
			List<Integer> dList = entry.getValue();
			for(int i = 0; i < dList.size() - 1; i++) {
				// Delta[i] = d[i + 1] - d[i]
				delta.get(id).add(dList.get(i + 1) - dList.get(i));
			}
		}
		for(Map.Entry<Integer, List<Integer>> entry : distortedSensorReadingTable.entrySet()) {
			int id = entry.getKey();
			delta_dash.put(id, new ArrayList<>());
			List<Integer> d_dashList = entry.getValue();
			for(int i = 0; i < d_dashList.size() - 1; i++) {
				// Delta_dash[i] = d_dash[i + 1] - d_dash[i]
				delta_dash.get(id).add(d_dashList.get(i + 1) - d_dashList.get(i));
			}
		}
	}

	/**
	 * read 10 most recent distorted readings for each sensor from DB.
	 */
	private void populateDistortedSensorReadingTable() {
		// initialize Distorted Sensor Reading Hash Table
		initDistortedSensorReadingTable();
		for(Map.Entry<Integer,List<Integer>> entry : distortedSensorReadingTable.entrySet()) {
			int id = entry.getKey();
			distortedSensorReadingTable.get(id).addAll(getKRecentDistortedSensorReadingsByID(id));
		}
	}
	
	/**
	 * reads k most recent readings for the given sensor from DB
	 * uses the Timestamps from the Timestamp table to read recent data from DB
	 * @param id
	 * @return
	 */
	private List<Integer> getKRecentDistortedSensorReadingsByID(int id) {
		List<Integer> list = new ArrayList<>();
		for(Timestamp ts : timestamps) {
			query = "SELECT SEN_DIST_READ FROM "+DB.SENSOR_DISTORTED_READINGS_TABLE+" WHERE SEN_ID="+id+" AND TS = '"+ts+"';";
			try {
				rs = stmt.executeQuery(query);
				if(rs.next()) {
					list.add(rs.getInt("SEN_DIST_READ"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * read 10 most recent readings for each sensor from DB.
	 */
	private void populateSensorReadingTable() {
		// initialize Sensor Reading Hash Table
		initSensorReadingTable();
		for(Map.Entry<Integer,List<Integer>> entry : sensorReadingTable.entrySet()) {
			int id = entry.getKey();
			sensorReadingTable.get(id).addAll(getKRecentSensorReadingsByID(id));
		}
	}
	
	/**
	 * reads k most recent readings for the given sensor from DB
	 * uses the Timestamps from the Timestamp table to read recent data from DB
	 * @param id
	 * @return
	 */
	private List<Integer> getKRecentSensorReadingsByID(int id) {
		List<Integer> list = new ArrayList<>();
		for(Timestamp ts : timestamps) {
			query = "SELECT SEN_READ FROM "+DB.SENSOR_READING_TABLE+" WHERE SEN_ID="+id+" AND TS = '"+ts+"';"; 
			try {
				rs = stmt.executeQuery(query);
				if(rs.next()) {
					list.add(rs.getInt("SEN_READ"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * reads the key value alloted to each sensor
	 */
	private void populateSensorKeyTable() {
		// initialize Sensor Key Hash Table
		initSensorKeyTable();
		for(int id = 1; id <= sensorCount; id++) {
			sensorKeyTable.get(id).addAll(getKRecentSensorKeysByID(id));
		}
	}
	
	/**
	 * reads the key value alloted to the given sensor from DB
	 * @param id
	 * @return
	 */
	private List<Integer> getKRecentSensorKeysByID(int id) {
		List<Integer> list = new ArrayList<>();
		for(Timestamp ts : timestamps) {
			query = "SELECT SEN_KEY FROM "+DB.SENSOR_KEY_TABLE+" WHERE SEN_ID="+id+" AND TS = '"+ts+"';";
			try {
				rs = stmt.executeQuery(query);
				if(rs.next()) {
					list.add(rs.getInt("SEN_KEY"));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * initializes the distorted sensor reading table with the current sensor count value
	 */
	private void initDistortedSensorReadingTable() {
		distortedSensorReadingTable = new HashMap<>(sensorCount);
		for(int i = 1; i <= sensorCount; i++) {
			distortedSensorReadingTable.put(i, new ArrayList<>());
		}
	}
	
	/**
	 * initializes the sensor reading table with the current sensor count value
	 */
	private void initSensorReadingTable() {
		sensorReadingTable = new HashMap<>(sensorCount);
		for(int i = 1; i <= sensorCount; i++) {
			sensorReadingTable.put(i, new ArrayList<>());
		}
	}
	
	/**
	 * initializes the sensor key table with the current sensor count value
	 */
	private void initSensorKeyTable() {
		sensorKeyTable = new HashMap<>(sensorCount);
		for(int i = 1; i <= sensorCount; i++) {
			sensorKeyTable.put(i, new ArrayList<>());
		}
	}
	
	/**
	 * Get K most recent Timestamps from the Database
	 * Based on these timestamps, Filtered-Delta mean difference algo. is implemented
	 */
	private void updateTimestampTable(int k) {
		query = "SELECT TS FROM "+DB.SENSOR_READING_TABLE+" ORDER BY TS DESC;";
		try {
			rs = stmt.executeQuery(query);
			timestamps = new HashSet<>();
			while(rs.next() && timestamps.size() < k) {
				timestamps.add(rs.getTimestamp("TS"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * updates the count of sensors.
	 */
	private void updateSensorCount() {
		query = "SELECT COUNT(*) FROM "+DB.SENSOR_REPO_TABLE+";";
		try {
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				sensorCount = rs.getInt("COUNT(*)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}