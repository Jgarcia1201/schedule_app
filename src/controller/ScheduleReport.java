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
     *     In addition the month comboBox is populated using the method initMonths().
     * </p>
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allApps = AppointmentDAO.getAllApps();
        months = initMonths(months);
        allSchedObj = getAllSchedObj();
        schedMonthComboBox.setItems(months);
    }

    /**
     * Hard codes the numerical representations of months and adds them to an observable list.
     *
     * @param list - list to be added to.
     * @return - list containing specified String values.
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
     * <p>
     *     When the Month ComboBox receives an action, the selection is saved as a string and an Observable List of
     *     ScheduleReportItems is created using the getTypeAppsByMonth() method with the selected month passed as a
     *     parameter.
     * </p>
     * <p>
     *     This observable list is then set to display in the on screen table and the table is refreshed to ensure
     *     correct data.
     * </p>
     * @param event - click on Month Combo Box.
     */
    public void onSchedMonthComboBoxAction(ActionEvent event) {
        String selectedMonth = schedMonthComboBox.getValue();
        ObservableList<ScheduleReportItem> appTypesAndCount = getTypeAppsByMonth(selectedMonth);
        schedTable.setItems(appTypesAndCount);
        schedCustomerId.setCellValueFactory(new PropertyValueFactory<>("type"));
        schedCount.setCellValueFactory(new PropertyValueFactory<>("count"));
        schedTable.refresh();
    }

    /**
     * <p>
     *     Creates an Observable List toReturn. A count is initialized to zero and every ScheduleReportItem is checked
     *     against every Appointment. If the types are shared and the Appointments month is the same as the parameter,
     *     1 is added to the count value of the ScheduleReportItem and the item is added to the toReturn list.
     * </p>
     *
     * @param param - String value representing desired month.
     * @return Observable List of all Appointments of the desired month.
     */
    private ObservableList<ScheduleReportItem> getTypeAppsByMonth(String param) {
        ObservableList<ScheduleReportItem> toReturn = FXCollections.observableArrayList();
        for (ScheduleReportItem schedItem : allSchedObj) {
            schedItem.setCount(0);
            for (Appointment app : allApps) {
                String start = app.getStart().toString();
                int dashOne = start.indexOf("-");
                int dashTwo = start.lastIndexOf("-");
                String month = start.substring(dashOne + 1, dashTwo);
                if (app.getType().equals(schedItem.getType()) && month.equals(param)) {
                    schedItem.setCount(schedItem.getCount() + 1);
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
     *     duplicateCheck list already contains the Appointments Type. If the type is unique, the type is added to the
     *     duplicate list, the ScheduleReportItem's customerID value is set to the unique type and then added to the return list.
     * </p>
     * <p>
     *     After every appointment, the toReturn list is returned.
     * </p>
     * @return - Observable List containing all ScheduleReportItems.
     */
    private ObservableList<ScheduleReportItem> getAllSchedObj() {
        ObservableList<ScheduleReportItem> toReturn = FXCollections.observableArrayList();
        ObservableList<String> duplicateCheck = FXCollections.observableArrayList();
        for (Appointment a : allApps) {
            ScheduleReportItem temp = new ScheduleReportItem();
            String type = a.getType();
            if (!duplicateCheck.contains(type)) {
                duplicateCheck.add(type);
                temp.setType(type);
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
