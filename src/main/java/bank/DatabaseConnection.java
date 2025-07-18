package bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	
	
    private static final String URL = "jdbc:mysql://localhost:3306/BankDB";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {

    	    try {
    	        Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
    	    } catch (ClassNotFoundException e) {
    	        throw new SQLException("Driver not found", e);
    	    }
    	    return DriverManager.getConnection(URL, USER, PASSWORD);
    	
        
    }
}
