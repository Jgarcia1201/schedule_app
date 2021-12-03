package utility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBQuery {

    private static Statement statement; // Statement reference
    private static PreparedStatement ps;

    // Create Statement Object:
    public static void setStatement(Connection conn) throws SQLException {
        statement = conn.createStatement();
    }

    // Create Prepared Statement:
    public static void setPreparedStatement(Connection conn, String sql) throws SQLException {
        ps = conn.prepareStatement(sql);
    }

    // Return Statement Object
    public static Statement getStatement() {
        return statement;
    }

    // Return Prepared Statement Object
    public static PreparedStatement getPreparedStatement() {
        return ps;
    }
}
