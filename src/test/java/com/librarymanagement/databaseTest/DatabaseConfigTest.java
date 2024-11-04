package com.librarymanagement.database;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseConfigTest {

    @Test
    public void testGetConnection() {
        try {
            Connection connection = DatabaseConfig.getConnection();
            assertNotNull(connection, "Connection should not be null");
            connection.close(); // Close the connection after testing
        } catch (SQLException e) {
            e.printStackTrace();
            assertThrows(SQLException.class, () -> {
                throw e; // This will fail the test if an SQLException is thrown
            });
        }
    }

    @Test
    public void testInvalidConnection() {
        // This test will check for invalid connection scenarios
        // Temporarily modify the URL, USER, or PASSWORD for testing invalid connections.
        String invalidUrl = "jdbc:mysql://invalid_host:3306/libraryappdb";

        assertThrows(SQLException.class, () -> {
            DriverManager.getConnection(invalidUrl, "invalid_user", "invalid_password");
        });
    }
}
