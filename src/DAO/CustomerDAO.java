package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import utility.DBManager;
import utility.DBQuery;

import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerDAO {

    public static Connection conn = DBManager.getConnection();

    public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    public static AtomicInteger customerIdGen = new AtomicInteger();

    public CustomerDAO() {} // Constructor

    /**
     * A SQL statement is assigned to a String variable and the allCustomers observable list is cleared to ensure correct data.
     * <p>
     *     The statement is executed and while rs.next() is true, a new Customer is created out of the values found in the
     *     columns of the database. These are added one by one to allCustomers and returned.
     * </p>
     *
     * @return - an observable list containing all Customers in the database.
     */
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
                c.setPostalCode(rs.getString("Postal_Code"));
                c.setPhone(rs.getString("Phone"));
                c.setCreateDate(rs.getTimestamp("Create_Date").toLocalDateTime());
                c.setCreatedBy(rs.getString("Created_By"));
                c.setLastUpdate(rs.getTimestamp("Last_Update").toLocalDateTime());
                c.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                c.setDisplayLastUpdate(rs.getTimestamp("Last_Update").toLocalDateTime());
                c.setDivisionId(rs.getInt("Division_ID"));
                // Address
                c.setAddress(rs.getString("Address"));
                allCustomers.add(c);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allCustomers;
    }

    /**
     * <p>
     *     A SQL statment is stored as a String to use in a prepared statement which is initialized immediately afterwards.
     * </p>
     * <p>
     *     A prepared statement is prepared using key pair values and an INSERT statement is attempted.
     *     If the insert was successful "success" is returned. Otherwise, an exception is thrown and "Fail" is returned.
     * </p>
     * @param c - Customer to be inserted.
     * @return String value indicating a successful insert or why it was unsuccessful.
     */
    public static String insertCustomer(Customer c) {
        String insertSql = "INSERT INTO customers(Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date," +
                " Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            DBQuery.setPreparedStatement(conn, insertSql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setInt(1, c.getCustomerId());
            ps.setString(2, c.getName());
            ps.setString(3, c.getAddress());
            ps.setString(4, c.getPostalCode());
            ps.setString(5, c.getPhone());
            ps.setTimestamp(6, Timestamp.valueOf(c.getCreateDate()));
            ps.setString(7, c.getCreatedBy());
            ps.setTimestamp(8, Timestamp.valueOf(c.getLastUpdate()));
            ps.setString(9, c.getLastUpdatedBy());
            ps.setInt(10, c.getDivisionId());
            ps.execute();
            allCustomers.add(c);
            return "Success";

        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "Fail";
    }

    /**
     * <p>
     *     A SQL statement is stored as a String to use in a prepared statement which is initialized immediately afterwards.
     * </p>
     * <p>
     *     The prepared statement is prepared using key pair values and an UPDATE statement is attempted.
     *     If the insert was successful "success" is returned. Otherwise, an exception is thrown and "fail" is returned.
     * </p>
     * @param c - Customer to be updated.
     * @return String value indicating a successful update or type of error.
     */
    public static boolean updateCustomer(Customer c) {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?," +
                " Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        try {
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setString(1, c.getName());
            ps.setString(2, c.getAddress());
            ps.setString(3, c.getPostalCode());
            ps.setString(4, c.getPhone());
            ps.setTimestamp(5, Timestamp.valueOf(c.getLastUpdate()));
            ps.setString(6, c.getLastUpdatedBy());
            ps.setInt(7, c.getDivisionId());
            ps.setInt(8, c.getCustomerId());
            ps.execute();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * <p>
     *     A SQL statement is assigned to a String and a prepared statement is prepared using key value pairing.
     *     A DELETE statement is attempted. If the delete was unsuccessful, an exception is thrown.
     * </p>
     * @param c - Customer to be deleted from Database.
     */
    public static void deleteCustomer(Customer c) {
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        try {
            DBQuery.setPreparedStatement(conn, sql);
            PreparedStatement ps = DBQuery.getPreparedStatement();
            ps.setInt(1, c.getCustomerId());
            ps.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks every customer's name against string specified in parameter. If a match is found, the Customer is returned.
     * @param s - String, name of desired Customer.
     * @return desired Customer.
     */
    public static Customer getCustomerByName(String s) {
        ObservableList<Customer> customers = getAllCustomers();
        for (Customer c : customers) {
            if (c.getName().equals(s)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Checks every customer's ID against string specified in parameter. If a match is found, the Customer is returned.
     * @param i - int, ID of desired Customer.
     * @return desired Customer.
     */
    public static Customer getCustomerById(int i) {
        ObservableList<Customer> customer = getAllCustomers();
        for (Customer c : customer) {
            if (c.getCustomerId() == i) {
                return c;
            }
        }
        return null;
    }

}
