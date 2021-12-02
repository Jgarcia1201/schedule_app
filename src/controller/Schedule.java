package controller;

import DAO.AppointmentDAO;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Appointment;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class Schedule implements Initializable {
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
    public TableView monthTable;
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


    ObservableList<Appointment> appointments = AppointmentDAO.getAllApps();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
    }
}
