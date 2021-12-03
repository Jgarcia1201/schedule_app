package controller;

import DAO.AppointmentDAO;
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

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.TimeZone;

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
        int appointmentId = AppointmentDAO.appIdGen.get();
        addAppAppID.setText(String.valueOf(appointmentId));
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
            int contactId = 1;

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
            app.setContactId(contactId);
            // Adding to DB
            String result = AppointmentDAO.insertAppointment(app);
            if (result == "Success") {
                showMainMenu(event);
            }
            else if (result == "Fail") {
                System.out.println("stupid");
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
