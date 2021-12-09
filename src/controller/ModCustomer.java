package controller;

import DAO.CountryDAO;
import DAO.CustomerDAO;
import DAO.DivisionDAO;
import com.mysql.cj.util.StringUtils;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        allCountries = CountryDAO.getAllCountries();
        allDivs = DivisionDAO.getAllDivs();

        ObservableList<String> countryNames = getCountryNames(allCountries);
        modCustomerCountry.setItems(countryNames);
    }

    public ObservableList<String> getCountryNames(ObservableList<Country> countries) {
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        for (Country c : countries) {
            countryNames.add(c.getCountryName());
        }
        return countryNames;
    }

    public void passCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
        modCustomerId.setText(String.valueOf(currentCustomer.getCustomerId()));
        modCustomerName.setText(currentCustomer.getName());
        modCustomerPhone.setText(currentCustomer.getPhone());
        modCustomerPostal.setText(currentCustomer.getPostalCode());
        // Address Manipulations
        String address = currentCustomer.getAddress();
        String street;
        String city;
        int commaIndex = address.indexOf(",");
        if (commaIndex != -1) {
            street = address.substring(0, commaIndex);
            city = address.substring(commaIndex + 2);
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

    private Country getCountryByDiv(Division d) {
        int index = d.getCountryId() - 1;
        Country c = allCountries.get(index);
        return c;
    }

    private ObservableList<String> getCountryDivs(Country c) {
        ObservableList<String> toReturn = FXCollections.observableArrayList();
        for (Division d : allDivs) {
            if (c.getCountryId() == d.getCountryId()) {
                toReturn.add(d.getDivName());
            }
        }
        return toReturn;
    }

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

    public void onModCustomerSave(ActionEvent event) {
        try {
            if (modCustomerStreet.getText().equals("") || modCustomerPostal.getText().equals("") ||
            modCustomerName.getText().equals("") || modCustomerPhone.getText().equals("")) {
                throw new Exception();
            }

            String address = modCustomerStreet.getText() + ", " + modCustomerCity.getText(); // Assemble Address
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("INVALID INPUTS");
            alert.setHeaderText("Please Enter Values For All Text Fields");
            alert.setContentText("Please Try Again");
            alert.showAndWait();
        }
    }

    public void onModCustomerCancel(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root = FXMLLoader.load(getClass().getResource("/view/Schedule.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

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
