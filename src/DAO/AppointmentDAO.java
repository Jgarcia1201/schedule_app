package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Appointment;
import model.User;
import utility.DBManager;
import utility.DBQuery;

import java.sql.*;
import java.time.*;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class AppointmentDAO {

    private static Connection conn = DBManager.getConnection();

    private static ObservableList<Appointment> allApps = FXCollections.observableArrayList();
    public static AtomicInteger appIdGen = new AtomicInteger(5);

    public AppointmentDAO() {} // Constructor.

    public static Appointment getAppointment(int id) {
        try {
            ObservableList<Appointment> all = allApps;
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

    public static String insertAppointment(Appointment app) {
        String insertStatement = "INSERT INTO appointments(Appointment_ID, Title, Description, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            DBQuery.setPreparedStatement(conn, insertStatement);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            // Time Zone Conversion:
            LocalDateTime startTime = app.getStart();
            LocalDate startTimeDate = LocalDate.from(startTime);

            LocalDateTime endTime = app.getEnd();

            LocalTime openTime = LocalTime.of(13, 00);
            LocalTime closeTime = LocalTime.of(3,00);

            LocalDateTime open = LocalDateTime.of(startTimeDate, openTime);
            LocalDateTime close = LocalDateTime.of(startTimeDate.plusDays(1), closeTime);

            LocalDateTime startUtc = handleTimeConversion(startTime, "start");
            LocalDateTime endUtc = handleTimeConversion(endTime, "end");

            // Checking Start Time Is Before End Time
            if (startUtc.isAfter(endUtc)) {
                return "StartBeforeEnd";

            }
            // Checking Business Hours
            else if (startUtc.isBefore(open) || startUtc.isAfter(close)) {
                return "BusinessHours";
            }
            // Checking Overlap
            for (Appointment a : allApps) {
                if (a.getStart().isEqual(startUtc) || a.getEnd().isEqual(endUtc)) {
                    return "Overlap";
                }
            }
            // Key Value Pairing
            ps.setInt(1, app.getAppointmentId());
            ps.setString(2, app.getTitle());
            ps.setString(3, app.getDescription());
            ps.setString(4, app.getType());
            ps.setTimestamp(5, Timestamp.valueOf(startUtc));
            ps.setTimestamp(6, Timestamp.valueOf(endUtc));
            ps.setTimestamp(7, Timestamp.valueOf(app.getCreateDate()));
            ps.setString(8, app.getCreatedBy());
            ps.setTimestamp(9, Timestamp.valueOf(app.getLastUpdate()));
            ps.setString(10, app.getLastUpdatedBy());
            ps.setInt(11, app.getCustomerId());
            ps.setInt(12, app.getUserId());
            ps.setInt(13, app.getCustomerId());
            ps.execute();
            allApps.add(app);
            return "Success";
        }
        catch (Exception e) {

        }
        return "Fail";
    }

    public static ObservableList<Appointment> getAllApps() {
        String sqlStatement = "SELECT * FROM appointments";
        allApps.clear();
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

    public static LocalDateTime handleTimeConversion(LocalDateTime time, String c) {
        try {
            String choice = c;
            ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID()); // Getting Local Timezone
            ZoneId uctZoneId = ZoneId.of("UCT");

            switch (c) {
                case "start":
                    ZonedDateTime startZdt = ZonedDateTime.of(time, localZoneId);
                    LocalDateTime startUct = startZdt.withZoneSameInstant(uctZoneId).toLocalDateTime();
                    return startUct;
                case "end":
                    ZonedDateTime endZdt = ZonedDateTime.of(time, localZoneId);
                    LocalDateTime endUct = endZdt.withZoneSameInstant(uctZoneId).toLocalDateTime();
                    return endUct;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
