package controller;

import DAO.CountryDAO;
import DAO.CustomerDAO;
import DAO.DivisionDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Country;
import model.Customer;
import model.Division;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class AddCustomer implements Initializable {

    public TextField addCustomerId;
    public TextField addCustomerName;
    public TextField addCustomerPhone;
    public TextField addCustomerPostal;
    public ComboBox<String> addCustomerCountry;
    public ComboBox<String> addCustomerToggle;
    public Button addCustomerSave;
    public Button addCustomerCancel;
    public Label addCustomerToggleLabel;
    public TextField addCustomerCity;
    public TextField addCustomerStreet;

    private ObservableList<Country> allCountries = FXCollections.observableArrayList();
    private ObservableList<Division> allDivs = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allDivs = DivisionDAO.getAllDivs();
        allCountries = CountryDAO.getAllCountries();

        ObservableList<String> countryNames = getCountryNames(allCountries);
        addCustomerCountry.setItems(countryNames);

        // Id Gen
        CustomerDAO.customerIdGen.set(CustomerDAO.getAllCustomers().size() + 1);
        int customerId = CustomerDAO.customerIdGen.get();
        addCustomerId.setText(String.valueOf(customerId));
    }

    public void onAddCustomerSave(ActionEvent event) {
        try {
            int customerId = CustomerDAO.customerIdGen.getAndIncrement();
            String name = addCustomerName.getText();
            String phone = addCustomerPhone.getText();
            String postal = addCustomerPostal.getText();
            String city = addCustomerCity.getText();
            String street = addCustomerStreet.getText();
            String div = addCustomerToggle.getSelectionModel().getSelectedItem();
            String country = addCustomerCountry.getSelectionModel().getSelectedItem();
            LocalDateTime createDate = LocalDateTime.now();
            LocalDateTime lastUpdate = LocalDateTime.now();
            String lastUpdateBy = Login.you.getUserName();
            String createdBy = Login.you.getUserName();
            // Checking Blanks
            if (name.equals("") || phone.equals("") || postal.equals("") || city.equals("") || street.equals("") || div.equals("") || country.equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("INVALID INPUTS");
                alert.setHeaderText("Please Enter Values For All Text Fields");
                alert.setContentText("Please Try Again");
                alert.showAndWait();
                return;
            }
            // Getting Division Id
            int divId = DivisionDAO.getDivIdFromName(div);
            // Assembling Addresses
            String displayAddress = street + ", " + city;

            Customer newCustomer = new Customer();
            newCustomer.setCustomerId(customerId);
            newCustomer.setName(name);
            newCustomer.setPhone(phone);
            newCustomer.setPostalCode(postal);
            newCustomer.setAddress(displayAddress);
            newCustomer.setCountry(country);
            newCustomer.setDivisionId(divId);
            newCustomer.setLastUpdatedBy(lastUpdateBy);
            newCustomer.setLastUpdate(lastUpdate);
            newCustomer.setCreatedBy(createdBy);
            newCustomer.setCreateDate(createDate);
            String result = CustomerDAO.insertCustomer(newCustomer);
            if (result.equals("Success")) {
                showMainMenu(event);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ObservableList<String> getCountryNames(ObservableList<Country> countries) {
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        for (Country c : countries) {
            countryNames.add(c.getCountryName());
        }
        return countryNames;
    }

    public void onAddCustomerCountryAction() {
        String selectedName = addCustomerCountry.getSelectionModel().getSelectedItem();
        Country selected = CountryDAO.getCountryByName(selectedName);
        ObservableList<String> divList = FXCollections.observableArrayList();
        for (Division div : allDivs) {
            if (div.getCountryId() == selected.getCountryId()) {
                divList.add(div.getDivName());
            }
        }
        addCustomerToggle.setItems(divList);

        if (selectedName.equals("U.S")) {
            addCustomerToggleLabel.setText("State");
        }
        else if (selectedName.equals("UK")) {
            addCustomerToggleLabel.setText("Country in UK");
        }
        else if (selectedName.equals("Canada")) {
            addCustomerToggleLabel.setText("Province");
        }
        else {
            addCustomerToggleLabel.setText("First Level Division");
        }
    }

    public ObservableList<Division> getDivsByCountryId(int id) {
        ObservableList<Division> resultDivs = FXCollections.observableArrayList();
        allDivs = DivisionDAO.getAllDivs();
        for (Division div : allDivs) {
            if (div.getCountryId() == id) {
                resultDivs.add(div);
            }
        }
        return resultDivs;
    }

    public ObservableList<String> getDivNames(ObservableList<Division> d) {
        ObservableList<String> divNames = FXCollections.observableArrayList();
        for (Division div : d) {
            divNames.add(div.getDivName());
        }
        return divNames;
    }

    public void onAddCustomerCancel(ActionEvent event) throws IOException {
        showMainMenu(event);
    }

    public void onAddCustomerContactMenu(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void showMainMenu(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
