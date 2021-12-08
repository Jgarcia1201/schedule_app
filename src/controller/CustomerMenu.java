package controller;

import DAO.AppointmentDAO;
import DAO.CustomerDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class CustomerMenu implements Initializable {
    public TableView<Customer> customerMenuTable;
    public TableColumn<Customer, Integer> customerIDCol;
    public TableColumn<Customer, String> customerNameCol;
    public TableColumn<Customer, String> customerAddCol;
    public TableColumn<Customer, String> customerZipCol;
    public TableColumn<Customer, String> customerPhoneCol;
    public TableView<Appointment> customerAppTable;
    public TableColumn<Appointment, Integer> customerAppId;
    public TableColumn<Appointment, String> customerAppName;
    public TableColumn<Appointment, String> customerAppLocation;
    public TableColumn<Appointment, LocalDateTime> customerAppStart;
    public TableColumn<Appointment, LocalDateTime> customerAppEnd;
    public TableColumn<Appointment, String> customerAppContact;

    ObservableList<Customer> allCustomers = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allCustomers = CustomerDAO.getAllCustomers();
        customerMenuTable.setItems(allCustomers);
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerZipCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    public void onCustomerTableClicked() {
        Customer selected = customerMenuTable.getSelectionModel().getSelectedItem();
        if (selected.equals(null)) {
            System.out.println("hello");
            return;
        }
        try {
            ObservableList<Appointment> selectedApps = AppointmentDAO.getCustomerAppointments(selected);
            customerAppTable.setItems(selectedApps);
            customerAppId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            customerAppName.setCellValueFactory(new PropertyValueFactory<>("title"));
            customerAppLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            customerAppStart.setCellValueFactory(new PropertyValueFactory<>("displayStart"));
            customerAppEnd.setCellValueFactory(new PropertyValueFactory<>("displayEnd"));
            customerAppContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void onReturnToMainMenuButtonAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onAddCustomerAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
