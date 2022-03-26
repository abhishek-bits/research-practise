package iot;

public class Packet {

	private int sensorId;
	private int reading;
	
	public Packet(int sensorId, int reading) {
		this.sensorId = sensorId;
		this.reading = reading;
	}

	public int getReading() {
		return reading;
	}

	public int getSensorId() {
		return sensorId;
	}
	
	
}
