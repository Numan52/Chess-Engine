package org.example.server.Database;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtils {
//    private static final String url = System.getProperty("DB_URL");
//    private static final String username = System.getProperty("DB_USERNAME");
//    private static final String password = System.getProperty("DB_PASSWORD");

    private static final String url = "jdbc:postgresql://localhost:5432/chess_engine";
    private static final String username = "postgres";
    private static final String password = "password";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        System.out.println("Connecting to database: " + url);
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Could not connect to the database: " + e.getMessage());
            throw e;
        }

    }
}
