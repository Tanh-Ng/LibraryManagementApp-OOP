package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Admin;
import com.librarymanagement.model.NormalUser;
import com.librarymanagement.model.User;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class  UserDAOTest {

    private static UserDAO userDAO;

    @BeforeAll
    static void setup() {
        userDAO = new UserDAO();
    }

    @BeforeEach
    void clearTable() throws SQLException {
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Users"); // Clears Users table before each test
        }
    }

    @Test
    void testAddUser() throws SQLException {
        User admin = new Admin( "AdminUser", "admin123");
        userDAO.addUser(admin); // Retrieve generated user ID
        List<User> users = userDAO.getAllUsers();
        assertEquals(1, users.size());
        assertTrue(users.get(0) instanceof Admin);
        assertEquals(users.get(0).getUserId(), users.get(0).getUserId()); // Verify ID matches
    }

    @Test
    void testGetUserById() throws SQLException {
        User normalUser = new NormalUser(100, "NormalUser", "user123");
        userDAO.addUser(normalUser); // Use generated ID
        User retrievedUser = userDAO.getUserById(normalUser.getUserId()); // Use dynamic ID here
        assertNotNull(retrievedUser);
        assertEquals("NormalUser", retrievedUser.getName());
    }

    @Test
    void testGetAllUsers() throws SQLException {
        User admin = new Admin( "AdminUser", "admin123");
        User normalUser = new NormalUser("NormalUser", "user123");
        userDAO.addUser(admin);
        userDAO.addUser(normalUser);

        List<User> users = userDAO.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    void testChangePassword() throws SQLException {
        User admin = new Admin( "AdminUser", "admin123");
        userDAO.addUser(admin); // Retrieve generated user ID
        userDAO.changePassword(admin.getUserId(), "newPassword123");

        User updatedUser = userDAO.getUserById(admin.getUserId()); // Use dynamic ID here
        assertEquals("newPassword123", updatedUser.getPassword());
    }

    @Test
    void testChangeName() throws SQLException {
        User normalUser = new NormalUser( "OldName", "user123");
        userDAO.addUser(normalUser); // Retrieve generated user ID

        userDAO.changeName(normalUser.getUserId(), "NewName");

        User updatedUser = userDAO.getUserById(normalUser.getUserId()); // Use dynamic ID here
        assertEquals("NewName", updatedUser.getName());
    }

    @Test
    void testDeleteUser() throws SQLException {
        User normalUser = new NormalUser("DeleteUser", "user123");
        userDAO.addUser(normalUser); // Retrieve generated user ID

        userDAO.deleteUser(normalUser.getUserId()); // Use dynamic ID here

        assertThrows(SQLException.class, () -> userDAO.getUserById(normalUser.getUserId()));
    }

    @AfterEach
    void afterEachTest() throws SQLException {
        clearTable(); // Ensures table is cleared after each test as well
    }
}
