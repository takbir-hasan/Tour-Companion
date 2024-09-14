package com.example.tour;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String JDBC_URL = "jdbc:mysql://tourproject.cvxui31tcden.eu-north-1.rds.amazonaws.com:3306/tourProject";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "11112222";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Connected to the database!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to connect to the database.");
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
