package iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Multi-threaded implementation of Sensors
 */
public class SensorHandler implements Runnable {
	
	private final Socket sensorSocket;
	private final Statement stmt;
	private int sensorID;
	
	public SensorHandler(Socket socket, Statement stmt) {
		this.sensorSocket = socket;
		this.stmt = stmt;
	}
	
	@Override
	public void run() {
		
		String query = null;
		BufferedReader in = null;
		
		try {
			
			// Add this sensor information into the Status table
			query = "INSERT INTO "+DB.SENSOR_STATUS_TABLE+"(SEN_PORT) "
					+ "VALUES("+sensorSocket.getPort()+");";
			stmt.execute(query);
			
			// Find and store the Unique ID for this sensor
			query = "SELECT SEN_ID from "+DB.SENSOR_STATUS_TABLE+" where SEN_PORT=" + sensorSocket.getPort() + ";"; 
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				sensorID = rs.getInt("SEN_ID");
			}
			
			System.out.println("Receiving readings from Sensor ID = " + sensorID);
			
			// Now each time this sensor sends a reading,
			// it will be updated ito the Reading table
			while(true) {
				// Read the value from the router
				in = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()));
				
				int reading = Integer.parseInt(in.readLine());
				
				// System.out.println("Received reading = " + reading + " kWhr from sensor " + sensorID);
								
				query = "INSERT INTO " + DB.SENSOR_READING_TABLE + "(SEN_ID, SEN_READ) "
						+ "values(" + sensorID + "," + reading + ");";
				
				stmt.execute(query);
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null) {
					in.close();
				}
				sensorSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
