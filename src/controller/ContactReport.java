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

    /**
     * <p>
     *     Two observable list are created and assigned the values returned from getAllContacts and getAllApps respectively.
     * </p>
     * <p>
     *     An observable list of strings is obtained by calling the getContactNames with allContacts passed as a parameter
     *      and the list is set to display in the contact ComboBox.
     * </p>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allContacts = ContactDAO.getAllContacts();
        allApps = AppointmentDAO.getAllApps();

        ObservableList<String> contactNames = getContactNames(allContacts);
        contactComboBox.setItems(contactNames);
    }

    /**
     * <p>
     *     When the contactComboBox is clicked, the user's choice is stored in a String. The Contact of the same name is
     *     the retrieved using the getContactByName function with the user's selected passed as a parameter. The Contact is then
     *     passed as a parameter into getAppsByContact which returns an Observable List which is then displayed in the contactAppTable
     *     Table View.
     * </p>
     */
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

    /**
     * Every Contact's name value in allContacts is compared to the String provided by the parameter. When a match is found,
     * the Contact object is returned.
     *
     * @param s - String value, Contact's name.
     * @return Contact with matching name value.
     */
    private Contact getContactByName(String s) {
        for (Contact c : allContacts) {
            if (c.getContactName().equals(s)) {
                return c;
            }
        }
        return null;
    }

    /**
     * An observable list is created to store all Appointments with matching ContactIds.
     * Every Appointment's contactId is checked against the parameter and when a match is found, it is added to the Observable list.
     *
     * @param c - Contact of desired appointments.
     * @return - Observable List containing all Appointment objects with matching Contact IDs.
     */
    private ObservableList<Appointment> getAppsByContact(Contact c) {
        ObservableList<Appointment> toReturn = FXCollections.observableArrayList();
        for (Appointment app : allApps) {
            if (c.getContactId() == app.getContactId()) {
                toReturn.add(app);
            }
        }
        return toReturn;
    }

    /**
     * An observable list is created to store all Contact's name values.
     * Every Contact's getContactName() is called and the value is added to the Observable List.
     *
     * @param list - Observable list of Contact objects.
     * @return - Observable List containing all name values of parameter list.
     */
    private ObservableList<String> getContactNames(ObservableList<Contact> list) {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (Contact c : list) {
            toReturn.add(c.getContactName());
        }
        return toReturn;
    }


    /**
     * Returns the user to the Main Menu. (Schedule.)
     *
     * @param event - click on return to schedule button.
     */
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
