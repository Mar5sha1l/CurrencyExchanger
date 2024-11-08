package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Currency {
    private final int id;
    private final String name;
    private final String code;
    private final String sign;

    public Currency(int id, String name, String code, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }
    public int getId() {
        return id;
    }
    public String getSign() {
        return sign;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Currency fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String code = rs.getString("code");
        String sign = rs.getString("sign");
        return new Currency(id, name, code, sign);
    }

    public static Currency fromResultSetWithPrefix(ResultSet rs, String prefix) throws SQLException {
        int id = rs.getInt(prefix + "CurrencyId");
        String name = rs.getString(prefix + "CurrencyName");
        String code = rs.getString(prefix + "CurrencyCode");
        String sign = rs.getString(prefix + "CurrencySign");
        return new Currency(id, name, code, sign);
    }

    public String toJson() {
        return "{" +
                "\"id\": " + getId() + ", " +
                "\"name\": \"" + getName() + "\", " +
                "\"code\": \"" + getCode() + "\", " +
                "\"sign\": \"" + getSign() + "\"" +
                "}";
    }
}
