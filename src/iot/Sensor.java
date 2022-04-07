package iot;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Sensor {
	
	private static final int LOW = 500;
	private static final int HIGH = 800;
	private Socket socket;
	
	public Sensor() {
		connectToServer();
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
	
	public static void main(String[] args) throws InterruptedException {
		
		Sensor sensor = new Sensor();
//		System.out.println("Sensor starts sending reading to server...");
		while(true) {
			sensor.sendReadingToServer();
			Thread.sleep(1000);
		}
	}
	
}
