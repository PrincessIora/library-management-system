package com.manage.lms.library.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost/lms";

    public Connection connect(String username, String password)
            throws SQLException {

        return DriverManager.getConnection(
                URL,
                username,
                password
        );
    }
}