package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;
import java.util.TimeZone;

public class ModAppointment implements Initializable {
    public TextField modAppAppID;
    public TextField modAppCustomerId;
    public TextField modAppUserId;
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
    Stage stage;
    Scene scene;

    Appointment currentApp = new Appointment();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void passAppointment(Appointment currentApp) {
        this.currentApp = currentApp;
        // IDs
        modAppAppID.setText(String.valueOf(currentApp.getAppointmentId()));
        modAppCustomerId.setText(String.valueOf(currentApp.getCustomerId()));
        modAppUserId.setText(String.valueOf(currentApp.getUserId()));
        // Appointment Info
        modAppTitle.setText(currentApp.getTitle());
        modAppDesc.setText(currentApp.getDescription());
        modAppLocation.setText(currentApp.getLocation());
        modAppType.setText(currentApp.getType());
        modAppContact.setValue(currentApp.getContact());
        // Times
        ZoneId localId = ZoneId.of(TimeZone.getDefault().getID());
        ZoneId utcId = ZoneId.of("UTC");
        LocalDateTime startDateTime = currentApp.getStart();
        ZonedDateTime startTimeUtc = ZonedDateTime.of(startDateTime, utcId);
        LocalDateTime startDateTimeLocal = startTimeUtc.withZoneSameInstant(localId).toLocalDateTime();
        LocalTime convertedStartTime = LocalTime.from(startDateTimeLocal);
        modAppStartTime.setValue(convertedStartTime.toString());
        LocalDateTime endDateTime = currentApp.getEnd();
        ZonedDateTime endTimeUtc = ZonedDateTime.of(endDateTime, utcId);
        LocalDateTime endDateTimeLocal = endTimeUtc.withZoneSameInstant(localId).toLocalDateTime();
        LocalTime convertedEndTime = LocalTime.from(endDateTimeLocal);
        modAppEndTime.setValue(convertedEndTime.toString());
        // Date
        modAppDateBox.setValue(LocalDate.from(startDateTimeLocal));
    }

    public void onModAppExitButtonAction(ActionEvent event) throws IOException {
        showMainMenu(event);
    }

    public void onModAppSaveButtonAction(ActionEvent event) throws IOException {
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
