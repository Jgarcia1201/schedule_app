package controller;

import DAO.AppointmentDAO;
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
import model.ScheduleReportItem;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ScheduleReport implements Initializable {

    public ComboBox<String> schedMonthComboBox;
    public ComboBox<String> schedTypeComboBox;
    public TableView<ScheduleReportItem> schedTable;
    public TableColumn<ScheduleReportItem, Integer> schedCustomerId;
    public TableColumn<ScheduleReportItem, Integer> schedCount;

    ObservableList<String> months = FXCollections.observableArrayList();
    ObservableList<String> types = FXCollections.observableArrayList();
    ObservableList<Appointment> allApps = FXCollections.observableArrayList();
    ObservableList<ScheduleReportItem> allSchedObj = FXCollections.observableArrayList();

    /**
     * Initializes all Observable Lists to be used throughout the class.
     * <p>
     *     In addition the month and Type Combo boxes are populated with their repspective observable list.
     * </p>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allApps = AppointmentDAO.getAllApps();
        months = initMonths(months);
        allSchedObj = getAllSchedObj();
        schedMonthComboBox.setItems(months);
        types = initTypes();
        schedTypeComboBox.setItems(types);
    }

    /**
     * Hard codes the numerical representations of months and adds them to an observable list.
     *
     * @param list - list to be added to.
     * @return - list containing speicified String values.
     */
    public ObservableList<String> initMonths(ObservableList<String> list) {
        list.add("01");
        list.add("02");
        list.add("03");
        list.add("04");
        list.add("05");
        list.add("06");
        list.add("07");
        list.add("08");
        list.add("09");
        list.add("10");
        list.add("11");
        list.add("12");
        return list;
    }

    /**
     * Iterates over all Appointments and checks it their types are already contained in the toReturn Observable List.
     * This ensures that all values in the toReturn list are unique.
     *
     * @return - Observable list of all unique Types of appointments found in the Database.
     */
    public ObservableList<String> initTypes() {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (Appointment a : allApps) {
            if (!toReturn.contains(a.getType())) {
                toReturn.add(a.getType());
            }
        }
        return toReturn;
    }


    /**
     * <p>
     *     There are two possibilities when a user makes an action. Either a type is selected or it is not.
     *     First the method checks for which scenario the application is currently in. If the type comboBox returns a null value,
     *     an Observable List is created using the getCustomerAppsByMonth() method and displayed on the TableView.
     * </p>
     * <p>
     *     Otherwise, an observable list is created using the getCustomerAppsByMonthAndType() method and that list is displayed
     *     on the TableView.
     * </p>
     * @param event - click on Month Combo Box.
     */
    public void onSchedMonthComboBoxAction(ActionEvent event) {
        String selectedMonth = schedMonthComboBox.getValue();
        String selectedType = schedTypeComboBox.getValue();
        if (selectedType == null) {
            ObservableList<ScheduleReportItem> customerAppsByMonth = getCustomerAppsByMonth(selectedMonth);
            schedTable.setItems(customerAppsByMonth);
            schedCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            schedCount.setCellValueFactory(new PropertyValueFactory<>("monthCount"));
        }

        else {
            ObservableList<ScheduleReportItem> customerAppsByMonthAndType = getCustomerAppsByMonthAndType(selectedMonth, selectedType);
            schedTable.setItems(customerAppsByMonthAndType);
            schedCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            schedCount.setCellValueFactory(new PropertyValueFactory<>("totalCount"));
        }

        schedTable.refresh();

    }

    /**
     * <p>
     *     There are two possibilities when a user makes an action. Either a month is selected or it is not.
     *     First the method checks for which scenario the application is currently in. If the month comboBox returns a null value,
     *     an Observable List is created using the getCustomerAppsByType() method and displayed on the TableView.
     * </p>
     * <p>
     *     Otherwise, an observable list is created using the getCustomerAppsByMonthAndType() method and that list is displayed
     *     on the TableView.
     * </p>
     * @param event - click on schedule month combo box.
     */
    public void onSchedTypeComboBoxAction(ActionEvent event) {
        String selectedMonth = schedMonthComboBox.getValue();
        String selectedType = schedTypeComboBox.getValue();
        if (selectedMonth == null) {
            ObservableList<ScheduleReportItem> customerAppsByType = getCustomerAppsByType(selectedType);
            schedTable.setItems(customerAppsByType);
            schedCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            schedCount.setCellValueFactory(new PropertyValueFactory<>("typeCount"));
        }

        else {
            ObservableList<ScheduleReportItem> customerAppsByMonthAndType = getCustomerAppsByMonthAndType(selectedMonth, selectedType);
            schedTable.setItems(customerAppsByMonthAndType);
            schedCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            schedCount.setCellValueFactory(new PropertyValueFactory<>("totalCount"));
        }

        schedTable.refresh();
    }

    /**
     * <p>
     *     Creates an Observable List toReturn. A count is initialized to zero and every ScheduleReportItem is checked
     *     against every Appointment. If the customer ids are shared and the Appointments type is the same as the parameter,
     *     1 is added to the typeCount value of the ScheduleReportItem and the item is added to the toReturn list.
     * </p>
     *
     * @param param - String value representing desired type.
     * @return Observable List of all Appointments of the desired type.
     */
    private ObservableList<ScheduleReportItem> getCustomerAppsByType(String param) {
        ObservableList<ScheduleReportItem> toReturn = FXCollections.observableArrayList();
        for (ScheduleReportItem schedItem : allSchedObj) {
            schedItem.setTypeCount(0);
            for (Appointment app : allApps) {
                if (app.getCustomerId() == schedItem.getCustomerId() && app.getType().equals(param)) {
                    schedItem.setTypeCount(schedItem.getTypeCount() + 1);
                }
            }
            toReturn.add(schedItem);
        }
        return toReturn;
    }

    /**
     * <p>
     *     Creates an Observable List toReturn. A count is initialized to zero and every ScheduleReportItem is checked
     *     against every Appointment. The month is then extracted from the starting time of the appointment.
     *     If the customer ids are shared, the Appointments type is the same as the parameter,
     *     and the month is the same as the parameter.
     *     1 is added to the typeCount value of the ScheduleReportItem and the item is added to the toReturn list.
     * </p>
     *
     * @param selectedMonth - String value representing desired month.
     * @param selectedType String value representing desired type.
     * @return - Observable list containing all ScheduleReportItems of the desired type and month.
     */
    private ObservableList<ScheduleReportItem> getCustomerAppsByMonthAndType(String selectedMonth, String selectedType) {
        ObservableList<ScheduleReportItem> toReturn = FXCollections.observableArrayList();
        for (ScheduleReportItem schedItem : allSchedObj) {
            schedItem.setTotalCount(0);
            for (Appointment app : allApps) {
                String start = app.getStart().toString();
                int dashOne = start.indexOf("-");
                int dashTwo = start.lastIndexOf("-");
                String month = start.substring(dashOne + 1, dashTwo);
                if (app.getCustomerId() == schedItem.getCustomerId() && month.equals(selectedMonth) && app.getType().equals(selectedType)) {
                    schedItem.setTotalCount(schedItem.getTotalCount() + 1);
                }
            }
            toReturn.add(schedItem);
        }
        return toReturn;
    }

    /**
     * <p>
     *     Creates an Observable List toReturn. A count is initialized to zero and every ScheduleReportItem is checked
     *     against every Appointment. If the customer ids are shared and the Appointments month is the same as the parameter,
     *     1 is added to the month Count value of the ScheduleReportItem and the item is added to the toReturn list.
     * </p>
     *
     * @param param - String value representing desired month.
     * @return Observable List of all Appointments of the desired month.
     */
    private ObservableList<ScheduleReportItem> getCustomerAppsByMonth(String param) {
        ObservableList<ScheduleReportItem> toReturn = FXCollections.observableArrayList();
        for (ScheduleReportItem schedItem : allSchedObj) {
            schedItem.setMonthCount(0);
            for (Appointment app : allApps) {
                String start = app.getStart().toString();
                int dashOne = start.indexOf("-");
                int dashTwo = start.lastIndexOf("-");
                String month = start.substring(dashOne + 1, dashTwo);
                if (app.getCustomerId() == schedItem.getCustomerId() && month.equals(param)) {
                    schedItem.setMonthCount(schedItem.getMonthCount() + 1);
                }
            }
            toReturn.add(schedItem);
        }
        return toReturn;
    }

    /**
     * <p>
     *     Creates two Observable List, one to return and one to check the state of duplicate IDs.
     * </p>
     * <p>
     *     For every Appointment in allApps a new ScheduleReportItem is created. An if statement then checks whether the
     *     duplicateCheck list already contains the Appointments Customer Id. If the ID is unique, the id is added to the
     *     duplicate list, the ScheduleReportItem's customerID value is set to the unique ID and then added to the return list.
     * </p>
     * <p>
     *     After every appointment, the toReturn list is returned.
     * </p>
     * @return - Observable List containing all ScheduleReportItems.
     */
    private ObservableList<ScheduleReportItem> getAllSchedObj() {
        ObservableList<ScheduleReportItem> toReturn = FXCollections.observableArrayList();
        ObservableList<Integer> duplicateCheck = FXCollections.observableArrayList();
        for (Appointment a : allApps) {
            ScheduleReportItem temp = new ScheduleReportItem();
            int id = a.getCustomerId();
            if (!duplicateCheck.contains(id)) {
                duplicateCheck.add(id);
                temp.setCustomerId(id);
                toReturn.add(temp);
            }
        }
        return toReturn;
    }

    /**
     * Returns user to Main Menu (schedule).
     * @param event - click on Return to Menu Button
     */
    public void onSchedReturn(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
