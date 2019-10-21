package org.khomenko.maga.db;

import java.sql.*;

public class DbMain {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:11111:ORCLCDB", "rk", "rk");

            Statement stmt = connection.createStatement();
            String query = "select REGION, COUNTRY, CITY_NAME from CITY";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println(rs.getString("REGION") + " "
                        + rs.getString("COUNTRY") + " "
                        + rs.getString("CITY_NAME"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
