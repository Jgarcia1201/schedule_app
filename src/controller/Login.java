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
import utility.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
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

    private String alertHeader;
    private String alertTitle;
    private String alertMessage;

    /**
     * A ResourceBundle sets the language depending on the user's language setting and retrieves the translations form the
     * language file found in src/language. Text to be shown on the screen is then translated based off the user's Locale
     * and current timezone.
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting Language:
        ResourceBundle rb = ResourceBundle.getBundle("language/language", Locale.getDefault());
        ZoneId userZone = ZoneId.systemDefault();
        String displayZone = String.valueOf(userZone.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()));
        loginTitle.setText(rb.getString("login.title"));
        loginSubtitle.setText(rb.getString("login.subtitle"));
        loginUsernameLabel.setText(rb.getString("login.username"));
        loginPasswordLabel.setText(rb.getString("login.password"));
        loginButton.setText(rb.getString("login.button"));
        languageLabel.setText(displayZone);

        //Alert:
        alertTitle = rb.getString("alertTitle");
        alertHeader = rb.getString("alertHeader");
        alertMessage = rb.getString("alertMessage");
    }

    /**
     * <p>
     *     Calls checkLogin and passes the User object you created earlier in class.
     * </p>
     * <p>
     *     If the login attempt returns successful, a Logger writes
     *     the attempted login to the file "login_activity.txt". Upcoming Appointments are checked, and the main menu is shown.
     * </p>
     * <p>
     *     if the login attempt returns unsuccessful, a Logger writes the attempted login to the file "login_activity.txt"
     *     and the "invalid password" alert is shown.
     * </p>
     * @param event - click on Login Button.
     */
    public void onLogin(ActionEvent event) {
        try {
            if (checkLogin(you)) {
                // Set Current User
                you = UserDAO.getUserByName(userNameInput.getText());
                // Show Schedule Page
                Logger.logAttempt(userNameInput.getText(), true);
                checkUpcomingApps();
                showMainMenu(event);
            }
            else {
                Logger.logAttempt(userNameInput.getText(), false);
                showAlert();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     *     An observable list containing all Users is created by calling getAllUsers() from UserDAO.
     * </p>
     * <p>
     *     Variables are created and assigned values from their respective text fields.
     *     The variables are then checked against every userName and password in the database.
     *     If there is a match the function returns true. If no match is found, the function returns false.
     * </p>
     *
     * @param user - user Class - see model.User
     * @return boolean: true if login was successful, false if unsuccessful.
     */
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
            // do nothing. return false.
        }
        return false;
    }

    /**
     * Shows alert in language specified during initialization for invalid username and/or password.
     *
     */
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(alertTitle);
        alert.setHeaderText(alertHeader);
        alert.setContentText(alertMessage);
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


    /**
     * <p>
     *     Creates two observable list to hold all Appointments and the Appointments that match the upcoming criteria.
     *     All times are converted to UTC to be compared to each other to ensure that function works in any timezone.
     *     Every Appointment's start time is then checked and added to upcoming appointments, if the start time is within
     *     15 minutes.
     *
     * </p>
     * <p>
     *     If there are any upcoming appointments, and alert containing the appointments names, and start times is shown.
     *     If there are no upcoming appointments, an alert is shown alerting the user.
     * </p>
     *
     */
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
            String message = "Hello, " + you.getUserName() + " you have " + upcoming.size() + " Upcoming Appointments:";
            for (Appointment a : upcoming) {
                    message = message + "\n" + a.getTitle() + ", Starting at: " + a.getDisplayStart();
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("UPCOMING APPOINTMENT!");
            alert.setHeaderText("You Have " + upcoming.size() + " Upcoming Appointments Within 15 Minutes!");
            alert.setContentText(message);
            alert.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pen Me In");
            alert.setHeaderText("Hello " + you.getUserName() + ", You Have No Upcoming Appointments.");
            alert.show();
        }
    }



}
