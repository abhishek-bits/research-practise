package iot;

public class DB {
	private final static String DB_NAME = "test";
	private final static int PORT = 3306;
	public final static String DRIVER = "com.mysql.cj.jdbc.Driver";
	public final static String URL = "jdbc:mysql://localhost:"+PORT+"/"+DB_NAME;
	public final static String USER = "abhishek";
	public final static String PASSWORD = "1234";
}
