package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import utility.DBManager;
import utility.DBQuery;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerDAO {

    public static Connection conn = DBManager.getConnection();

    public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    public static AtomicInteger customerIdGen = new AtomicInteger();

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
                c.setPostalCode(rs.getString("Postal_Code"));
                c.setPhone(rs.getString("Phone"));
                c.setCreateDate(rs.getTimestamp("Create_Date").toLocalDateTime());
                c.setCreatedBy(rs.getString("Created_By"));
                c.setLastUpdate(rs.getTimestamp("Last_Update").toLocalDateTime());
                c.setLastUpdatedBy(rs.getString("Last_Updated_By"));
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

    public static Customer getCustomer(int id) {
        ObservableList<Customer> all = getAllCustomers();
        for (Customer c : all) {
            if (id == c.getCustomerId()) {
                return c;
            }
        }
        return null;
    }

    public static Customer getCustomerByName(String s) {
        ObservableList<Customer> customers = getAllCustomers();
        for (Customer c : customers) {
            if (c.getName().equals(s)) {
                return c;
            }
        }
        return null;
    }

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
