package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Division;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DivisionDAO {

    private static Connection conn = DBManager.getConnection();

    private static ObservableList<Division> allDivs = FXCollections.observableArrayList();

    /**
     * A SQL statement is assigned to a String variable and the allDivs observable list is cleared to ensure correct data.
     * <p>
     *     The statement is executed and while rs.next() is true, a new Division is created out of the values found in the
     *     columns of the database. These are added one by one to allDivs and returned.
     * </p>
     *
     * @return - an observable list containing allDivs in the database.
     */
    public static ObservableList<Division> getAllDivs() {
        String sql = "SELECT * FROM first_level_divisions";
        allDivs.clear();
        try {
            DBQuery.setStatement(conn);
            Statement statement = DBQuery.getStatement();
            statement.execute(sql);
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                Division d = new Division();
                d.setDivId(rs.getInt("Division_ID"));
                d.setDivName(rs.getString("Division"));
                d.setCountryId(rs.getInt("Country_ID"));
                allDivs.add(d);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return allDivs;
    }

    /**
     * Checks every Division's name against string specified in parameter. If a match is found, the Division is returned.
     * @param s - String, name of desired Division.
     * @return desired Division.
     */
    public static int getDivIdFromName(String s) {
        allDivs = getAllDivs();
        for (Division d : allDivs) {
            if (d.getDivName().equals(s)) {
                return d.getDivId();
            }
        }
        return 0;
    }
}
