package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    private Connection connection;

    public ConnectionDB() {
        String url = "jdbc:sqlite:/C:/Govno_Projects/CurrencyExchanger/exchangerdb.db";

        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(url);
            System.out.println("Database connection established");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
