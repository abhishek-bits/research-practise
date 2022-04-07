package iot;

public class AttackerRepo {
	
	public static final Integer[] DEFAULT_PORTS = {80, 8080};
	public static final String[] DEFAULT_DB_NAMES = {"master", "root", "test", "db"};
	public static final String[] DEFAULT_SERVERS = {"localhost", "192.168.8.1"};
	public static final String[] DEFAULT_USERNAMES = {"root", "admin", "abhishek", "master"};
	public static final String[] DEFAULT_PASSWORDS = {"root", "1234", "password", "123456", "p@s$w0rd"};
	/*
	 *  Attacker came to know that organization uses MySQL database.
	 */
	public final static String DRIVER = "com.mysql.cj.jdbc.Driver";
	public final static String HALF_URL = "jdbc:mysql://";
	
}
