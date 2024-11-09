package DAO.impl;

import DAO.CurrencyDAO;
import model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDAOImpl implements CurrencyDAO {
    private final Connection connection;

    public CurrencyDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Currency getCurrency(String currency) throws SQLException {
        String sql = "SELECT * FROM Currencies WHERE code = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, currency);
        ResultSet rs = statement.executeQuery();
        if(rs.next()) {
            return Currency.fromResultSet(rs);
        }
        return null;
    }

    @Override
    public List<Currency> getAllCurrencies() throws SQLException {
        String sql = "SELECT * FROM Currencies";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet rs = statement.executeQuery();
        List<Currency> currencies = new ArrayList<>();
        while(rs.next()) {
            currencies.add(Currency.fromResultSet(rs));
        }
        return currencies;
    }

    @Override
    public boolean addCurrency(String name, String sign, String code) throws SQLException {
        String insertQuery = "INSERT INTO Currencies (name, sign, code) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, name);
            stmt.setString(2, sign);
            stmt.setString(3, code);
            return stmt.executeUpdate() > 0;
        }
    }
}
