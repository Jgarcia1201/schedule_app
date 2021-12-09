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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allApps = AppointmentDAO.getAllApps();
        months = initMonths(months);
        allSchedObj = getAllSchedObj();
        schedMonthComboBox.setItems(months);
        types = initTypes();
        schedTypeComboBox.setItems(types);


    }

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

    public ObservableList<String> initTypes() {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (Appointment a : allApps) {
            if (!toReturn.contains(a.getType())) {
                toReturn.add(a.getType());
            }
        }
        return toReturn;
    }


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
