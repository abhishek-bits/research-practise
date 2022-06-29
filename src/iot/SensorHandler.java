/** SensorHandler.java */

package iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

public class SensorHandler implements Runnable {
	
	private static final int e = 3;
	private final Socket sensorSocket;
	private final Statement stmt;
	private int sensorID;
	private int sensorKey;
	private int d;			// actual reading from the sensor
	private int d_dash;		// distorted reading from the sensor
	private String query;
	private ResultSet rs;
	private Timestamp ts;
	
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
			query = "INSERT INTO "+DB.SENSOR_REPO_TABLE+"(SEN_PORT) "
					+ "VALUES("+sensorSocket.getPort()+");";
			stmt.execute(query);
			
			// Find and store the Unique ID for this sensor
			query = "SELECT SEN_ID from "+DB.SENSOR_REPO_TABLE+" where SEN_PORT=" + sensorSocket.getPort() + ";"; 
			rs = stmt.executeQuery(query);
			if(rs.next()) {
				sensorID = rs.getInt("SEN_ID");
			}			
			
			System.out.println("Receiving readings from Sensor ID = " + sensorID);
			
			// Now each time this sensor sends a reading,
			// it will be updated ito the Reading table
			while(true) {
				// Read the value from the router
				in = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()));
								
				/** Filtered Delta Mean Difference Algorithm **/
				
				//Get the actual reading from the sensor
				d = Integer.parseInt(in.readLine());
				
				// Collect the Timestamp
				ts = new Timestamp(new Date().getTime());
				
				// First, allot a fresh key to this Sensor
				sensorKey = new Random().nextInt(2);
				
				query = "INSERT INTO " + DB.SENSOR_KEY_TABLE+ " "
						+ "values(" + sensorID + ", " + sensorKey + ", '" + ts + "');";
				stmt.execute(query);
				
				// First, we store the actual reading first
				query = "INSERT INTO " + DB.SENSOR_READING_TABLE + " "
						+ "values(" + sensorID + ", " + d + ", '" + ts + "');";
				stmt.execute(query);
				
				// Now, depending on the key, calculate the distorted value
				d_dash = sensorKey == 1 ? d + e : d - e;
				
				// Now, store the distorted sensor reading
				query = "INSERT INTO " + DB.SENSOR_DISTORTED_READINGS_TABLE + " "
						+ "values(" + sensorID + ", " + d_dash + ", '" + ts + "');"; 
				
				stmt.execute(query);
				
				/**********************************************/

			}
			
		} catch (IOException e) {
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
