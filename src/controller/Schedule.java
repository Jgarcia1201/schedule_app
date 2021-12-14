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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointment;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class Schedule implements Initializable {
    public Button mainAddAppButton;
    public Tab weekTab;
    public TabPane allTabs;
    public Button mainCancelButton;
    public TableView<Appointment> weekTable;
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
    public TableColumn<Appointment, Integer> monthCustomerId;
    public TableColumn<Appointment, Integer> monthUserId;
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
    public TableView<Customer> customerTable;
    public TableColumn<Customer, String> customerTableName;
    public Button mainModApp;
    private Stage stage;
    private Scene scene;

    private static ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Appointment> weekly = FXCollections.observableArrayList();
    private ObservableList<Appointment> monthly = FXCollections.observableArrayList();

    /**
     * LAMBDA JUSTIFICATION - Makes code more readable as otherwise each eventListener would have to be added explicitly
     * to each tab.
     * <p>
     *     The Appointment and Contact Table Views are then populated with Observable list containing all Contact and
     *     Appointment objects currently stored in the DataBase.
     * </p>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Adding Event Listener to TabPane.
        allTabs.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                handleChangeTab(newValue.getText())
        ); // Lambda 3

        

        // Populating All Appointments Table
        appointments = AppointmentDAO.getAllApps();
        allTable.setItems(appointments);
        allID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        allTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        allDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        allLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        allContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        allType.setCellValueFactory(new PropertyValueFactory<>("type"));
        allStart.setCellValueFactory(new PropertyValueFactory<>("displayStart"));
        allEnd.setCellValueFactory(new PropertyValueFactory<>("displayEnd"));
        allCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        allUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        allTable.setVisible(true);
        weekTable.setVisible(false);
        monthTable.setVisible(false);

        // Populating Customer Table
        allCustomers = CustomerDAO.getAllCustomers();
        customerTable.setItems(allCustomers);
        customerTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    /**
     * User is taken to the Add Appointment screen.
     * @param event - click on the Add Appointment Button.
     */
    public void onMainAddAppButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The currently displayed tab is identified, and the selected Appointment object is assigned to the selected variable.
     * The Modify Appointment controller is loaded and the selected Appointment is passed using the passAppointment method
     * contained in the Modify Appointment Controller. If successful, the user is taken to the Modify Appointment screen,
     * otherwise an exception is thrown and an alert is shown notify the user.
     *
     * @param event - Modify Appointment is clicked.
     */
    public void onMainModAppAction(ActionEvent event) throws IOException {
        Appointment selected = new Appointment();
        try {
            if (monthTable.isVisible()) {
                selected = monthTable.getSelectionModel().getSelectedItem();
            }
            else if (weekTable.isVisible()) {
                selected = weekTable.getSelectionModel().getSelectedItem();
            }
            else {
                selected = allTable.getSelectionModel().getSelectedItem();
            }

            if (selected == null) {
                throw new Exception();
            }

            // Load Controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ModAppointment.fxml"));
            Parent root = loader.load();
            // Pass Data
            ModAppointment ma = loader.getController();
            ma.passAppointment(selected);
            // Stage
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("WARNING!!");
            alert.setHeaderText("Select An Appointment");
            alert.setContentText("Please Select an Appointment and Try Again");
            alert.show();
        }
    }

    /**
     * <p>
     *     Depending on the tab selected, a String value is passed through the function.
     *     Visibility values are set to their correct state and the Week, Table, or Month table is populated depending on
     *     the tab selected.
     * </p>
     * <p>
     *     Observable List are obtained using the getWeekApps(), getMonthApps(), and getAllApps() from the AppointmentDAO
     *     respectively.
     * </p>
     *
     * @param s - String value/Name of clicked Tab.
     */
    public void handleChangeTab(String s) {
        if (s.equals("Week")) {
            allTable.setVisible(false);
            monthTable.setVisible(false);
            weekTable.setVisible(true);
            weekly = AppointmentDAO.getWeekApps();
            weekTable.setItems(weekly);
            weekID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            weekTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            weekDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
            weekLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            weekContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
            weekType.setCellValueFactory(new PropertyValueFactory<>("type"));
            weekStart.setCellValueFactory(new PropertyValueFactory<>("displayStart"));
            weekEnd.setCellValueFactory(new PropertyValueFactory<>("displayEnd"));
            weekCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            weekUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        }
        else if (s.equals("Month")) {
            allTable.setVisible(false);
            weekTable.setVisible(false);
            monthTable.setVisible(true);
            monthly = AppointmentDAO.getMonthApps();
            monthTable.setItems(monthly);
            monthID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            monthTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            monthDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
            monthLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            monthContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
            monthType.setCellValueFactory(new PropertyValueFactory<>("type"));
            monthStart.setCellValueFactory(new PropertyValueFactory<>("displayStart"));
            monthEnd.setCellValueFactory(new PropertyValueFactory<>("displayEnd"));
            monthCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            monthUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        }
        else {
            weekTable.setVisible(false);
            monthTable.setVisible(false);
            allTable.setVisible(true);
            appointments = AppointmentDAO.getAllApps();
            allTable.setItems(appointments);
            allID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
            allTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            allDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
            allLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
            allContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
            allType.setCellValueFactory(new PropertyValueFactory<>("type"));
            allStart.setCellValueFactory(new PropertyValueFactory<>("displayStart"));
            allEnd.setCellValueFactory(new PropertyValueFactory<>("displayEnd"));
            allCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            allUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        }
    }

    /**
     * <p>
     *     A conditional check verifies that the appointment has selected an appointment from the current displaying
     *     Table View.
     * </p>
     * <p>
     *     A confirmation alert is then shown to the user verifying their desire to delete the appointment.
     *     If the user selects Okay, a DELETE statement is executed using removeApp from the AppointmentDAO and is removed from
     *     the appointments list.
     * </p>
     *
     * @param event - click on Cancel Appointment Button.
     */
    public void onMainCancelButtonAction(ActionEvent event) {
        if (monthTable.isVisible()) {
            Appointment selected = monthTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("WARNING!");
                alert.setContentText("Are You Sure You Want To Delete: " + selected.getAppointmentId() + " " + selected.getTitle() + " a " + selected.getType() + " Appointment?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    AppointmentDAO.removeApp(selected);
                    appointments.remove(selected);
                    monthly.remove(selected);
                }
            }
        }
        else if (weekTable.isVisible()) {
            Appointment selected = weekTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("WARNING!");
                alert.setContentText("Are You Sure You Want To Delete: " + selected.getAppointmentId() + " " + selected.getTitle() + " a " + selected.getType() + " Appointment?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    AppointmentDAO.removeApp(selected);
                    appointments.remove(selected);
                    weekly.remove(selected);
                }
            }
        }
        else {
            Appointment selected = allTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete Confirmation");
                alert.setHeaderText("WARNING!");
                alert.setContentText("Are You Sure You Want To Delete: \n"
                        + selected.getTitle() + " a " + selected.getType() + " Appointment?" + "\n\n"
                        + "ID Number: " + selected.getAppointmentId());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    AppointmentDAO.removeApp(selected);
                    appointments.remove(selected);
                }
            }
        }
    }

    /**
     * User is taken to Add Customer page.
     * @param event - Click on Add Customer Button
     */
    public void onScheduleAddCustomerAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddCustomer.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * User is taken to Customer Menu page.
     * @param event - click on Customer Menu Button.
     */
    public void onCustomerMenuButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Application is closed.
     * @param event - click on Exit Button.
     */
    public void onMainExitButtonAction(ActionEvent event) {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * The currently displayed tab is identified, and the selected Customer object is assigned to the selected variable.
     * The Modify Customer controller is loaded and the selected Customer is passed using the passAppointment method
     * contained in the Modify Customer Controller. If successful, the user is taken to the Modify Customer screen,
     * otherwise an exception is thrown and an alert is shown notify the user.
     *
     * @param event - click on Modify Customer Button.
     */
    public void onMainModCustomerButtonAction(ActionEvent event) {
        Stage stage;
        Scene scene;
        try {
            Customer selected = customerTable.getSelectionModel().getSelectedItem();
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
     * User is taken to the Contact Report Page.
     * @param event - Click on the Contact Report Button.
     */
    public void onContactReportAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/ContactReport.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * User is taken to the User Report Page.
     * @param event - click on the User Report Button.
     */
    public void onUserReportAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/UserReport.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * User is taken to the Schedule Report Page.
     * @param event - click on the Schedule Report Action.
     */
    public void onScheduleReportAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/ScheduleReport.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
