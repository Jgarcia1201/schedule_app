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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class AddAppointment implements Initializable {
    public TextField addAppAppID;
    public ComboBox<String> addAppCustomer;
    public ComboBox<String> addAppUser;
    public TextField addAppTitle;
    public TextField addAppDesc;
    public TextField addAppLocation;
    public ComboBox<String> addAppContact;
    public TextField addAppType;
    public DatePicker addAppDateBox;
    public ComboBox<String> addAppStartTime;
    public ComboBox<String> addAppEndTime;
    public Button addAppSaveButton;
    public Button addAppExitButton;
    public Stage stage;
    public Scene scene;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final ObservableList<String> times = FXCollections.observableArrayList();

    /**
     * When initializing the AddAppointment Controller, the ComboBoxes used to select appointment contact,
     * user, and customer are populated using Observable List. An Atomic Integer serves as an
     * ID Generator for appointments and is initialized here.
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Time Choice Boxes
        LocalTime hours = LocalTime.MIN.plusHours(0);
        for (int i = 0; i <= 48; i++) {
            times.add(hours.format(formatter));
            hours = hours.plusMinutes(30);
        }
        addAppStartTime.setItems(times);
        addAppEndTime.setItems(times);

        // Appointment ID Generation
        AppointmentDAO.appIdGen.set(AppointmentDAO.getAllApps().size() + 1); // Setting ID Generator to Size of AllApps + 1.
        int appointmentId = AppointmentDAO.appIdGen.get();
        addAppAppID.setText(String.valueOf(appointmentId));

        // Contact Choice Box
        ObservableList<Contact> contacts = ContactDAO.getAllContacts();
        ObservableList<String> contactNames = FXCollections.observableArrayList();
        for (Contact c : contacts) {
            contactNames.add(c.getContactName());
        }
        addAppContact.setItems(contactNames);

        // User Choice Box
        ObservableList<User> allUsers = UserDAO.getAllUsers();
        ObservableList<String> allUserNames = FXCollections.observableArrayList();
        for (User u: allUsers) {
            allUserNames.add(u.getUserName());
        }
        addAppUser.setItems(allUserNames);

        // Customer Choice Box
        ObservableList<Customer> allCustomer = CustomerDAO.getAllCustomers();
        ObservableList<String> customerNames = FXCollections.observableArrayList();
        for (Customer c : allCustomer) {
            customerNames.add(c.getName());
        }
        addAppCustomer.setItems(customerNames);
    }

    /**
     * <p>
     *     Triggered by an action performed on the button reading "Save".
     *      Variables are created and used to store the values of the controller's text fields and combo boxes.
     * </p>
     * <p>
     *     Validation is performed on the variables ensuring valid input from user.
     *     An Appointment class is created from the user's inputs and is inserted using the AppointmentDAO method:
     *     insertAppointment()
     * </p>
     * <p>
     *     insertAppointment() returns a String and that result is then used to decide whether to return to the main schedule
     *     or show an error depending on the return value.
     * </p>
     *
     *
     * @param event - click on Save Button.
     */
    public void onAddAppSaveButtonAction(ActionEvent event) {
        try {
            int appointmentId = AppointmentDAO.appIdGen.getAndIncrement();
            String title = addAppTitle.getText();
            String description = addAppDesc.getText();
            String location = addAppLocation.getText();
            String type = addAppType.getText();
            LocalDate date = addAppDateBox.getValue();
            String start = addAppStartTime.getValue();
            String end = addAppEndTime.getValue();
            LocalDateTime createDate = LocalDateTime.now();
            String createdBy = Login.you.getUserName();
            LocalDateTime lastUpdate = LocalDateTime.now();
            String lastUpdatedBy = Login.you.getUserName();
            String userChoice = addAppUser.getValue();
            String customerChoice = addAppCustomer.getValue();

            // Checking For Blanks.
            if (title.equals("") || description.equals("") || location.equals("") || type.equals("") || userChoice == null || customerChoice == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID INPUTS");
                alert.setHeaderText("Please Enter Values For All Text Fields");
                alert.setContentText("Please Try Again");
                alert.showAndWait();
                return;
            }
            User selectedUser = UserDAO.getUserByName(userChoice);
            Customer selectedCustomer = CustomerDAO.getCustomerByName(customerChoice);
            int CustomerId = selectedCustomer.getCustomerId();
            int userId = selectedUser.getUserId();

            // Contact Box
            String contactChoiceName = addAppContact.getSelectionModel().getSelectedItem();
            Contact contactChoice = ContactDAO.getContactByName(contactChoiceName);
            int contactId = contactChoice.getContactId();
            String contact = contactChoice.getContactName();

            // Time
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.parse(start, formatter));
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.parse(end, formatter));

            // Creating New Appointment
            Appointment app = new Appointment();
            app.setAppointmentId(appointmentId);
            app.setTitle(title);
            app.setDescription(description);
            app.setLocation(location);
            app.setType(type);
            app.setStart(startTime);
            app.setEnd(endTime);
            app.setCreateDate(createDate);
            app.setCreatedBy(createdBy);
            app.setLastUpdate(lastUpdate);
            app.setLastUpdatedBy(lastUpdatedBy);
            app.setCustomerId(CustomerId);
            app.setUserId(userId);
            app.setContact(contact);
            app.setContactId(contactId);

            // Checking Time is In Future
            if (date.isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID TIME");
                alert.setHeaderText("New Appointments Must Be Scheduled In The Future");
                alert.setContentText("Please Select a Valid Time");
                alert.showAndWait();
                addAppStartTime.getSelectionModel().clearSelection();
                addAppEndTime.getSelectionModel().clearSelection();
            }

            // Attempting DB Insert.
            String result = AppointmentDAO.insertAppointment(app);
            if (result == "Success") {
                showMainMenu(event);
            }
            else if (result == "StartBeforeEnd") {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID TIMES");
                alert.setHeaderText("Start Time MUST Be Before End Time");
                alert.setContentText("Please Enter Valid Start And End Times");
                alert.showAndWait();
                addAppStartTime.getSelectionModel().clearSelection();
                addAppEndTime.getSelectionModel().clearSelection();
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
                alert.setContentText("Business Hours are Between: " + openLocalDisplayTime + " - " + closeLocalDisplayTime);
                alert.showAndWait();
                addAppStartTime.getSelectionModel().clearSelection();
                addAppEndTime.getSelectionModel().clearSelection();
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
            e.getMessage();
        }
    }

    /**
     * calls showMainMenu() and returns to Main Menu when Exit button is clicked.
     * @param event - click on Exit Button.
     * @throws IOException
     */
    public void onAddAppExitButtonAction(ActionEvent event) throws IOException {
        showMainMenu(event);
    }

    public void showMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
