package iot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Router class
 */
public class Router {
	
	public static void main(String[] args) {
		
		//ServerSocket server = null;
		ServerSocket router = null;
		
		try {
			
			//server = new ServerSocket(Port.SERVER_PORT)
			router = new ServerSocket(Port.ROUTER_PORT);
			router.setReuseAddress(true);
			
			System.out.println("Router waiting for new requests...");
			
			// we will wait for new requests
			while (true) {
				
				// Accept new sensor connection request
				Socket sensor = router.accept();
				
				// sensor object for the newly added sensor
				SensorHandler sensorObj = new SensorHandler(sensor);
				
				// Now, parallely handle this newly added sensor
				new Thread(sensorObj).start();
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (router != null) {
				try {
					router.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
