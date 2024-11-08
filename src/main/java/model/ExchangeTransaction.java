package model;

public class ExchangeTransaction {
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private final double rate;
    private final double amount;
    private final double convertedAmount;

    public ExchangeTransaction(ExchangeRate rate, double amount, double convertedAmount) {
        this.baseCurrency = rate.getBaseCurrency();
        this.targetCurrency = rate.getTargetCurrency();
        this.rate = rate.getRate();
        this.amount = amount;
        this.convertedAmount = convertedAmount;
    }

    public String toJson() {
        return "{" +
                "\"baseCurrency\": " + baseCurrency.toJson() + ", " +
                "\"targetCurrency\": " + targetCurrency.toJson() + ", " +
                "\"rate\": " + rate + ", " +
                "\"amount\": " + amount + ", " +
                "\"convertedAmount\": " + convertedAmount +
                "}";
    }
}
