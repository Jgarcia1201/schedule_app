package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddAppointment implements Initializable {
    public TextField addAppAppID;
    public TextField addAppCustomerId;
    public TextField addAppUserId;
    public TextField addAptTitle;
    public TextField addAptDesc;
    public TextField addAppLocation;
    public TextField addAppContact;
    public TextField addAppType;
    public ChoiceBox<String> addAppStartTime;
    public ChoiceBox<String> addAppEndTime;
    public Button addAppSaveButton;
    public Button addAppExitButton;
    public Stage stage;
    public Scene scene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Time Choice Boxes
        ObservableList<String> times = FXCollections.observableArrayList();
        times.addAll("8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM",
                "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM",
                "9:00 PM");
        addAppStartTime.setItems(times);
        addAppEndTime.setItems(times);

    }

    public void onAddAppExitButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
