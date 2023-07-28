package com.example.testproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    public static final String DB_NAME = "/Users/kennyhector/Desktop/Web-Development/TestDBProject/database/faq_wizard.db";
    public static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private Connection databaseLink;

    public Connection getConnection() {
        try {
            if (databaseLink == null || databaseLink.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                databaseLink = DriverManager.getConnection(DB_URL);
                createTableIfNotExists();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return databaseLink;
    }

    private void createTableIfNotExists() {
        try {
            Statement statement = databaseLink.createStatement();
            String createTableQuery = "CREATE TABLE IF NOT EXISTS faq_table ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "question TEXT NOT NULL,"
                    + "answer TEXT NOT NULL"
                    + ")";
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
