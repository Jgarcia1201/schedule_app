package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomerDAO {

    public static Connection conn = DBManager.getConnection();

    public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    public CustomerDAO() {} // Constructor

    public static ObservableList<Customer> getAllCustomers() {
        String sql = "SELECT * FROM customers";
        allCustomers.clear();
        try {
            DBQuery.setStatement(conn);
            Statement statement = DBQuery.getStatement();
            statement.execute(sql);
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                Customer c = new Customer();
                c.setCustomerId(rs.getInt("Customer_ID"));
                c.setName(rs.getString("Customer_Name"));
                c.setAddress(rs.getString("Address"));
                c.setPostalCode(rs.getString("Postal_Code"));
                c.setPhone(rs.getString("Phone"));
                c.setCreateDate(rs.getTimestamp("Create_Date").toLocalDateTime());
                c.setCreatedBy(rs.getString("Created_By"));
                c.setLastUpdate(rs.getTimestamp("Last_Update"));
                c.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                c.setDivisionId(rs.getInt("Division_ID"));
                allCustomers.add(c);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allCustomers;
    }

    public static Customer getCustomer(int id) {
        ObservableList<Customer> all = getAllCustomers();
        for (Customer c : all) {
            if (id == c.getCustomerId()) {
                return c;
            }
        }
        return null;
    }

}
