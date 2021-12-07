package controller;

import DAO.AppointmentDAO;
import DAO.ContactDAO;
import DAO.CustomerDAO;
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

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class AddAppointment implements Initializable {
    public TextField addAppAppID;
    public TextField addAppCustomerId;
    public TextField addAppUserId;
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
    private DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private ObservableList<String> times = FXCollections.observableArrayList();

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
            String name = c.getContactName();
            contactNames.add(name);
        }
        addAppContact.setItems(contactNames);
    }

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
            String createdBy = Login.temp.getCurrentUser();
            LocalDateTime lastUpdate = LocalDateTime.now();
            String lastUpdatedBy = Login.temp.getCurrentUser();
            int CustomerId = 1;
            int userId = Login.temp.getUserId();

            // Contact Box
            String contactChoiceName = addAppContact.getSelectionModel().getSelectedItem();
            Contact contactChoice = ContactDAO.getContactByName(contactChoiceName);
            int contactId = contactChoice.getContactId();
            String contact = contactChoice.getContactName();

            // Checking For Blanks.
            if (title.equals("") || description.equals("") || location.equals("") || type.equals(""))    {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID INPUTS");
                alert.setHeaderText("Please Enter Values For All Text Fields");
                alert.setContentText("Please Try Again");
                alert.showAndWait();
                addAppTitle.clear();
                addAppDesc.clear();
                addAppLocation.clear();
                addAppType.clear();
            }
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID TIMES");
                alert.setHeaderText("Appointment Must Be Scheduled Within Business Hours");
                alert.setContentText("Business Hours are 8AM to 10PM EST");
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
        }
    }

    public void onAddAppExitButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void showMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
