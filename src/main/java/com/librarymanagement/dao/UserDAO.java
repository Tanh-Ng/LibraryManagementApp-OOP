package com.librarymanagement.dao;

import com.librarymanagement.model.*;
import com.librarymanagement.database.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Method to add a user to the database
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (name, password, user_type) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user instanceof Admin ? "Admin" : "NormalUser");

            // Execute the insert and retrieve the generated keys
            pstmt.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    user.setUserId(generatedId); // Set the generated ID on the user object
                } else {
                    throw new SQLException("Failed to retrieve the generated ID for user: " + user.getName());
                }
            }
        }
    }

    // Method to retrieve a user by ID
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String password = rs.getString("password"); // Plain text
                String userType = rs.getString("user_type");

                return userType.equals("Admin") ? new Admin(userId, name, password)
                        : new NormalUser(userId, name, password);
            } else {
                throw new SQLException("User with ID " + userId + " not found.");
            }
        } catch (SQLException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();
            throw new SQLException("Error retrieving user with ID " + userId, e);
        }
    }

    // Method to authenticate a user during login
    public User authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE name = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username); // Chỉ cần username

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retrieve user details
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String userType = rs.getString("user_type");

                // Return appropriate User object
                return userType.equals("Admin")
                        ? new Admin(userId, name, rs.getString("password"))
                        : new NormalUser(userId, name, rs.getString("password"));
            } else {
                // Invalid username
                throw new SQLException("Username not found.");
            }
        } catch (SQLException e) {
            //e.printStackTrace(); // Log the exception or handle it appropriately
            throw new SQLException("User with username " + username + " not found", e);
        }
    }


    //Method to get all user
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int userId = rs.getInt("user_id"); // Retrieve user ID
                String name = rs.getString("name"); // Retrieve user name
                String password = rs.getString("password"); // Retrieve user password
                String userType = rs.getString("user_type"); // Retrieve user type ("Admin" or "NormalUser")

                User user;
                if ("Admin".equalsIgnoreCase(userType)) {
                    user = new Admin(userId, name, password); // Create Admin object
                } else {
                    user = new NormalUser(userId, name, password); // Create NormalUser object
                }
                users.add(user); // Add user to the list
            }
        }
        return users;
    }

    //Method for updating user's password base on ID;
    public void changePassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword); // Plain text
            pstmt.setInt(2, userId); // Retrieve user ID

            //Execute the change of password
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();
            throw new SQLException("Error changing password for user with ID " + userId, e);
        }
    }

    //Method for updating user's name bade on ID;
    public void changeName(int userId, String newName) throws SQLException {
        String sql = "UPDATE Users SET name = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName); // Plain text
            pstmt.setInt(2, userId); // Retrieve user ID

            //Execute the change of name
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();
            throw new SQLException("Error changing name for user with ID " + userId, e);
        }
    }

    //Method for deleting user's base on ID;
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId); // Retrieve user ID

            //Execute the delete
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();
            throw new SQLException("Error deleting user with ID " + userId, e);
        }
    }

    // Method to change a user's ID
    public void changeUserId(int oldUserId, int newUserId) throws SQLException {
        // Get database connection
        Connection conn = DatabaseConfig.getConnection();
        try {
            // Start transaction
            conn.setAutoCommit(false);

            // Insert a new user record with the new user_id
            String insertSql = "INSERT INTO Users (user_id, name, password, user_type) " +
                    "SELECT ?, name, password, user_type FROM Users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, newUserId); // New user ID
                pstmt.setInt(2, oldUserId); // Old user ID to copy data from

                // Execute insert
                pstmt.executeUpdate();
            }

            // Delete the old user record
            String deleteSql = "DELETE FROM Users WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, oldUserId); // Old user ID to delete

                // Execute delete
                pstmt.executeUpdate();
            }

            // Commit transaction
            conn.commit();
        } catch (SQLException e) {
            // Rollback transaction in case of error
            conn.rollback();
            throw new SQLException("Error changing user ID from " + oldUserId + " to " + newUserId, e);
        } finally {
            // Restore default autocommit behavior and close connection
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    public boolean isUserExists(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE name = ?";
        Connection conn = DatabaseConfig.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    public boolean isIdExists(int Id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Connection conn = DatabaseConfig.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

}
