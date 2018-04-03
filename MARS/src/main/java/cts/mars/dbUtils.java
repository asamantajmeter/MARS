package cts.mars;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class dbUtils {

	static Connection conn = null;	
	
	public static Connection dbConn(String conn_str, String usr, String pass) throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
        }
		
        conn = DriverManager.getConnection(conn_str,usr,pass);
	    return conn;
	}

	public static void main(String[] args) throws SQLException {
		String URI = "jdbc:oracle:thin:@//myrdsorcl.cahrgql4rsph.us-east-2.rds.amazonaws.com:1521/ORCL";
		String user = "tataiermail";
		String pass = "07dy8w9hq";
		String sql = "select * from PERSON";

		Connection dbConn = dbConn(URI,user,pass);
		Statement statement = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE , ResultSet.CONCUR_UPDATABLE);

		ResultSet rs = statement.executeQuery(sql);
	    ResultSetMetaData metadata = rs.getMetaData();
	    int columnCount = metadata.getColumnCount();   


	    while (rs.next()) {
	        String row = "";
	        for (int i = 1; i <= columnCount; i++) {
	            row += rs.getString(i) + ", ";          
	        }
	        System.out.println(row);


	    }
		

	}

}
