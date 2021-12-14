package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.ZoneId;

public class CountryDAO {

    private static Connection conn = DBManager.getConnection();

    private static ZoneId utc = ZoneId.of("UTC");
    private static ObservableList<Country> allCountries = FXCollections.observableArrayList();

    /**
     * A SQL statement is assigned to a String variable and the allCountries observable list is cleared to ensure correct data.
     * <p>
     *     The statement is executed and while rs.next() is true, a new Country is created out of the values found in the
     *     columns of the database. These are added one by one to allCountries and returned.
     * </p>
     *
     * @return - an observable list containing allCountries in the database.
     */
    public static ObservableList<Country> getAllCountries() {
        String sqlStatement = "SELECT * FROM countries";
        allCountries.clear();
        try {
            DBQuery.setStatement(conn);
            Statement statement = DBQuery.getStatement();
            statement.execute(sqlStatement);
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                Country c = new Country();
                c.setCountryId(rs.getInt("Country_ID"));
                c.setCountryName(rs.getString("Country"));
                c.setCreateDate(rs.getTimestamp("Create_Date").toLocalDateTime());
                c.setCreatedBy(rs.getString("Created_By"));
                c.setLastUpdate(rs.getTimestamp("Last_Update").toLocalDateTime());
                c.setLastUpdateBy(rs.getString("Last_Updated_By"));
                allCountries.add(c);
            }
        }
        catch (Exception e) {
            // do nothing.
        }
        return allCountries;
    }

    /**
     * Checks every Country's name against string specified in parameter. If a match is found, the User is returned.
     * @param s - String, name of desired Country.
     * @return desired Country.
     */
    public static Country getCountryByName(String s) {
        for (Country c : allCountries) {
            if (c.getCountryName().equals(s)) {
                return c;
            }
        }
        return null;
    }
}
