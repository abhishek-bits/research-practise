package iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Multi-threaded implementation of Sensors
 */
public class SensorHandler implements Runnable {
	
	private final Socket sensorSocket;
	private final String tableName;
	private final Statement stmt;
	
	public SensorHandler(Socket socket, Statement stmt, String tableName) {
		this.sensorSocket = socket;
		this.stmt = stmt;
		this.tableName = tableName;
	}
	
	@Override
	public void run() {
		
		PrintWriter out = null;
		BufferedReader in = null;
		
		try {
			
			while(true) {
				
				// Read the value from the router
				in = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()));
				
				String msg = in.readLine();
				
				String[] items = msg.split(" ");
				int sensorID = Integer.parseInt(items[0]);
				int reading = Integer.parseInt(items[1]);
				
				// System.out.println("Received reading = " + reading + " kWhr from sensor " + sensorID);
				
				/**
				 * Man In the Middle Attack
				 * DoS / DDoS attack
				 */
				
				String query = "INSERT INTO " + tableName + "(SEN_ID, SEN_READ) "
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
				if(out != null) {
					out.close();
				}
				sensorSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
