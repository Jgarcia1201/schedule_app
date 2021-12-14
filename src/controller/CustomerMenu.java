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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;


public class CustomerMenu implements Initializable {
    public TableView<Customer> customerMenuTable;
    public TableColumn<Customer, Integer> customerIDCol;
    public TableColumn<Customer, String> customerNameCol;
    public TableColumn<Customer, String> customerAddCol;
    public TableColumn<Customer, String> customerZipCol;
    public TableColumn<Customer, String> customerPhoneCol;
    public TableColumn<Customer, Integer>customerDivCol;
    public TableView<Appointment> customerAppTable;
    public TableColumn<Appointment, Integer> customerAppId;
    public TableColumn<Appointment, String> customerAppName;
    public TableColumn<Appointment, String> customerAppLocation;
    public TableColumn<Appointment, LocalDateTime> customerAppStart;
    public TableColumn<Appointment, LocalDateTime> customerAppEnd;
    public TableColumn<Appointment, String> customerAppContact;

    ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    ObservableList<Appointment> customerApps = FXCollections.observableArrayList();

    /**
     * allCustomers is set to all customers in the database. Calling during the initializing period allows minimization of
     * Database function calls.
     * The allCustomers list and it's values are then set to display in the customerMenuTable.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allCustomers = CustomerDAO.getAllCustomers();
        customerMenuTable.setItems(allCustomers);
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerZipCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDivCol.setCellValueFactory(new PropertyValueFactory<>("divisionId"));
    }

    /**
     * <p>
     *     Activated when the TableView customerMenuTable is clicked.
     *     The function the attempts to create a Customer from the selected value.
     *      If there is no Customer selected, the function returns without doing anything.
     * </p>
     * <p>
     *     If a Customer is successfully created, customerApps is then assigned a value using AppointmentDAO.getCustomerAppointments()
     *     this list is then set to display in the customerAppTable TableView.
     * </p>
     */
    public void onCustomerTableClicked() {
        Customer selected = customerMenuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        try {
            customerApps = AppointmentDAO.getCustomerAppointments(selected);
            customerAppTable.setItems(customerApps);
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

    /**
     * Loads and displayds Schedule controller and view.
     * @param event - click on Return To Menu Button.
     */
    public void onReturnToMainMenuButtonAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads and displays AddCustomer controller and view.
     * @param event - Click on Add Customer Button.
     */
    public void onAddCustomerAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * <p>
     *     Attempts to create a Customer. If unsuccessful an alert is shown, instructing the user to select a customer and try again
     * </p>
     * <p>
     *     If the attempt was successful, the ModCustomer controller is loaded and the selected Customer is passed through the passCustomer() method
     *     and the ModCustomer view is displayed.
     * </p>
     * @param event - Click on the Modify Customer Button.
     */
    public void customerModButtonAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        try {
            Customer selected = customerMenuTable.getSelectionModel().getSelectedItem();
            if (selected.equals(null)) {
                throw new Exception();
            }
            // Load Controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModCustomer.fxml"));
            Parent root = loader.load();
            // Pass Data
            ModCustomer mc = loader.getController();
            mc.passCustomer(selected);
            // Stage
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("WARNING!!");
            alert.setHeaderText("Select A Customer");
            alert.setContentText("Please Select a Customer and Try Again");
            alert.show();
        }
    }

    /**
     * <p>
     *     Attempts to create a Customer based off user selection. If unsuccessful, the function returns without doing anything.
     * </p>
     * <p>
     *     If successful, the current size of customerApps is checked. If it is non empty, the customer has existing appointments and
     *     cannot be deleted. In this case, an alert is displayed informing the user.
     * </p>
     * <p>
     *     If currentApps is empty, a confirmation alert is shown verifying the user's desire to delete the selected Customer.
     *     If the user selects Okay, a DELETE statement is then preformed on the Database, and the Customer is removed from the
     *     Database as well as the allCustomers Observable List.
     * </p>
     * @param event - click on Delete Customer Button.
     */
    public void onCustomerMenuDeleteButtonAction(ActionEvent event) {
        Customer selected = customerMenuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        else {
            if (customerApps.size() == 0) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("WARNING!");
                alert.setContentText("Are You Sure You Want To Delete: " + selected.getName() + "?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    CustomerDAO.deleteCustomer(selected);
                    allCustomers.remove(selected);
                }
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("CANNOT DELETE CUSTOMER!!");
                alert.setHeaderText("Cannot Delete Customer With Scheduled Appointments!");
                alert.setContentText("Check Customer Appointments");
                alert.show();
            }
        }
    }
}
