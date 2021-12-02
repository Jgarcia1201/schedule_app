package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AppointmentDAO {

    private static Connection conn = DBManager.getConnection();

    public static ObservableList<Appointment> allApps = FXCollections.observableArrayList();

    public AppointmentDAO() {} // Constructor.

    public static Appointment getAppointment(int id) {
        try {
            ObservableList<Appointment> all = getAllApps();
            for (Appointment app : all) {
                if (app.getAppointmentId() == id) {
                    return app;
                }
                else {
                    throw new Exception();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Appointment> getAllApps() {
        String sqlStatement = "SELECT * FROM appointments";
        try {
            DBQuery.setStatement(conn);
            Statement statement = DBQuery.getStatement();
            statement.execute(sqlStatement);
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                Appointment a = new Appointment();
                a.setAppointmentId(rs.getInt("Appointment_ID"));
                a.setTitle(rs.getString("Title"));
                a.setDescription(rs.getString("Description"));
                a.setLocation(rs.getString("Location"));
                a.setContactId(rs.getInt("Contact_ID"));
                a.setType(rs.getString("Type"));
                a.setStart(rs.getTimestamp("Start").toLocalDateTime());
                a.setEnd(rs.getTimestamp("End").toLocalDateTime());
                a.setCreateDate(rs.getTimestamp("Create_Date").toLocalDateTime());
                a.setCreatedBy(rs.getString("Created_By"));
                a.setLastUpdate(rs.getTimestamp("Last_Update").toLocalDateTime());
                a.setLastUpdatedBy(rs.getString("Last_Updated_By"));
                a.setCustomerId(rs.getInt("Customer_ID"));
                a.setUserId(rs.getInt("User_ID"));
                allApps.add(a);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allApps;
    }
}
