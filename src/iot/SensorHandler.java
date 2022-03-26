package iot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Multi-threaded implementation of Sensor
 */
public class SensorHandler implements Runnable {
	
	// private final ServerSocket serverSocket;
	private final Socket sensorSocket;
	
	public SensorHandler(Socket socket) {
		// this.serverSocket = serverSocket;
		this.sensorSocket = socket;
	}
	
	@Override
	public void run() {
		
		PrintWriter out = null;
		BufferedReader in = null;
		
		try {
			
			while(true) {
				
				// Read the value from the router
				in = new BufferedReader(new InputStreamReader(sensorSocket.getInputStream()));
				
				String[] msg = in.readLine().split(" ");
				int sensorId = Integer.parseInt(msg[0]);
				int reading = Integer.parseInt(msg[1]);
				
				System.out.println("Sensor " + sensorId + " sent reading = " + reading + " kWhr.");
				
				// Now, send this value to the server
				// out = new PrintWriter(serverSocket.);
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
