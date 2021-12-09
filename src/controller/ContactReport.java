package controller;

import DAO.AppointmentDAO;
import DAO.ContactDAO;
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
import model.Contact;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ContactReport implements Initializable {

    public TableView<Appointment> contactAppTable;
    public TableColumn<Appointment, Integer> contactAppID;
    public TableColumn<Appointment, String> contactAppTitle;
    public TableColumn<Appointment, String> contactAppDesc;
    public TableColumn<Appointment, String> contactAppLocation;
    public TableColumn<Appointment, String> contactAppContact;
    public TableColumn<Appointment, String> contactAppType;
    public TableColumn<Appointment, LocalDateTime> contactAppStart;
    public TableColumn<Appointment, LocalDateTime> contactAppEnd;
    public TableColumn<Appointment, Integer> contactAppCustomerId;
    public ComboBox<String> contactComboBox;
    private ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private ObservableList<Appointment> allApps = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allContacts = ContactDAO.getAllContacts();
        allApps = AppointmentDAO.getAllApps();

        ObservableList<String> contactNames = getContactNames(allContacts);
        contactComboBox.setItems(contactNames);
    }

    public void contactComboBoxAction() {
        String choice = contactComboBox.getValue();
        Contact chosenOne = getContactByName(choice);
        ObservableList<Appointment> contactApp = getAppsByContact(chosenOne);
        contactAppTable.setItems(contactApp);
        contactAppID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        contactAppTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        contactAppDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        contactAppLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactAppContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        contactAppType.setCellValueFactory(new PropertyValueFactory<>("type"));
        contactAppStart.setCellValueFactory(new PropertyValueFactory<>("displayStart"));
        contactAppEnd.setCellValueFactory(new PropertyValueFactory<>("displayEnd"));
        contactAppCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    }

    private Contact getContactByName(String s) {
        for (Contact c : allContacts) {
            if (c.getContactName().equals(s)) {
                return c;
            }
        }
        return null;
    }

    private ObservableList<Appointment> getAppsByContact(Contact c) {
        ObservableList<Appointment> toReturn = FXCollections.observableArrayList();
        for (Appointment app : allApps) {
            if (c.getContactId() == app.getContactId()) {
                toReturn.add(app);
            }
        }
        return toReturn;
    }

    private ObservableList<String> getContactNames(ObservableList<Contact> list) {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (Contact c : list) {
            toReturn.add(c.getContactName());
        }
        return toReturn;
    }


    public void onContactReturn(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
