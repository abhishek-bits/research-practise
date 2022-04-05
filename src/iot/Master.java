package iot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Master implements Runnable {
	
	private static Connection conn = null;
	private static Statement stmt = null;
	private static Map<Integer,Double> sensorReadingTable = null;
	
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
			
			// initialize the Table
			initData();
			
			// Get data from MySQL
			getData();
			
			// Show data
			showData();
		}
	}
	
	private void initData() {
		sensorReadingTable = new HashMap<>();
	}

	private void getData() {
		// What is the average reading of each sensor
		String query = "select SEN_ID, AVG(SEN_READ) from sensorreadings group by sen_id;";
		try {
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()) {
				int sensorId = Integer.parseInt(rs.getString("SEN_ID"));
				double avgSensorReading = Double.parseDouble(rs.getString("AVG(SEN_READ)"));
				sensorReadingTable.put(sensorId, avgSensorReading);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void showData() {
		System.out.println("+------- Sensor Reading Table -------+");
		System.out.println("SensorID -> Avg. Sensor Reading");
		for(Map.Entry<Integer, Double> entry : sensorReadingTable.entrySet()) {
			System.out.println("\t"+entry.getKey() + " -> " + entry.getValue());
		}
	}

}
