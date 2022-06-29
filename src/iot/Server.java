/** Server.java */

package iot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Server {
	
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
		
		/** Filtered Delta Mean Difference Algorithm **/
		query = "drop table if exists "+DB.SENSOR_READING_TABLE+";";
		stmt.execute(query);
		query = "drop table if exists "+DB.SENSOR_DISTORTED_READINGS_TABLE+";";
		stmt.execute(query);
		query = "drop table if exists "+DB.SENSOR_KEY_TABLE+";";
		stmt.execute(query);
		/**********************************************/
		query = "drop table if exists "+DB.SENSOR_REPO_TABLE+";";
		stmt.execute(query);
		
		query = "create table if not exists "+DB.SENSOR_REPO_TABLE+"("
				+ "SEN_ID integer PRIMARY KEY AUTO_INCREMENT,"
				+ "SEN_PORT integer not null"
				+ ");";
		stmt.execute(query);
		query = "create table if not exists "+DB.SENSOR_READING_TABLE+"("
				+ "SEN_ID integer NOT NULL,"
				+ "SEN_READ integer NOT NULL,"
				+ "TS DATETIME NOT NULL,"
				+ "FOREIGN KEY (SEN_ID) REFERENCES "+DB.SENSOR_REPO_TABLE+"(SEN_ID)"
				+ ");";
		stmt.execute(query);
		/** Filtered Delta Mean Difference Algorithm **/
		query = "create table if not exists "+DB.SENSOR_DISTORTED_READINGS_TABLE+"("
				+ "SEN_ID integer NOT NULL,"
				+ "SEN_DIST_READ integer NOT NULL,"
				+ "TS DATETIME NOT NULL,"
				+ "FOREIGN KEY (SEN_ID) REFERENCES "+DB.SENSOR_REPO_TABLE+"(SEN_ID)"
				+ ");";
		stmt.execute(query);
		query = "create table if not exists "+DB.SENSOR_KEY_TABLE+"("
				+ "SEN_ID integer NOT NULL,"
				+ "SEN_KEY integer NOT NULL,"
				+ "TS DATETIME NOT NULL,"
				+ "FOREIGN KEY (SEN_ID) REFERENCES "+DB.SENSOR_REPO_TABLE+"(SEN_ID)"
				+ ");";
		stmt.execute(query); 
		/**********************************************/
		
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
			SensorHandler sensorObj = new SensorHandler(sensor, stmt);
			
			// Now, parallely handle this newly added sensor
			new Thread(sensorObj).start();
		}
	}
	
}
