package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contact;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ContactDAO {

    private static Connection conn = DBManager.getConnection();

    private static ObservableList<Contact> allContacts = FXCollections.observableArrayList();

    public static Contact getContactByName(String s) {
        ObservableList<Contact> temp = getAllContacts();
        for (Contact c : temp) {
            if (s.equals(c.getContactName())) {
                return c;
            }
        }
        return null;
    }

    public static Contact getContactById(int id) {
        ObservableList<Contact> temp = getAllContacts();
        for (Contact c : temp) {
            if (c.getContactId() == id) {
                return c;
            }
        }
        return null;
    }

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
