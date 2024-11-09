package service;

import DAO.ExchangeRateDAO;
import DAO.impl.ExchangeRateDAOImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import model.ExchangeTransaction;
import utils.Utils;
import utils.exceptions.MissingParameterException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static utils.Utils.validateParameter;

public class ExchangeRateService {
    public void getExchangeRates(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");

        String pathInfo = request.getPathInfo();
        try (Connection connection = DatabaseConnection.getConnection()) {
            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl(connection);

            if (pathInfo == null || pathInfo.equals("/")) {
                List<ExchangeRate> exchangeRates = exchangeRateDAO.getAllExchangeRates();
                String resultJson = Utils.convertExchangeRatesToJson(exchangeRates);
                response.getWriter().println(resultJson);
            } else {
                String currencyPair = pathInfo.substring(1);
                String baseCurrencyCode = currencyPair.substring(0, 3);
                String targetCurrencyCode = currencyPair.substring(3);
                ExchangeRate exchangeRate = exchangeRateDAO.getExchangeRate(baseCurrencyCode, targetCurrencyCode);

                if (exchangeRate != null) {
                    response.getWriter().println(exchangeRate.toJson());
                } else {
                    response.getWriter().println("{\"error\": \"Exchange rate not found for " + currencyPair + "\"}");
                }
            }
        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    public void addExchangeRate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl(connection);
            response.setContentType("application/json");
            try {
                String baseCurrencyCode = validateParameter(request, "baseCurrencyCode");
                String targetCurrencyCode = validateParameter(request, "targetCurrencyCode");
                String rateParam = validateParameter(request, "rate");

                double rate = Double.parseDouble(rateParam);


                boolean isAdded = exchangeRateDAO.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);

                if (isAdded) {
                    response.getWriter().println("{\"message\": \"Exchange rate added successfully\"}");
                } else {
                    response.getWriter().println("{\"error\": \"Unable to add exchange rate\"}");
                }
            } catch (MissingParameterException e) {
                response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
                e.printStackTrace();
            }

        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    public void updateExchangeRate(HttpServletRequest request, HttpServletResponse response) throws IOException {
         try (Connection connection = DatabaseConnection.getConnection()) {
            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl(connection);
            response.setContentType("application/json");

            String pathInfo = request.getPathInfo();
            String[] pathParts = pathInfo.split("/");

            if (pathParts.length != 2) {
                response.getWriter().println("{\"error\": \"Invalid request format\"}");
                return;
            }

            String currencyPair = pathParts[1];
            String baseCurrencyCode = currencyPair.substring(0, 3);
            String targetCurrencyCode = currencyPair.substring(3);

            double newRate = Double.parseDouble(request.getParameter("rate"));

            boolean isUpdated = exchangeRateDAO.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, newRate);

            if (isUpdated) {
                response.getWriter().println("{\"message\": \"Exchange rate updated successfully\"}");
            } else {
                response.getWriter().println("{\"error\": \"Exchange rate not found or invalid currency pair\"}");
            }

        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    public void exchangeCurrency(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("from");
        String targetCurrencyCode = request.getParameter("to");
        double amount = Double.parseDouble(request.getParameter("amount"));
        response.setContentType("application/json");
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAOImpl(connection);

            ExchangeRate exchangeRate = exchangeRateDAO.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            if (exchangeRate == null) {
                exchangeRate = exchangeRateDAO.getReverseExchangeRate(baseCurrencyCode, targetCurrencyCode);
            }

            if (exchangeRate != null) {
                double convertedAmount = amount * exchangeRate.getRate();
                ExchangeTransaction exchangeTransaction = new ExchangeTransaction(exchangeRate, amount, convertedAmount);
                String result = exchangeTransaction.toJson();

                response.getWriter().println(result);
            } else {
                response.getWriter().println("{\"error\": \"Currency pair not found\"}");
            }

        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
