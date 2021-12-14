package controller;

import DAO.AppointmentDAO;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserReport implements Initializable {


    public ComboBox<String> userComboBox;
    public TableView<Appointment> userAppTable;
    public TableColumn<Appointment, String> userAppTitle;
    public TableColumn<Appointment, LocalDateTime> userAppUpdate;
    public TableView<Customer> userCustomerTable;
    public TableColumn<Customer, String> userCustomerName;
    public TableColumn<Customer, LocalDateTime> userCustomerUpdate;

    ObservableList<User> allUsers = FXCollections.observableArrayList();
    ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    ObservableList<Appointment> allApps = FXCollections.observableArrayList();
    ObservableList<Appointment> userApps = FXCollections.observableArrayList();
    ObservableList<Customer> userCustomers = FXCollections.observableArrayList();

    /**
     * Observable Lists containing all Users, Appointments, and Contacts are created.
     * <p>
     *     The User's names values are extracted using getUserNames() and set to display in the User combo box.
     * </p>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allUsers = UserDAO.getAllUsers();
        ObservableList<String> userNames = getUserNames(allUsers);
        userComboBox.setItems(userNames);

        allApps = AppointmentDAO.getAllApps();
        allCustomers = CustomerDAO.getAllCustomers();
    }

    /**
     * An observable list to return is created.
     * <p>
     *     Every User is then iterated through and name values are added to the Observable List.
     * </p>
     * @param user - An observable list of User objects.
     * @return - An observable list containing the name values of all Objects in the parameter.
     */
    private ObservableList<String> getUserNames(ObservableList<User> user) {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (User u : allUsers) {
            toReturn.add(u.getUserName());
        }
        return toReturn;
    }

    /**
     * <p>
     *     LAMBDA JUSTIFICATION - Using LAMBDA here improves readability and condensed the UserReport class by two functions.
     *     Due to the small amount of Users, there isn't a need to write out two separate methods for doing such similar things
     *     at the same time.
     * </p>
     * <p>
     *     The observable list obtained by the lambda functions are set to display on their respective Table Views.
     * </p>
     *
     * @param event - click on User Combo Box.
     */
    public void userComboBoxAction(ActionEvent event) {
        String choice = userComboBox.getValue();

        userApps = allApps.stream()
                .filter(app -> app.getLastUpdatedBy().equals(choice))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)); // LAMBDA 1
        userAppTable.setItems(userApps);
        userAppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        userAppUpdate.setCellValueFactory(new PropertyValueFactory<>("displayLastUpdate"));

        userCustomers = allCustomers.stream()
                .filter(customer -> customer.getLastUpdatedBy().equals(choice))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)); // LAMBDA 2
        userCustomerTable.setItems(userCustomers);
        userCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        userCustomerUpdate.setCellValueFactory(new PropertyValueFactory<>("displayLastUpdate"));

    }

    /**
     * User is returned to Main Menu. (Schedule)
     * @param event - click on return to menu button.
     */
    public void onUserReturn(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
