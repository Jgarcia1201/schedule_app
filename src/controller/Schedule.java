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
    public TableView monthTable;
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


    ObservableList<Appointment> appointments = AppointmentDAO.getAllApps();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
    }
}
