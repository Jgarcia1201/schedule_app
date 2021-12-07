package controller;

import DAO.AppointmentDAO;
import DAO.UserDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appointment;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

public class Login implements Initializable {
    public Label loginTitle;
    public Label loginSubtitle;
    public Label loginUsernameLabel;
    public Label loginPasswordLabel;
    public TextField userNameInput;
    public TextField passwordInput;
    public Button loginButton;
    public Label languageLabel;
    private Stage stage;
    private Scene scene;

    public static User you = new User();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting Language:
        ResourceBundle rb = ResourceBundle.getBundle("language/language", Locale.getDefault());
        loginTitle.setText(rb.getString("login.title"));
        loginSubtitle.setText(rb.getString("login.subtitle"));
        loginUsernameLabel.setText(rb.getString("login.username"));
        loginPasswordLabel.setText(rb.getString("login.password"));
        loginButton.setText(rb.getString("login.button"));
        languageLabel.setText(rb.getString("login.language"));
    }


    public void onLogin(ActionEvent event) {
        try {
            if (checkLogin(you)) {
                // Set Current User
                you = UserDAO.getUserByName(userNameInput.getText());
                System.out.println(you.getUserName());
                // Show Schedule Page
                checkUpcomingApps();
                showMainMenu(event);
            }
            else if (userNameInput.getText().equals("") || passwordInput.getText().equals("")) {
                showAlert("empty");
            }
            else {
                showAlert("invalid password");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkLogin(User user) {
        try {
            ObservableList<User> allUsers = UserDAO.getAllUsers();
            String userName = userNameInput.getText();
            String password = passwordInput.getText();

            user.setUserName(userName);
            user.setPassword(password);

            for (User u : allUsers) {
                if (user.getUserName().equals(u.getUserName()) && user.getPassword().equals(u.getPassword())) {
                    return true;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Incorrect");
        }
        return false;
    }

    // TODO: SET LANGUAGE!!!
    private void showAlert(String a) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (a.equals("invalid password")) {
            alert.setTitle("INVALID LOGIN CREDENTIALS");
            alert.setHeaderText("You Have Entered an Invalid Username or Password!");
            alert.setContentText("Please Try Again");
        }
        else if (a.equals("empty")) {
            alert.setTitle("INVALID INPUT");
            alert.setHeaderText("You Must Enter Values Into BOTH Fields!");
            alert.setContentText("Please Try Again");
        }
        alert.showAndWait();
        userNameInput.setText("");
        passwordInput.setText("");
    }

    public void showMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void checkUpcomingApps() {
        ObservableList<Appointment> apps = AppointmentDAO.getAllApps();
        ObservableList<Appointment> upcoming = FXCollections.observableArrayList();
        ZoneId utc = ZoneId.of("UTC");
        for (Appointment a : apps) {
            if (a.getStart().isBefore(LocalDateTime.now(utc).plusMinutes(15)) && a.getStart().isAfter(LocalDateTime.now(utc))) {
                upcoming.add(a);
                System.out.println(a.getTitle());
            }
        }
        if (upcoming.size() > 0) {
            String message = "Upcoming Appointments:";
            for (Appointment a : upcoming) {
                if (upcoming.indexOf(a) + 1 == upcoming.size()) {
                    message = message + " " + a.getTitle() + ".";
                }
                else {
                    message = message + " " + a.getTitle() + ",";
                }
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("UPCOMING APPOINTMENT!");
            alert.setHeaderText("You Have " + upcoming.size() + " Upcoming Appointments Within 15 Minutes!");
            alert.setContentText(message);
            alert.show();
        }
    }



}
