package br.edu.bsi.biblioteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL =
        "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "root";
    private static final String PASS = "Mysql@2026"; // ajuste

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
