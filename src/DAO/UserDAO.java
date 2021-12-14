package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDAO {

    private static Connection conn = DBManager.getConnection();

    public static ObservableList<User> allUsers = FXCollections.observableArrayList();

    public UserDAO() {} // Constructor.

    /**
     * A SQL statement is assigned to a String variable and the allUsers observable list is cleared to ensure correct data.
     * <p>
     *     The statement is executed and while rs.next() is true, a new User is created out of the values found in the
     *     columns of the database. These are added one by one to allUsers and returned.
     * </p>
     *
     * @return - an observable list containing allUsers in the database.
     */
    public static ObservableList<User> getAllUsers() {
        String sqlStatement = "SELECT * FROM users";
        allUsers.clear();
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

    /**
     * Checks every User's name against string specified in parameter. If a match is found, the User is returned.
     * @param s - String, name of desired User.
     * @return desired User.
     */
    public static User getUserByName(String s) {
        allUsers = getAllUsers();
        for (User u : allUsers) {
            if (u.getUserName().equals(s)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Checks every User's ID against string specified in parameter. If a match is found, the User is returned.
     * @param i - Integer, ID of desired User.
     * @return desired User.
     */
    public static User getUserById(int i) {
        allUsers = getAllUsers();
        for (User u: allUsers) {
            if (u.getUserId() == i) {
                return u;
            }
        }
        return null;
    }
}
