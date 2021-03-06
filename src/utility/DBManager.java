package utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBManager {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = LOCAL"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // DRIVER REFERENCE
    private static final String userName = "sqlUser"; // Username
    private static final String password = "passw0rd!"; // Password
    public static Connection connection; // Connection Interface

    public static void openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection Object
            System.out.println("Connection Successful!");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection()
    {
        try {
            connection.close();
            System.out.println("Connection Ended!");
        }
        catch (Exception e) {
            // do nothing.
        }
    }
}
