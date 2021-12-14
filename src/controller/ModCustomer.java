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

public class ModCustomer implements Initializable {
    public TextField modCustomerId;
    public TextField modCustomerName;
    public TextField modCustomerPhone;
    public TextField modCustomerPostal;
    public Label modCustomerToggleLabel;
    public ComboBox<String> modCustomerCountry;
    public ComboBox<String> modCustomerToggle;
    public TextField modCustomerCity;
    public TextField modCustomerStreet;
    public Button modCustomerSave;
    public Button modCustomerCancel;
    public Button modCustomerContactMenu;

    ObservableList<Country> allCountries = FXCollections.observableArrayList();
    ObservableList<Division> allDivs = FXCollections.observableArrayList();

    Customer currentCustomer = new Customer();

    /**
     * Two Observable List are created and populated with every Country and Contact in the Database respectively.
     * Next, the allCountries Observable List is passed through the getCountryNames function and assigned to allDivs.
     * This is then set to display in the Country combo box.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allCountries = CountryDAO.getAllCountries();
        allDivs = DivisionDAO.getAllDivs();

        ObservableList<String> countryNames = getCountryNames(allCountries);
        modCustomerCountry.setItems(countryNames);
    }


    /**
     * <p>
     *     An observable list is created to hold the name values. The parameter is then iterated over and the getCountryName()
     *     function of every item in the list is called and added to the list countryNames.
     *     The list is then returned.
     * </p>
     *
     * @param countries - An observable list of Country items.
     * @return an Observable List of String values containing the Counties' names.
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
     *     The parameter value is assigned to the global variable currentCustomer. The Customer setters and the setText functions are
     *     used in combination to fill the text boxes with the specified customer's respective values.
     * </p>
     * <p>
     *     The Customer's address is then separated into street & city values and then set to display in the street and city text fields.
     * </p>
     * <p>
     *     First level divisions are then handled by checking the Division objects Country ID against the Country ID of every Country Object.
     *     Depending on the result, the first level division label and combo boxes both display values.
     * </p>
     *
     * @param currentCustomer - Customer specified in the previous controller.
     */
    public void passCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
        modCustomerId.setText(String.valueOf(currentCustomer.getCustomerId()));
        modCustomerName.setText(currentCustomer.getName());
        modCustomerPhone.setText(currentCustomer.getPhone());
        modCustomerPostal.setText(currentCustomer.getPostalCode());
        // Address Manipulations
        String address = currentCustomer.getAddress();
        int commaIndex = address.indexOf(",");
        int commaTwoIndex = address.indexOf(",", commaIndex + 1);
        if (commaTwoIndex != -1) {
            String street = address.substring(0, commaIndex);
            String city = address.substring(commaIndex + 2, commaTwoIndex);
            modCustomerStreet.setText(street);
            modCustomerCity.setText(city);
        }
        else if (commaIndex != -1) {
            String street = address.substring(0, commaIndex);
            String city = address.substring(commaIndex + 2);
            modCustomerStreet.setText(street);
            modCustomerCity.setText(city);
        }
        else {
            modCustomerStreet.setText(address);
        }
        // Country & First Level Division
        int divId = currentCustomer.getDivisionId();
        Division div = getDivisionById(divId);
        Country country = getCountryByDiv(div);
        ObservableList<String> divNames = getCountryDivs(country);
        modCustomerCountry.setValue(country.getCountryName());
        modCustomerToggle.setValue(div.getDivName());
        modCustomerToggle.setItems(divNames);
        // Toggle Label
        if (country.getCountryName().equals("U.S")) {
            modCustomerToggleLabel.setText("State");
        }
        else if (country.getCountryName().equals("UK")) {
            modCustomerToggleLabel.setText("Country in U.K");
        }
        else if (country.getCountryName().equals("Canada")) {
            modCustomerToggleLabel.setText("Province");
        }
        else {
            modCustomerToggleLabel.setText("First Level Division");
        }
    }

    /**
     * Due to the first level Divisions being stored in numerical order, a Binary Search algorithm is used here to reduce
     * the time complexity of the search to O(log n) from O(n).
     *
     * @param id - integer of the target Division ID value.
     * @return - Division with matching ID value.
     */
    private Division getDivisionById(int id) {
        int left = 0;
        int right = allDivs.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (allDivs.get(mid).getDivId()  == id) {
                return allDivs.get(mid);
            }
            if (allDivs.get(mid).getDivId() < id) {
                left = mid + 1;
            }
            else {
                right = mid - 1;
            }
        }
        return null;
    }

    /**
     * Countries IDs are stored in increasing numerical order, therefore subtracting 1 from a countries ID will give its
     * position in the observable list. This fact is used to get around using linear search and improves time complexity
     * to O(1).
     *
     * @param d - Division containing target Country ID.
     * @return Country with matching values.
     */
    private Country getCountryByDiv(Division d) {
        int index = d.getCountryId() - 1;
        Country c = allCountries.get(index);
        return c;
    }

    /**
     * An observable list is created to hold the matching values and every Division's id is checked against the parameter's ID.
     * If there is matching values between the two function calls, the getDivName() string value is added to the list.
     *
     * @param c - Country object.
     * @return - an Observable List containing the names of all Divisions with matching country IDs.
     */
    private ObservableList<String> getCountryDivs(Country c) {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (Division d : allDivs) {
            if (c.getCountryId() == d.getCountryId()) {
                toReturn.add(d.getDivName());
            }
        }
        return toReturn;
    }

    /**
     * When the Country combo box is clicked, the selected value will cause the first level division
     * label and available values to change.
     */
    public void onModCustomerCountryAction() {
        Country country = CountryDAO.getCountryByName(modCustomerCountry.getValue());
        ObservableList<String> divNames = getCountryDivs(country);
        modCustomerToggle.setItems(divNames);
        // Toggle Label
        if (country.getCountryName().equals("U.S")) {
            modCustomerToggleLabel.setText("State");
        }
        else if (country.getCountryName().equals("UK")) {
            modCustomerToggleLabel.setText("Country in U.K");
        }
        else if (country.getCountryName().equals("Canada")) {
            modCustomerToggleLabel.setText("Province");
        }
        else {
            modCustomerToggleLabel.setText("First Level Division");
        }
    }

    /**
     * <p>
     *     A check is performed to ensure the user has inputted compatible values into all relevant text fields.
     *     If all checks out, the address is assembled using the inputted street and city values.
     *     currentCustomer's setters are then called in order to set the values of its variables to the inputted user
     *     values and an UPDATE statement is attempted. If this returns true, the Customer Menu is shown, otherwise an
     *     alert is shown to inform the user that something has went wrong.
     * </p>
     * @param event - click on the Save Button.
     */
    public void onModCustomerSave(ActionEvent event) {
        try {
            if (modCustomerStreet.getText().equals("") || modCustomerPostal.getText().equals("") ||
            modCustomerName.getText().equals("") || modCustomerPhone.getText().equals("")) {
                throw new Exception();
            }
            // Address Format
            String address;
            if (modCustomerCountry.getValue().equals("UK")) {
                address = modCustomerStreet.getText() + ", " + modCustomerCity.getText() +
                        ", " + modCustomerToggle.getValue(); // Assemble Address
            }
            else if (modCustomerCity.getText().equals("")) {
                address = modCustomerStreet.getText();
            }
            else {
                address = modCustomerStreet.getText() + ", " + modCustomerCity.getText(); // Assemble Address
            }
            int divId = DivisionDAO.getDivIdFromName(modCustomerToggle.getValue());
            currentCustomer.setName(modCustomerName.getText());
            currentCustomer.setAddress(address);
            currentCustomer.setPostalCode(modCustomerPostal.getText());
            currentCustomer.setPhone(modCustomerPhone.getText());
            currentCustomer.setLastUpdate(LocalDateTime.now());
            currentCustomer.setLastUpdatedBy(Login.you.getUserName());
            currentCustomer.setDivisionId(divId);
            if (CustomerDAO.updateCustomer(currentCustomer)) {
                Stage stage;
                Scene scene;
                Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerMenu.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                throw new Exception();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("INVALID INPUTS");
            alert.setHeaderText("Please Enter Values For All Text Fields");
            alert.setContentText("Please Try Again");
            alert.showAndWait();
        }
    }

    /**
     * Returns user to main menu. (Schedule)
     * @param event - click on the Return to Schedule Button.
     */
    public void onModCustomerCancel(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Returns user to Contact Menu.
     * @param event - click on the Contact Menu Button.
     */
    public void onModCustomerContactMenu(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/CustomerMenu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
