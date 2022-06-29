/** DB.java */

package iot;

public class DB {
	private final static String DB_NAME = "test";
	private final static int PORT = 3306;
	public final static String SENSOR_READING_TABLE = "sensor_readings";
	public final static String SENSOR_DISTORTED_READINGS_TABLE = "sensor_dist_readings";
	public final static String SENSOR_REPO_TABLE = "sensor_repo";
	public final static String SENSOR_KEY_TABLE = "sensor_key";
	public final static String DRIVER = "com.mysql.cj.jdbc.Driver";
	public final static String URL = "jdbc:mysql://localhost:"+PORT+"/"+DB_NAME;
	public final static String USER = "abhishek";
	public final static String PASSWORD = "1234";
}
