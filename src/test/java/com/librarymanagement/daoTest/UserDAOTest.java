package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Admin;
import com.librarymanagement.model.NormalUser;
import com.librarymanagement.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        clearUsersTable();
    }

    @AfterEach
    public void tearDown() {
        // Optional: Clean up the database after each test to maintain a clean state.
        clearUsersTable();
    }

    private void clearUsersTable() {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Documents")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testAddNormalUser() throws SQLException {
        NormalUser user = new NormalUser("Test User", "password123");
        userDAO.addUser(user);
        List<User> Users=();
        // Attempt to retrieve the user to confirm it was added
        User retrievedUser = userDAO.getUserById(0);
        assertNotNull(retrievedUser);
        assertEquals("Test User", retrievedUser.getName());
        assertEquals("password123", retrievedUser.getPassword());
        assertTrue(retrievedUser instanceof NormalUser);
    }

    @Test
    public void testAddAdminUser() throws SQLException {
        Admin user = new Admin("Admin User", "adminPassword");
        userDAO.addUser(user);

        // Attempt to retrieve the user to confirm it was added
        User retrievedUser = userDAO.getUserById(0);
        assertNotNull(retrievedUser);
        assertEquals("Admin User", retrievedUser.getName());
        assertEquals("adminPassword", retrievedUser.getPassword());
        assertTrue(retrievedUser instanceof Admin);
    }

    @Test
    public void testGetUserByIdNotFound() {
        // Check if an exception is thrown when trying to get a non-existent user
        assertThrows(SQLException.class, () -> {
            userDAO.getUserById(99999); // Assuming this ID does not exist
        });
    }

    @Test
    public void testAddUserInvalidData() {
        // Example of handling invalid data (e.g., null name)
        assertThrows(SQLException.class, () -> {
            NormalUser invalidUser = new NormalUser(null, "password");
            userDAO.addUser(invalidUser);
        });
    }
}
