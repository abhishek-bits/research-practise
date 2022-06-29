/** Sensor.java */

package iot;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Sensor {
	
	private static final int LOW = 500;
	private static final int HIGH = 800;
	
	private Socket socket = null;
	private int id = -1;
	private String query = null;
	private Connection conn = null;
	private Statement stmt = null;
	private ResultSet res = null;
	
	public Sensor() {
		connectToServer();
		connectToDB();
		id = getIDFromDB();
	}
	
	private int getPort() {
		return socket.getLocalPort();
	}
	
	private int getReading() {
		return new Random().nextInt(HIGH - LOW) + LOW;
	}
	
	private void sendReadingToServer() {
		try {
			// prepare output channel for this sensor
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			String msg = String.valueOf(getReading());

			// send this reading through the channel
			out.println(msg);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connectToServer() {
		try {
			socket = new Socket("localhost", Port.SERVER_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void connectToDB() {
		try {
			// Load the Driver
			Class.forName(DB.DRIVER);
			
			// Establish the connection with MySQL database
			conn = DriverManager.getConnection(DB.URL, DB.USER, DB.PASSWORD);
			
			// Create Statement object
			stmt = conn.createStatement();
		
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	private int getIDFromDB() {
		try {
			query = "SELECT SEN_ID from " + DB.SENSOR_REPO_TABLE + " WHERE SEN_PORT = "+getPort()+";";
			res = stmt.executeQuery(query);
			if(res.next()) {
				return res.getInt("SEN_ID");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return -1;
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		Sensor sensor = new Sensor();
//		System.out.println("ID: " + sensor.id);
//		System.out.println("Key: " + sensor.key);
//		System.out.println("Port: " + sensor.getPort());
//		System.out.println("Sensor starts sending reading to server...");
		while(true) {
			sensor.sendReadingToServer();
			Thread.sleep(1000);
		}
	}
	
}
