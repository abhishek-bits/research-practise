package iot;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SensorRepo implements Runnable {
	
	private final static int SENSOR_COUNT = 4;
	
	private static List<Sensor> sensors = null;
	
	public SensorRepo() {
		sensors = new ArrayList<>();
	}
	
	@Override
	public void run() {
		sendReadingToRouter();
	}
	
	private void sendReadingToRouter() {
		
		while(true) {
			
			for(Sensor sensor : sensors) {
				
				int sensorId = sensor.getId();
				int reading = sensor.getReading();
				Socket socket = sensor.getSocket();
				PrintWriter out;
				
				String msg = sensorId + " " + reading;
				
				// System.out.println("Sensor " + sensorId + " sent reading = " + reading + " kWhr.");
				
				try {
					// prepare output channel for this sensor
					out = new PrintWriter(socket.getOutputStream(), true);
					
					// send this reading through the channel
					out.println(msg);
					
					Thread.sleep(1000);
					
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}

	private void addNewSensor(int id) {
		try {
			Socket socket = new Socket("localhost", Port.SERVER_PORT);
			Sensor sensor = new Sensor(id, socket);
			sensors.add(sensor);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void createSensors(int n) {
		for(int id = 1; id <= n; id++) {
			addNewSensor(id);
		}
	}
	
	public static void main(String[] args) {
		
		SensorRepo sensorRepo = new SensorRepo();
		
		sensorRepo.createSensors(SENSOR_COUNT);
		
		Thread thread = new Thread(sensorRepo);
		thread.start();
		
	}
}
