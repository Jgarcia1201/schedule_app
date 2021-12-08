package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;
import model.Division;
import utility.DBManager;
import utility.DBQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DivisionDAO {

    private static Connection conn = DBManager.getConnection();

    private static ObservableList<Division> allDivs = FXCollections.observableArrayList();

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
