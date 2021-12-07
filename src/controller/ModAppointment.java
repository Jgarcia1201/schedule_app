package controller;

import DAO.AppointmentDAO;
import DAO.ContactDAO;
import DAO.CustomerDAO;
import DAO.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class ModAppointment implements Initializable {
    public TextField modAppAppID;
    public ComboBox<String> modAppCustomer;
    public ComboBox<String> modAppUser;
    public TextField modAppTitle;
    public ComboBox<String> modAppContact;
    public TextField modAppType;
    public TextField modAppLocation;
    public TextField modAppDesc;
    public Button modAppSaveButton;
    public Button modAppExitButton;
    public DatePicker modAppDateBox;
    public ComboBox<String> modAppStartTime;
    public ComboBox<String> modAppEndTime;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private ObservableList<String> times = FXCollections.observableArrayList();
    Stage stage;
    Scene scene;

    Appointment currentApp = new Appointment();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Time Combo Boxes
        LocalTime hours = LocalTime.MIN.plusHours(0);
        for (int i = 0; i <= 48; i++) {
            times.add(hours.format(formatter));
            hours = hours.plusMinutes(30);
        }
        modAppStartTime.setItems(times);
        modAppEndTime.setItems(times);
        // Contact Combo Box
        ObservableList<Contact> contacts = ContactDAO.getAllContacts();
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        for (Contact c : contacts) {
            String name = c.getContactName();
            contactNames.add(name);
        }
        modAppContact.setItems(contactNames);

        // User Choice Box
        ObservableList<User> allUsers = UserDAO.getAllUsers();
        ObservableList<String> allUserNames = FXCollections.observableArrayList();
        for (User u: allUsers) {
            allUserNames.add(u.getUserName());
        }
        modAppUser.setItems(allUserNames);

        // Customer Choice Box
        ObservableList<Customer> allCustomer = CustomerDAO.getAllCustomers();
        ObservableList<String> customerNames = FXCollections.observableArrayList();
        for (Customer c : allCustomer) {
            customerNames.add(c.getName());
        }
        modAppCustomer.setItems(customerNames);
    }

    public void passAppointment(Appointment currentApp) {
        this.currentApp = currentApp;
        // IDs
        modAppAppID.setText(String.valueOf(currentApp.getAppointmentId()));
        Customer chosenCustomer = CustomerDAO.getCustomerById(currentApp.getCustomerId());
        modAppCustomer.setValue(String.valueOf(chosenCustomer.getName()));
        User chosenUser = UserDAO.getUserById(currentApp.getUserId());
        modAppUser.setValue(String.valueOf(chosenUser.getUserName()));
        // Appointment Info
        modAppTitle.setText(currentApp.getTitle());
        modAppDesc.setText(currentApp.getDescription());
        modAppLocation.setText(currentApp.getLocation());
        modAppType.setText(currentApp.getType());
        modAppContact.setValue(currentApp.getContact());
        // Times
        String timePattern = "hh:mm a";
        ZoneId localId = ZoneId.of(TimeZone.getDefault().getID());
        ZoneId utcId = ZoneId.of("UTC");
        LocalDateTime startDateTime = currentApp.getStart();
        ZonedDateTime startTimeUtc = ZonedDateTime.of(startDateTime, utcId);
        LocalDateTime startDateTimeLocal = startTimeUtc.withZoneSameInstant(localId).toLocalDateTime();
        LocalTime convertedStartTime = LocalTime.from(startDateTimeLocal);
        String displayStartTime = convertedStartTime.format(DateTimeFormatter.ofPattern(timePattern));
        modAppStartTime.setValue(displayStartTime);
        LocalDateTime endDateTime = currentApp.getEnd();
        ZonedDateTime endTimeUtc = ZonedDateTime.of(endDateTime, utcId);
        LocalDateTime endDateTimeLocal = endTimeUtc.withZoneSameInstant(localId).toLocalDateTime();
        LocalTime convertedEndTime = LocalTime.from(endDateTimeLocal);
        String displayEndTime = convertedEndTime.format(DateTimeFormatter.ofPattern(timePattern));
        modAppEndTime.setValue(displayEndTime);
        // Date
        modAppDateBox.setValue(LocalDate.from(startDateTimeLocal));
    }

    public void onModAppExitButtonAction(ActionEvent event) throws IOException {
        showMainMenu(event);
    }

    public void onModAppSaveButtonAction(ActionEvent event) throws IOException {
        try {
            int appointmentId = Integer.parseInt(modAppAppID.getText());
            String title = modAppTitle.getText();
            String description = modAppDesc.getText();
            String location = modAppLocation.getText();
            String type = modAppType.getText();
            LocalDate date = modAppDateBox.getValue();
            String start = modAppStartTime.getValue();
            String end = modAppEndTime.getValue();
            LocalDateTime lastUpdate = LocalDateTime.now();
            String lastUpdatedBy = Login.you.getUserName();
            String userChoice = modAppUser.getSelectionModel().getSelectedItem();
            User selectedUser = UserDAO.getUserByName(userChoice);
            String customerChoice = modAppCustomer.getSelectionModel().getSelectedItem();
            Customer selectedCustomer = CustomerDAO.getCustomerByName(customerChoice);
            int CustomerId = selectedCustomer.getCustomerId();
            int userId = selectedUser.getUserId();

            // Contact Box
            String contactChoiceName = modAppContact.getSelectionModel().getSelectedItem();
            Contact contactChoice = ContactDAO.getContactByName(contactChoiceName);
            int contactId = contactChoice.getContactId();
            String contact = contactChoice.getContactName();

            // Checking For Blanks.
            if (title.equals("") || description.equals("") || location.equals("") || type.equals("") || contactChoiceName.equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID INPUTS");
                alert.setHeaderText("Please Enter Values For All Text Fields");
                alert.setContentText("Please Try Again");
                alert.showAndWait();
                modAppTitle.clear();
                modAppDesc.clear();
                modAppLocation.clear();
                modAppType.clear();
            }
            // Time
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.parse(start, formatter));
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.parse(end, formatter));

            // Creating New Appointment
            currentApp.setAppointmentId(appointmentId);
            currentApp.setTitle(title);
            currentApp.setDescription(description);
            currentApp.setLocation(location);
            currentApp.setType(type);
            currentApp.setStart(startTime);
            currentApp.setEnd(endTime);
            currentApp.setLastUpdate(lastUpdate);
            currentApp.setLastUpdatedBy(lastUpdatedBy);
            currentApp.setCustomerId(CustomerId);
            currentApp.setUserId(userId);
            currentApp.setContact(contact);
            currentApp.setContactId(contactId);
            // Checking Time is In Future
            if (date.isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID TIME");
                alert.setHeaderText("New Appointments Must Be Scheduled In The Future");
                alert.setContentText("Please Select a Valid Time");
                alert.showAndWait();
                modAppStartTime.getSelectionModel().clearSelection();
                modAppEndTime.getSelectionModel().clearSelection();
            }

            // Attempting DB Update.
            String result = AppointmentDAO.updateApp(currentApp);
            if (result == "Success") {
                showMainMenu(event);
            }
            else if (result == "StartBeforeEnd") {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID TIMES");
                alert.setHeaderText("Start Time MUST Be Before End Time");
                alert.setContentText("Please Enter Valid Start And End Times");
                alert.showAndWait();
                modAppStartTime.getSelectionModel().clearSelection();
                modAppEndTime.getSelectionModel().clearSelection();
            }
            else if (result == "BusinessHours") {
                String timePattern = "hh:mm a";
                ZoneId utc = ZoneId.of("UTC");
                ZoneId local = ZoneId.of(TimeZone.getDefault().getID());
                ZonedDateTime openUtc = ZonedDateTime.of(LocalDateTime.of((LocalDate.now()), LocalTime.of(13, 00)), utc);
                LocalTime openLocal = openUtc.withZoneSameInstant(local).toLocalTime();
                String openLocalDisplayTime = openLocal.format(DateTimeFormatter.ofPattern(timePattern));
                ZonedDateTime closeUtc = ZonedDateTime.of(LocalDateTime.of((LocalDate.now().plusDays(1)), LocalTime.of(3, 00)), utc);
                LocalTime closeLocal = closeUtc.withZoneSameInstant(local).toLocalTime();
                String closeLocalDisplayTime = closeLocal.format(DateTimeFormatter.ofPattern(timePattern));
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID TIMES");
                alert.setHeaderText("Appointment Must Be Scheduled Within Business Hours");
                alert.setContentText("Business Hours are: " + openLocalDisplayTime + " - " + closeLocalDisplayTime);
                alert.showAndWait();
                modAppStartTime.getSelectionModel().clearSelection();
                modAppEndTime.getSelectionModel().clearSelection();
            }
            else if (result == "Overlap") {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID TIMES");
                alert.setHeaderText("Appointment Already Exists at Specified Time");
                alert.setContentText("Please Select a New Time");
                alert.showAndWait();
            }
            else {
                throw new Exception();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void showMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
