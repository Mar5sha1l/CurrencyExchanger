package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    static {
        try {
            String url = "jdbc:sqlite:/C:/Govno_Projects/CurrencyExchanger/exchangerdb.db";
            connection = DriverManager.getConnection(url);
            System.out.println("Database connection established");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return connection;
    }

    public static void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}