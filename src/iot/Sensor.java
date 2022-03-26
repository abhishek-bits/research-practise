package iot;

import java.net.Socket;
import java.util.Random;

public class Sensor {
	
	private static final int LOW = 500;
	private static final int HIGH = 800;
	private Socket socket;
	private int id;
	
	public Sensor(int sensorId, Socket socket) {
		this.id = sensorId;
		this.socket = socket;
	}
	
	public int getReading() {
		return new Random().nextInt(HIGH - LOW) + LOW;
	}
	
	public int getId() {
		return id;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
}
