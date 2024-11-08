package service;

import DAO.CurrencyDAO;
import DAO.impl.CurrencyDAOImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import utils.Utils;

import java.util.Map;


public class CurrencyService {
    public void getCurrencies(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String pathInfo = request.getPathInfo();

        try (Connection connection = DatabaseConnection.getConnection()) {
            CurrencyDAO currencyDAO = new CurrencyDAOImpl(connection);
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Currency> currencies = currencyDAO.getAllCurrencies();
                StringBuilder resultJson = new StringBuilder("[");
                for (int i = 0; i < currencies.size(); i++) {
                    Currency currency = currencies.get(i);
                    resultJson.append(currency.toJson());
                    if (i < currencies.size() - 1) {
                        resultJson.append(",\n");
                    }
                }
                resultJson.append("]");

                response.getWriter().println(resultJson);
            } else {
                String currencyCode = pathInfo.substring(1);
                Currency currency = currencyDAO.getCurrency(currencyCode);

                if (currency != null) {
                    response.getWriter().println(currency.toJson());
                } else {
                    response.getWriter().println("{\"error\": \"Currency not found: " + currencyCode + "\"}");
                }
            }
        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"Database error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    public void addNewCurrency(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        Map<String, String> urlParam = Utils.getUrlParametersAsMap(request);

        try (Connection connection = DatabaseConnection.getConnection()){
            CurrencyDAO currencyDAO = new CurrencyDAOImpl(connection);

            boolean isAdded = currencyDAO.addCurrency(
                    urlParam.get("name"),
                    urlParam.get("sign"),
                    urlParam.get("code")
            );
            if (isAdded) {
                response.getWriter().println("Currency added successfully!");
            } else {
                response.getWriter().println("Failed to add currency.");
            }
        } catch (SQLException e) {
            response.getWriter().println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        response.getWriter().println(urlParam);
    }
}
