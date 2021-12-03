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
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class Schedule implements Initializable {
    public Button mainAddAppButton;
    private Stage stage;
    private Scene scene;
    public TableView weekTable;
    public TableColumn<Appointment, Integer> weekID;
    public TableColumn<Appointment, String> weekTitle;
    public TableColumn<Appointment, String> weekDesc;
    public TableColumn<Appointment, String> weekLocation;
    public TableColumn<Appointment, String> weekContact;
    public TableColumn<Appointment, String> weekType;
    public TableColumn<Appointment, LocalDateTime> weekStart;
    public TableColumn<Appointment, LocalDateTime> weekEnd;
    public TableColumn<Appointment, Integer> weekCustomerId;
    public TableColumn<Appointment, Integer> weekUserId;
    public TableView<Appointment> monthTable;
    public TableColumn<Appointment, Integer> monthID;
    public TableColumn<Appointment, String> monthTitle;
    public TableColumn<Appointment, String> monthDesc;
    public TableColumn<Appointment, String> monthLocation;
    public TableColumn<Appointment, String> monthContact;
    public TableColumn<Appointment, String> monthType;
    public TableColumn<Appointment, LocalDateTime> monthStart;
    public TableColumn<Appointment, LocalDateTime> monthEnd;
    public TableColumn<Appointment, Integer> monthCustomerID;
    public TableColumn<Appointment, Integer> monthUserID;
    public TableView<Appointment> allTable;
    public TableColumn<Appointment, Integer> allID;
    public TableColumn<Appointment, String> allTitle;
    public TableColumn<Appointment, String> allDesc;
    public TableColumn<Appointment, String> allLocation;
    public TableColumn<Appointment, String> allContact;
    public TableColumn<Appointment, String> allType;
    public TableColumn<Appointment, LocalDateTime> allStart;
    public TableColumn<Appointment, LocalDateTime> allEnd;
    public TableColumn<Appointment, Integer> allCustomerId;
    public TableColumn<Appointment, Integer> allUserId;
    public TableView customerTable;
    public TableColumn<Customer, String> customerTableName;

    private static ObservableList<Appointment> appointments = AppointmentDAO.getAllApps();
    private ObservableList<Customer> allCustomers = CustomerDAO.getAllCustomers();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Populating All Appointments Table
        allTable.setItems(appointments);
        allID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        allTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        allDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        allLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        allContact.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        allType.setCellValueFactory(new PropertyValueFactory<>("type"));
        allStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        allEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        allCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        allUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Populating Week Table
        weekTable.setItems(appointments);
        weekID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        weekTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        weekDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        weekLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        weekContact.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        weekType.setCellValueFactory(new PropertyValueFactory<>("type"));
        weekStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        weekEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        weekCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        weekUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Populating Month Table
        monthTable.setItems(appointments);
        monthID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        monthTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        monthDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        monthLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        monthContact.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        monthType.setCellValueFactory(new PropertyValueFactory<>("type"));
        monthStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        monthEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
        monthCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        monthUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Populating Customer Table
        customerTable.setItems(allCustomers);
        customerTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    public void onMainAddAppButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onMainExitButtonAction(ActionEvent event) {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
