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
import model.Contact;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Adding Event Listener to TabPane.
        allTabs.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) ->
                handleChangeTab(newValue.getText())
        );

        // Populating All Appointments Table
        appointments = AppointmentDAO.getAllApps();
        allTable.setItems(appointments);
        allID.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        allTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        allDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        allLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        allContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        allType.setCellValueFactory(new PropertyValueFactory<>("type"));
        allStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        allEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
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

    public void onMainAddAppButtonAction(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/AddAppointment.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

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
            weekStart.setCellValueFactory(new PropertyValueFactory<>("start"));
            weekEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
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
            monthStart.setCellValueFactory(new PropertyValueFactory<>("start"));
            monthEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
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
            allStart.setCellValueFactory(new PropertyValueFactory<>("start"));
            allEnd.setCellValueFactory(new PropertyValueFactory<>("end"));
            allCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            allUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        }
    }

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
                alert.setContentText("Are You Sure You Want To Delete " + selected.getTitle() + "?");
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
                alert.setContentText("Are You Sure You Want To Delete " + selected.getTitle() + "?");
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
                alert.setContentText("Are You Sure You Want To Delete " + selected.getTitle() + "?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    AppointmentDAO.removeApp(selected);
                    appointments.remove(selected);
                }
            }
        }
    }


    public void onMainExitButtonAction(ActionEvent event) {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
