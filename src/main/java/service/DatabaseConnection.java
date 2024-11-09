package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if(connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                String url = "jdbc:sqlite:C:\\Govno_Projects\\CurrencyExchanger\\exchangerdb.db";
                connection = DriverManager.getConnection(url);
                System.out.println("Database connection established");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    public static void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}