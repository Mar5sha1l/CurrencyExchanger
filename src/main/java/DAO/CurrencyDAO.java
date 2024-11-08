package DAO;

import model.Currency;

import java.sql.SQLException;
import java.util.List;

public interface CurrencyDAO {
    Currency getCurrency(String currency) throws SQLException;
    List<Currency> getAllCurrencies() throws SQLException;
    boolean addCurrency(String name, String sign, String code) throws SQLException;
}
