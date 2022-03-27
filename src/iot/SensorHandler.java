package iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Multi-threaded implementation of Sensors
 */
public class SensorHandler implements Runnable {
	
	private final Socket sensorSocket;
	private final SensorReadingTable sensorReadingTable;
	
	public SensorHandler(Socket socket, SensorReadingTable sensorReadingTable) {
		this.sensorSocket = socket;
		this.sensorReadingTable = sensorReadingTable;
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
				int sensorId = Integer.parseInt(items[0]);
				int reading = Integer.parseInt(items[1]);
				
				System.out.println("Received reading = " + reading + " kWhr from sensor " + sensorId);
				
				/**
				 * Man In the Middle Attack
				 * DoS / DDoS attack
				 */
				
				sensorReadingTable.addReading(msg);
				
			}
			
		} catch(IOException e) {
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
