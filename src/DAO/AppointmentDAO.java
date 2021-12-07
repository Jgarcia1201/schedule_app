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
    private static ObservableList<Appointment> weekly = FXCollections.observableArrayList();
    private static ObservableList<Appointment> monthly = FXCollections.observableArrayList();
    public static AtomicInteger appIdGen = new AtomicInteger();

    public AppointmentDAO() {} // Constructor.

    public static Appointment getAppointment(int id) {
        try {
            allApps = getAllApps();
            for (Appointment app : allApps) {
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

    public static ObservableList<Appointment> getMonthApps() {
        monthly.clear();
        ObservableList<Appointment> temp = getAllApps();
        try {
            for (Appointment app : temp) {
                if (isThisMonth(app.getStart())) {
                    monthly.add(app);
                }
            }
        }
        catch (Exception e) {
            // do nothing but fix later
        }
        return monthly;
    }

    public static ObservableList<Appointment> getWeekApps() {
        weekly.clear();
        ObservableList<Appointment> temp = getAllApps();
        try {
            for (Appointment app : temp) {
                if (isThisWeek(app.getStart())) {
                    weekly.add(app);
                }
            }
        }
        catch (Exception e) {
            // do nothing but fix later
        }
        return weekly;
    }

    public static String insertAppointment(Appointment app) {
        String insertStatement = "INSERT INTO appointments(Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setString(4, app.getLocation());
            ps.setString(5, app.getType());
            ps.setTimestamp(6, Timestamp.valueOf(startUtc));
            ps.setTimestamp(7, Timestamp.valueOf(endUtc));
            ps.setTimestamp(8, Timestamp.valueOf(app.getCreateDate()));
            ps.setString(9, app.getCreatedBy());
            ps.setTimestamp(10, Timestamp.valueOf(app.getLastUpdate()));
            ps.setString(11, app.getLastUpdatedBy());
            ps.setInt(12, app.getCustomerId());
            ps.setInt(13, app.getUserId());
            ps.setInt(14, app.getContactId());
            ps.execute();
            allApps.add(app);
            return "Success";
        }
        catch (Exception e) {

        }
        return "Fail";
    }

    public static void removeApp(Appointment app) {
        String deleteStatement = "DELETE FROM appointments WHERE Appointment_ID = ?";
        try {
            DBQuery.setPreparedStatement(conn, deleteStatement);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            ps.setInt(1, app.getAppointmentId());
            ps.execute();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
                a.setContact(String.valueOf(ContactDAO.getContactById(a.getContactId()).getContactName()));
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
            ZoneId uctZoneId = ZoneId.of("UTC");

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

    private static boolean isThisWeek(LocalDateTime time) {
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime now = LocalDateTime.now(utc);
        LocalDateTime weekFromNow = LocalDateTime.now(utc).plusDays(7);
        if (time.isAfter(now) && time.isBefore(weekFromNow)) {
            return true;
        }
        else return false;
    }

    private static boolean isThisMonth(LocalDateTime time) {
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime now = LocalDateTime.now(utc);
        LocalDateTime monthFromNow = LocalDateTime.now(utc).plusMonths(1);
        if (time.isAfter(now) && time.isBefore(monthFromNow)) {
            return true;
        }
        else return false;
    }
}
