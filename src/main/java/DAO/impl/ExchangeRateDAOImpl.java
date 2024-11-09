package DAO.impl;

import DAO.ExchangeRateDAO;
import model.ExchangeRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDAOImpl implements ExchangeRateDAO {
    private final Connection connection;

    public ExchangeRateDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException {
        String sql = """
            INSERT INTO ExchangeRates (baseCurrencyId, targetCurrencyId, rate)
            SELECT bc.ID, tc.ID, ?
            FROM Currencies bc, Currencies tc
            WHERE bc.code = ? AND tc.code = ?
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, rate);
            stmt.setString(2, baseCurrencyCode);
            stmt.setString(3, targetCurrencyCode);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double newRate) throws SQLException {
        String selectSql = """
            SELECT bc.ID AS baseCurrencyId, tc.ID AS targetCurrencyId
            FROM Currencies bc, Currencies tc
            WHERE bc.code = ? AND tc.code = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int baseCurrencyId = rs.getInt("baseCurrencyId");
                int targetCurrencyId = rs.getInt("targetCurrencyId");

                String updateSql = """
                    UPDATE ExchangeRates
                    SET rate = ?
                    WHERE baseCurrencyId = ? AND targetCurrencyId = ?
                    """;

                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, newRate);
                    updateStmt.setInt(2, baseCurrencyId);
                    updateStmt.setInt(3, targetCurrencyId);
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                return false;
            }
        }
    }
    @Override
    public ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        String sql = """
        SELECT er.id AS exchangeRateId, bc.ID AS baseCurrencyId, bc.name AS baseCurrencyName, bc.code AS baseCurrencyCode, bc.sign AS baseCurrencySign,
               tc.ID AS targetCurrencyId, tc.name AS targetCurrencyName, tc.code AS targetCurrencyCode, tc.sign AS targetCurrencySign,
               er.rate
        FROM ExchangeRates er
        JOIN Currencies bc ON er.baseCurrencyId = bc.ID
        JOIN Currencies tc ON er.targetCurrencyId = tc.ID
        WHERE bc.code = ? AND tc.code = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ExchangeRate.fromResultSet(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error fetching exchange rate for " + baseCurrencyCode + " to " + targetCurrencyCode, e);
        }
    }

    @Override
    public ExchangeRate getReverseExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException {
        return getExchangeRate(targetCurrencyCode, baseCurrencyCode);
    }

    @Override
    public List<ExchangeRate> getAllExchangeRates() throws SQLException {
        String sql = """
            SELECT er.id AS exchangeRateId, er.rate,
                   bc.ID AS baseCurrencyId, bc.name AS baseCurrencyName, bc.code AS baseCurrencyCode, bc.sign AS baseCurrencySign,
                   tc.ID AS targetCurrencyId, tc.name AS targetCurrencyName, tc.code AS targetCurrencyCode, tc.sign AS targetCurrencySign
            FROM ExchangeRates er
            JOIN Currencies bc ON er.baseCurrencyId = bc.ID
            JOIN Currencies tc ON er.targetCurrencyId = tc.ID
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (rs.next()) {
                exchangeRates.add(ExchangeRate.fromResultSet(rs));
            }
            return exchangeRates;
        }
    }
}
