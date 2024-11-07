import service.ConnectionDB;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        ConnectionDB db = new ConnectionDB();
        Connection con = db.getConnection();
        if(con != null) {
            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from Currencies");

                while (rs.next()) {
                    int id = rs.getInt("ID");
                    String code = rs.getString("Code");
                    String name = rs.getString("FullName");
                    String Sign = rs.getString("Sign");

                    // Выводим данные
                    System.out.println("ID: " + id + ", Code: " + code + ", Name: " + name + ", Sign: " + Sign);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
