package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Customer;
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

    /**
     * An Observable List is created to return.
     * <p>
     *     Every appointment in the database is checked. When matching IDs are found between the current Appointment and
     *     parameter, the Appointment is added to the observable list.
     * </p>
     *
     * @param c - User object.
     * @return - An observable list containing all Appointments associated with specified user.
     */
    public static ObservableList<Appointment> getCustomerAppointments(Customer c) {
        ObservableList<Appointment> customerApps = FXCollections.observableArrayList(); // O(1)
        allApps = getAllApps();
        for (Appointment app : allApps) {
            if (c.getCustomerId() == app.getCustomerId()) {
                customerApps.add(app);
            }
        }
        return customerApps;
    }

    /**
     * <p>
     *     The monthly List is cleared to ensure up to date data.
     *     Every Appointment in the database is checked using the isThisMonth() method and when a match is found it is
     *     added to the monthly List.
     * </p>
     * @return - Observable List containing Appointments within a month of the current moment.
     */
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
            e.printStackTrace();
        }
        return monthly;
    }


    /**
     * <p>
     *     The weekly List is cleared to ensure up to date data.
     *     Every Appointment in the database is checked using the isThisWeek() method and when a match is found it is
     *     added to the weekly List.
     * </p>
     * @return - Observable List containing Appointments within a week of the current moment.
     */
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
            e.printStackTrace();
        }
        return weekly;
    }

    /**
     * <p>
     *     A SQL statment is stored as a String to use in a prepared statement which is initialized immediately afterwards.
     * </p>
     * <p>
     *     Time Zone Conversion is then handled by extracting the start date of the Appointment, which is converted to UTC for
     *     storage and all comparisons throughout the application.
     * </p>
     * <p>
     *     Opening & Closing times in UTC are created here and then checked against the Appointment's start and end times.
     *      if the Appointment is out of bounds "BusinessHours" is returned.
     * </p>
     * <p>
     *     Every appointment's start and end time is then checked against the parameter's start and end time to ensure that
     *     no overlap exists in the schedule. A logical check is also performed to ensure that the start time is before the end time.
     * </p>
     * <p>
     *     If there are no errors thrown at this point the prepared statement is prepared using key pair values and an INSERT statement is attempted.
     *     If the insert was successful "success" is returned. Otherwise, an exception is thrown.
     * </p>
     * @param app - Appointment to be inserted.
     * @return String value indicating a successful insert or why it was unsuccessful.
     */
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
            allApps = getAllApps();
            for (Appointment a : allApps) {
                if (startUtc.isBefore(a.getEnd()) && endUtc.isAfter(a.getStart())) {
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
            e.printStackTrace();
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

    /**
     * A SQL statement is assigned to a String variable and the allApps observable list is cleared to ensure correct data.
     * <p>
     *     The statement is executed and while rs.next() is true, a new Appointment is created out of the values found in the
     *     columns of the database. These are added one by one to allApps and returned.
     * </p>
     *
     * @return - an observable list containing all appointments in the database.
     */
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
                a.setDisplayStart(rs.getTimestamp("Start").toLocalDateTime());
                a.setEnd(rs.getTimestamp("End").toLocalDateTime());
                a.setDisplayEnd(rs.getTimestamp("End").toLocalDateTime());
                a.setCreateDate(rs.getTimestamp("Create_Date").toLocalDateTime());
                a.setCreatedBy(rs.getString("Created_By"));
                a.setLastUpdate(rs.getTimestamp("Last_Update").toLocalDateTime());
                a.setDisplayLastUpdate(rs.getTimestamp("Last_Update").toLocalDateTime());
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

    /**
     * A ZoneId of UTC is specified and depending on the second parameter is used to convert the start or end time to
     * UTC for use in the Database and across the application.
     *
     * @param time - time to be converted.
     * @param c - specified if start time or end time.
     * @return LocalDateTime in UTC.
     */
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

    /**
     * <p>
     *     A SQL statement is stored as a String to use in a prepared statement which is initialized immediately afterwards.
     * </p>
     * <p>
     *     Time Zone Conversion is then handled by extracting the start date of the Appointment, which is converted to UTC for
     *     storage and all comparisons throughout the application.
     * </p>
     * <p>
     *     Opening & Closing times in UTC are created here and then checked against the Appointment's start and end times.
     *      if the Appointment is out of bounds "BusinessHours" is returned.
     * </p>
     * <p>
     *     Every appointment's start and end time is then checked against the parameter's start and end time to ensure that
     *     no overlap exists in the schedule. A logical check is also performed to ensure that the start time is before the end time.
     * </p>
     * <p>
     *     If there are no errors thrown at this point the prepared statement is prepared using key pair values and an UPDATE statement is attempted.
     *     If the insert was successful "success" is returned. Otherwise, an exception is thrown and "fail" is returned.
     * </p>
     * @param app - Appointment to be updated.
     * @return String value indicating a successful update or type of error.
     */
    public static String updateApp(Appointment app) {
        String sqlUpdate = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, " +
                "End = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";
        try {
            DBQuery.setPreparedStatement(conn, sqlUpdate);
            PreparedStatement ps = DBQuery.getPreparedStatement();

            // Time Conversion
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
            allApps = getAllApps();
            for (Appointment a : allApps) {
                if (startUtc.isBefore(a.getEnd()) && endUtc.isAfter(a.getStart())) {
                    if (a.getAppointmentId() != app.getAppointmentId()) {
                        return "Overlap";
                    }
                }
            }

            // Key Value Pairing
            ps.setString(1, app.getTitle());
            ps.setString(2, app.getDescription());
            ps.setString(3, app.getLocation());
            ps.setString(4, app.getType());
            ps.setTimestamp(5, Timestamp.valueOf(startUtc));
            ps.setTimestamp(6, Timestamp.valueOf(endUtc));
            ps.setTimestamp(7, Timestamp.valueOf(app.getCreateDate()));
            ps.setString(8, app.getCreatedBy());
            ps.setTimestamp(9, Timestamp.valueOf(app.getLastUpdate()));
            ps.setString(10, app.getLastUpdatedBy());
            ps.setInt(11, app.getCustomerId());
            ps.setInt(12, app.getUserId());
            ps.setInt(13, app.getContactId());
            ps.setInt(14, app.getAppointmentId());
            ps.execute();
            allApps = getAllApps();
            return "Success";

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return "Fail";
        }
    }

    /**
     * Takes a LocalDateTime and checks if it is seven days from the current moment. If so, returns true otherwise the
     * method will return false.
     * @param time - time to be checked.
     * @return - Boolean value indicating whether an Appointment's start time is within a week.
     */
    private static boolean isThisWeek(LocalDateTime time) {
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime now = LocalDateTime.now(utc);
        LocalDateTime weekFromNow = LocalDateTime.now(utc).plusDays(7);
        if (time.isAfter(now) && time.isBefore(weekFromNow)) {
            return true;
        }
        else return false;
    }

    /**
     * Takes a LocalDateTime and checks if it is a month from the current moment. If so, returns true otherwise the
     * method will return false.
     * @param time - time to be checked.
     * @return - Boolean value indicating whether an Appointment's start time is within a month.
     */
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
