package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Currency {
    private int id;
    private String name;
    private String code;
    private String sign;
    public Currency(int id, String name, String code, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }
    public Currency() {}

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

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static Currency fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String code = rs.getString("code");
        String sign = rs.getString("sign");
        return new Currency(id, name, code, sign);
    }

    public static Currency fromResultSet(ResultSet rs, String prefix) throws SQLException {
        int id = rs.getInt(prefix + "CurrencyId");
        String name = rs.getString(prefix + "CurrencyName");
        String code = rs.getString(prefix + "CurrencyCode");
        String sign = rs.getString(prefix + "CurrencySign");
        return new Currency(id, name, code, sign);
    }
}
