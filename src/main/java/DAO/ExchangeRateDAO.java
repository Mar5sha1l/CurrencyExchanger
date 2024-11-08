package DAO;

import model.ExchangeRate;

import java.sql.SQLException;
import java.util.List;

public interface ExchangeRateDAO {
    boolean addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) throws SQLException;
    boolean updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double newRate) throws SQLException;
    ExchangeRate getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;
    ExchangeRate getReverseExchangeRate(String baseCurrencyCode, String targetCurrencyCode) throws SQLException;
    List<ExchangeRate> getAllExchangeRates() throws SQLException;
}
