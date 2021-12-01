package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    private static Connection conn = DBManager.getConnection();

    public static ObservableList<User> allUsers = FXCollections.observableArrayList();

    public UserDAO() {} // Constructor.

    public static ObservableList<User> getAllUsers() {
        String sqlStatement = "SELECT * FROM users";
        try {
            DBQuery.setStatement(conn);
            Statement statement = DBQuery.getStatement();
            statement.execute(sqlStatement);
            ResultSet rs = statement.getResultSet();
            while (rs.next()) {
                User tempUser = new User();
                tempUser.setUserId(rs.getInt("User_ID"));
                tempUser.setUserName(rs.getString("User_Name"));
                tempUser.setPassword(rs.getString("Password"));
                allUsers.add(tempUser);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allUsers;
    }

    public static User getUser(int userId) throws SQLException {
        String selectStatement = "SELECT * FROM users WHERE User_ID=" + userId; // SQL
        User currentUser = new User();

        try {
            DBQuery.setStatement(conn);
            Statement statement = DBQuery.getStatement();
            statement.execute(selectStatement);
            ResultSet results = statement.getResultSet();
            if (results.next()) {
                currentUser.setUserName(results.getString("User_Name"));
                currentUser.setPassword(results.getString("Password"));
            }
        }
        catch (Exception e) {
            System.out.print("error");
        }
        return currentUser;
    }
}
