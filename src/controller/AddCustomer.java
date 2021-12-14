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

    /**
     * <p>
     *    Two observable lists are created holding all first level divisions and countries for use throughout the method.
     *    To ensure that these function calls are only called once.
     * </p>
     * <p>
     *     An observable list is then created using the local method, getCountryNames().
     *     This list is then set to display in the country combo box.
     * </p>
     * <p>
     *     An ID is generated using an Atomic Integer, and filled in to the uneditable CustomerID text field.
     * </p>
     */
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

    /**
     * <p>
     *     Triggered by an action performed on the button reading "Save".
     *      Variables are created and used to store the values of the controller's text fields and combo boxes.
     * </p>
     * <p>
     *     Validation is performed on the variables ensuring valid input from user.
     *     A Customer class is created from the user's inputs and is inserted using the CustomerDAO method:
     *     insertCustomer()
     * </p>
     * <p>
     *     insertCustomer() returns a boolean value indicating either a successful INSERT statement into the Database
     *     or a failure to do so.
     * </p>
     * @param event - click on the Save button.
     */
    public void onAddCustomerSave(ActionEvent event) {
        try {
            String displayAddress;
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
            if (country.equals("UK")) {
                displayAddress = street + ", " + city + ", " + div + ", " + country;
            }
            else {
                displayAddress = street + ", " + city;
            }

            Customer newCustomer = new Customer();
            newCustomer.setCustomerId(customerId);
            newCustomer.setName(name);
            newCustomer.setPhone(phone);
            newCustomer.setPostalCode(postal);
            newCustomer.setAddress(displayAddress);
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

    /**
     * An observable list to hold the String values of every Country item in allCountries is created.
     * Each Country's getCountryName() method is called and the returning value is added to the observable list and
     * returned.
     *
     * @param countries - ObservableList containing Country instances.
     * @return
     */
    public ObservableList<String> getCountryNames(ObservableList<Country> countries) {
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        for (Country c : countries) {
            countryNames.add(c.getCountryName());
        }
        return countryNames;
    }

    /**
     * <p>
     *     When the a country is selected using the countries combo box, the String value obtained is used in the function call
     *     CountryDAO.getCountryByName(). This function returns a country based of the String parameter provided.
     * </p>
     * <p>
     *     An observable list is the created using the local method getDivNamesByCountry().
     *     this list is set to display in the combo box intended to display first level divisions.
     * </p>
     * <p>
     *     Label is then toggled depending on Country selection.
     * </p>
     */
    public void onAddCustomerCountryAction() {
        String selectedName = addCustomerCountry.getSelectionModel().getSelectedItem();
        Country selected = CountryDAO.getCountryByName(selectedName);
        ObservableList<String> divList = getDivNamesByCountry(selected);
        addCustomerToggle.setItems(divList);
        // Toggle Label
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

    /**
     * <p>
     *     An observable list is created to hold the names of matching Division.
     * </p>
     * <p>
     *     In allDivs, every Division's Country Id is compared against the return value of the parameter's getCountryId() method.
     *     If the two values are equal to each other, the current Division's name will be added to the list to return.
     * </p>
     * @param country - country. country.getCountryId() will be accessed
     * @return an Observable List containing String values obtained from all relevant Divisions.
     */
    private ObservableList<String> getDivNamesByCountry(Country country) {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (Division d: allDivs) {
            if (country.getCountryId() == d.getCountryId()) {
                toReturn.add(d.getDivName());
            }
        }
        return toReturn;
    }

    public void onAddCustomerCancel(ActionEvent event) throws IOException {
        showMainMenu(event);
    }

    /**
     * Returns program to CustomerMenu.
     * @param event - click on Customer Menu Button
     */
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
