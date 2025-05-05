package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static final String DB_URL = "jdbc:sqlserver://localhost;databaseName=AttendanceDB;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "LMS_Admin";
    private static final String DB_PASSWORD = "enikin0322";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
