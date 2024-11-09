package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExchangeRate {
    private final int id;
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final double rate;

    public ExchangeRate(int id, Currency baseCurrency, Currency targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public static ExchangeRate fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("exchangeRateId");
        Currency baseCurrency = Currency.fromResultSetWithPrefix(rs, "base");
        Currency targetCurrency = Currency.fromResultSetWithPrefix(rs, "target");
        double rate = rs.getDouble("rate");

        return new ExchangeRate(id, baseCurrency, targetCurrency, rate);
    }

    public String toJson() {
        return "{" +
                "\"id\": " + getId() + ", " +
                "\"baseCurrency\": " + getBaseCurrency().toJson() + ", " +
                "\"targetCurrency\": " + getTargetCurrency().toJson() + ", " +
                "\"rate\": " + getRate() +
                "}";
    }
}
