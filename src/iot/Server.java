package iot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Router class
 */
public class Server {
	
	public static void main(String[] args) {
		
		ServerSocket server = null;
		SensorReadingTable sensorReadingTable = new SensorReadingTable();
		
		try {
			// Initialize Server
			server = new ServerSocket(Port.SERVER_PORT);
			server.setReuseAddress(true);
			
			System.out.println("Server waiting for new requests...");
			
			// master object for the newly added sensor
			Master masterObj = new Master(sensorReadingTable);
			new Thread(masterObj).start();
			
			// we will wait for new requests
			while (true) {
				
				// Accept new sensor connection request
				Socket sensor = server.accept();
				
				// sensor object for the newly added sensor
				SensorHandler sensorObj = new SensorHandler(sensor, sensorReadingTable);
				
				// Now, parallely handle this newly added sensor
				new Thread(sensorObj).start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
