package iot;

import java.util.LinkedList;
import java.util.Map;

public class Master implements Runnable {
	
	SensorReadingTable sensorReadingTable = null;
	
	public Master(SensorReadingTable sensorReadingTable) {
		this.sensorReadingTable = sensorReadingTable;
	}
	
	@Override
	public void run() {
		start();
	}
	
	private void start() {
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			showTable();
		}
	}
	
	private void showTable() {
		System.out.println("+------- Sensor Reading Table -------+");
		for(Map.Entry<Integer, LinkedList<Integer>> entry : sensorReadingTable.getSensorReadingTable().entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue());
		}
	}
	
}
