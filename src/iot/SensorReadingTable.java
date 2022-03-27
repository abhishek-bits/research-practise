package iot;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

public class SensorReadingTable {
	
	private static Map<Integer, LinkedList<Integer>> sensorReadingTable = new HashMap<>();
	
	public SensorReadingTable() {
		sensorReadingTable = new HashMap<>();
	}
	
	public void addReading(String msg) {
		String[] items = msg.split(" ");
		int sensorId = Integer.parseInt(items[0]);
		int sensorReading = Integer.parseInt(items[1]);
		addNewEntry(sensorId, sensorReading);
	}
	
	private void addNewEntry(int sensorId, int sensorReading) {
		if(!sensorReadingTable.containsKey(sensorId)) {
			sensorReadingTable.put(sensorId, new LinkedList<>());
		}
		// If size == 5, then remove the first reading received.
		// i.e. remove the node at the tail end of the Linked List.
		if(sensorReadingTable.get(sensorId).size() == 5) {
			sensorReadingTable.get(sensorId).removeLast();
		}
		// Insert the latest sensor reading at the head.
		sensorReadingTable.get(sensorId).addFirst(sensorReading);
	}

	public Map<Integer,LinkedList<Integer>> getSensorReadingTable() {
		return sensorReadingTable;
	}
	
}
