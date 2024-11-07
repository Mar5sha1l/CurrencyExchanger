package service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class ServletService {
    public void getCurrencies(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConnectionDB db = new ConnectionDB();
        Connection connection = db.getConnection();

        String pathInfo = request.getPathInfo();

        response.setContentType("application/json");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Currencies");
                ResultSet rs = stmt.executeQuery();
                System.out.println(rs.toString());

                StringBuilder result = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        result.append(", ");
                    }
                    first = false;
                    Currency currency = Currency.fromResultSet(rs);
                    String currencyJson = getCurrencyJson(currency);

                    result.append(currencyJson);
                }

                result.append("]");
                response.getWriter().println(result);

            } else {
                String currencyCode = pathInfo.substring(1);
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Currencies WHERE code = ?");
                stmt.setString(1, currencyCode);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Currency currency = Currency.fromResultSet(rs);
                    String currencyJson = getCurrencyJson(currency);

                    response.getWriter().println(currencyJson);
                } else {
                    response.getWriter().println("{\"error\": \"Currency not found: " + currencyCode + "\"}");
                }
            }
        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"Database error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void getExchangeRates(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConnectionDB db = new ConnectionDB();
        Connection connection = db.getConnection();
        response.setContentType("application/json");

        String pathInfo = request.getPathInfo();

        String currencyPair = pathInfo.substring(1);
        String baseCurrencyCode = currencyPair.substring(0, 3);
        String targetCurrencyCode = currencyPair.substring(3);

        try {
            if (pathInfo.equals("/")) {
                String sql = """
                    SELECT er.id AS exchangeRateId, er.rate,
                           bc.ID AS baseCurrencyId, bc.name AS baseCurrencyName, bc.code AS baseCurrencyCode, bc.sign AS baseCurrencySign,
                           tc.ID AS targetCurrencyId, tc.name AS targetCurrencyName, tc.code AS targetCurrencyCode, tc.sign AS targetCurrencySign
                    FROM ExchangeRates er
                    JOIN Currencies bc ON er.baseCurrencyId = bc.ID
                    JOIN Currencies tc ON er.targetCurrencyId = tc.ID
                    """;

                PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                StringBuilder result = new StringBuilder("[");
                boolean first = true;
                while (rs.next()) {
                    if (!first) {
                        result.append(", ");
                    }
                    first = false;

                    exchangeRateResultBuilder(result, rs);
                }
                result.append("]");

                response.getWriter().println(result);
            } else {
                String sql = """
                    SELECT er.id AS exchangeRateId, er.rate,
                           bc.ID AS baseCurrencyId, bc.name AS baseCurrencyName, bc.code AS baseCurrencyCode, bc.sign AS baseCurrencySign,
                           tc.ID AS targetCurrencyId, tc.name AS targetCurrencyName, tc.code AS targetCurrencyCode, tc.sign AS targetCurrencySign
                    FROM ExchangeRates er
                    JOIN Currencies bc ON er.baseCurrencyId = bc.ID
                    JOIN Currencies tc ON er.targetCurrencyId = tc.ID
                    WHERE bc.code = ? AND tc.code = ?
                    """;

                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, baseCurrencyCode);
                stmt.setString(2, targetCurrencyCode);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    StringBuilder result = new StringBuilder("[");
                    exchangeRateResultBuilder(result, rs);
                    result.append("]");
                    response.getWriter().println(result);
                } else {
                    response.getWriter().println("{\"error\": \"Exchange not found: " + currencyPair + "\"}");
                }
            }
        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addNewCurrency(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConnectionDB db = new ConnectionDB();
        Connection connection = db.getConnection();
        Map<String, String> urlParam = getUrlParametersAsMap(request);

        String insertQuery = "INSERT INTO Currencies (name, sign, code) VALUES (?, ?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(insertQuery);
            stmt.setString(1, urlParam.get("name"));
            stmt.setString(2, urlParam.get("sign"));
            stmt.setString(3, urlParam.get("code"));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().println("Currency added successfully!");
            } else {
                response.getWriter().println("Failed to add currency.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        response.getWriter().println(urlParam);
    }

    public void addExchangeRate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConnectionDB db = new ConnectionDB();
        Connection connection = db.getConnection();
        response.setContentType("application/json");

        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        double rate = Double.parseDouble(request.getParameter("rate"));

        try {
            String sql = """
            INSERT INTO ExchangeRates (baseCurrencyId, targetCurrencyId, rate)
            SELECT bc.ID, tc.ID, ?
            FROM Currencies bc, Currencies tc
            WHERE bc.code = ? AND tc.code = ?
            """;

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setDouble(1, rate);
            stmt.setString(2, baseCurrencyCode);
            stmt.setString(3, targetCurrencyCode);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                response.getWriter().println("{\"message\": \"Exchange rate added successfully\"}");
            } else {
                response.getWriter().println("{\"error\": \"Unable to add exchange rate\"}");
            }

        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateExchangeRate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ConnectionDB db = new ConnectionDB();
        Connection connection = db.getConnection();
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

        try {
            String sql = """
                SELECT bc.ID AS baseCurrencyId, tc.ID AS targetCurrencyId
                FROM Currencies bc, Currencies tc
                WHERE bc.code = ? AND tc.code = ?
            """;

            PreparedStatement stmt = connection.prepareStatement(sql);
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

                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setDouble(1, newRate);
                updateStmt.setInt(2, baseCurrencyId);
                updateStmt.setInt(3, targetCurrencyId);

                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected > 0) {
                    response.getWriter().println("{\"message\": \"Exchange rate updated successfully\"}");
                } else {
                    response.getWriter().println("{\"error\": \"Exchange rate not found\"}");
                }
            } else {
                response.getWriter().println("{\"error\": \"Invalid currency pair\"}");
            }

        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void exchangeCurrency(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String baseCurrencyCode = request.getParameter("from");
        String targetCurrencyCode = request.getParameter("to");
        double amount = Double.parseDouble(request.getParameter("amount"));
        response.setContentType("application/json");

        ConnectionDB db = new ConnectionDB();
        Connection connection = db.getConnection();

        try {
            String sql = """
                SELECT bc.ID AS baseCurrencyId, bc.name AS baseCurrencyName, bc.code AS baseCurrencyCode, bc.sign AS baseCurrencySign,
                       tc.ID AS targetCurrencyId, tc.name AS targetCurrencyName, tc.code AS targetCurrencyCode, tc.sign AS targetCurrencySign,
                       er.rate
                FROM ExchangeRates er
                JOIN Currencies bc ON er.baseCurrencyId = bc.ID
                JOIN Currencies tc ON er.targetCurrencyId = tc.ID
                WHERE bc.code = ? AND tc.code = ?
            """;

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Currency baseCurrency = Currency.fromResultSet(rs, "base");
                Currency targetCurrency = Currency.fromResultSet(rs, "target");

                double rate = rs.getDouble("rate");

                double convertedAmount = amount * rate;

                String result = "{\"baseCurrency\":" +
                        getCurrencyJson(baseCurrency) +
                        ", " +
                        "\"targetCurrency\":" +
                        getCurrencyJson(targetCurrency) +
                        ", " + "\"rate\":" + rate + "," + "\"amount\":" + amount +
                        "," + "\"convertedAmount\":" + convertedAmount + "}";

                response.getWriter().println(result);
            } else {
                String reverseSql = """
                    SELECT bc.ID AS baseCurrencyId, bc.name AS baseCurrencyName, bc.code AS baseCurrencyCode, bc.sign AS baseCurrencySign,
                           tc.ID AS targetCurrencyId, tc.name AS targetCurrencyName, tc.code AS targetCurrencyCode, tc.sign AS targetCurrencySign,
                           er.rate
                    FROM ExchangeRates er
                    JOIN Currencies bc ON er.baseCurrencyId = bc.ID
                    JOIN Currencies tc ON er.targetCurrencyId = tc.ID
                    WHERE bc.code = ? AND tc.code = ?
                """;

                PreparedStatement reverseStmt = connection.prepareStatement(reverseSql);
                reverseStmt.setString(1, targetCurrencyCode);
                reverseStmt.setString(2, baseCurrencyCode);
                ResultSet reverseRs = reverseStmt.executeQuery();

                if (reverseRs.next()) {
                    double reverseRate = reverseRs.getDouble("rate");
                    double newRate = 1 / reverseRate;
                    double convertedAmount = amount * newRate;

                    response.getWriter().println("{\"message\": \"Exchange rate " + convertedAmount + "\"}" );
                } else {
                    response.getWriter().println("{\"error\": \"Currency pair not found\"}");
                }
            }
        } catch (SQLException e) {
            response.getWriter().println("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getCurrencyJson(Currency currency) {
        return "{" +
                "\"id\": " + currency.getId() + ", " +
                "\"name\": \"" + currency.getName() + "\", " +
                "\"code\": \"" + currency.getCode() + "\", " +
                "\"sign\": \"" + currency.getSign() + "\"" +
                "}";
    }

    private void exchangeRateResultBuilder(StringBuilder result, ResultSet rs) throws SQLException {
        result.append("{")
                .append("\"id\": ").append(rs.getInt("exchangeRateId")).append(", ")
                .append("\"baseCurrency\": ").append(getCurrencyJson(Currency.fromResultSet(rs, "base"))).append(", ")
                .append("\"targetCurrency\": ").append(getCurrencyJson(Currency.fromResultSet(rs, "target"))).append(", ")
                .append("\"rate\": ").append(rs.getDouble("rate"))
                .append("}");
    }

    private static Map<String, String> getUrlParametersAsMap(HttpServletRequest request) {
        return request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()[0]
                ));
    }
}
