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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allUsers = UserDAO.getAllUsers();
        ObservableList<String> userNames = getUserNames(allUsers);
        userComboBox.setItems(userNames);

        allApps = AppointmentDAO.getAllApps();
        allCustomers = CustomerDAO.getAllCustomers();
    }

    private ObservableList<String> getUserNames(ObservableList<User> user){
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (User u : allUsers) {
            toReturn.add(u.getUserName());
        }
        return toReturn;
    }

    public void userComboBoxAction(ActionEvent event) {
        String choice = userComboBox.getValue();
        userApps = getAppUpdatesByUser(choice);
        userAppTable.setItems(userApps);
        userAppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        userAppUpdate.setCellValueFactory(new PropertyValueFactory<>("displayLastUpdate"));

        userCustomers = getCustomerUpdatesByUser(choice);
        userCustomerTable.setItems(userCustomers);
        userCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        userCustomerUpdate.setCellValueFactory(new PropertyValueFactory<>("displayLastUpdate"));

    }

    public ObservableList<Customer> getCustomerUpdatesByUser(String s) {
        ObservableList<Customer> toReturn = FXCollections.observableArrayList();
        for (Customer c : allCustomers) {
            if (c.getLastUpdatedBy().equals(s)) {
                toReturn.add(c);
            }
        }
        return toReturn;
    }

    private ObservableList<Appointment> getAppUpdatesByUser(String s) {
        ObservableList<Appointment> toReturn = FXCollections.observableArrayList();
        for (Appointment a : allApps) {
            if (a.getLastUpdatedBy().equals(s)) {
                toReturn.add(a);
            }
        }
        return toReturn;
    }

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
