package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ContactDAO {

    private static Connection conn = DBManager.getConnection();

    private static ObservableList<Contact> allContacts = FXCollections.observableArrayList();

    /**
     * Checks every Contact's name against string specified in parameter. If a match is found, the Contact is returned.
     * @param s - String, name of desired Contact.
     * @return desired Contact.
     */
    public static Contact getContactByName(String s) {
        ObservableList<Contact> temp = getAllContacts();
        for (Contact c : temp) {
            if (s.equals(c.getContactName())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Checks every Contact's ID against string specified in parameter. If a match is found, the Contact is returned.
     * @param id - Integer, ID of desired Contact.
     * @return desired Contact.
     */
    public static Contact getContactById(int id) {
        ObservableList<Contact> temp = getAllContacts();
        for (Contact c : temp) {
            if (c.getContactId() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * A SQL statement is assigned to a String variable and the allContacts observable list is cleared to ensure correct data.
     * <p>
     *     The statement is executed and while rs.next() is true, a new Contact is created out of the values found in the
     *     columns of the database. These are added one by one to allContacts and returned.
     * </p>
     *
     * @return - an observable list containing allContacts in the database.
     */
    public static ObservableList<Contact> getAllContacts() {
        String sqlStatement = "SELECT * FROM contacts";
        allContacts.clear();
        try {
            DBQuery.setStatement(conn);
            Statement statement = DBQuery.getStatement();
            statement.execute(sqlStatement);
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                Contact c = new Contact();
                c.setContactId(rs.getInt("Contact_ID"));
                c.setContactName(rs.getString("Contact_Name"));
                c.setEmail(rs.getString("Email"));
                allContacts.add(c);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allContacts;
    }

}
