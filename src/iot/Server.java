package iot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Router class
 */
public class Server {
	
	private final static String TABLE_NAME = "sensorreadings";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		String query = null;
		Connection conn = null;
		Statement stmt = null;
		ServerSocket server = null;
		
		/**
		 * Database Configuration
		 */
		// Load the Driver
		Class.forName(DB.DRIVER);
		
		// Establish the connection with MySQL database
		conn = DriverManager.getConnection(DB.URL, DB.USER, DB.PASSWORD);
		
		// Create Statement object
		stmt = conn.createStatement();
		
		query = "drop table "+TABLE_NAME+";";
		
		stmt.execute(query);
		
		query = "create table if not exists "+TABLE_NAME+"("
				+ "SEN_ID integer NOT NULL,"
				+ "SEN_READ integer NOT NULL,"
				+ "TS DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
				+ ");";
		
		stmt.execute(query);
		
		// System.out.println("Table created successfully!");
		
		// Initialize Server
		server = new ServerSocket(Port.SERVER_PORT);
		server.setReuseAddress(true);
		
		System.out.println("Server listening...");
		
		// we will wait for new requests
		while (true) {
			// Accept new sensor connection request
			Socket sensor = server.accept();
			
			// sensor object for the newly added sensor
			SensorHandler sensorObj = new SensorHandler(sensor, stmt, TABLE_NAME);
			
			// Now, parallely handle this newly added sensor
			new Thread(sensorObj).start();
		}
	}
	
}
