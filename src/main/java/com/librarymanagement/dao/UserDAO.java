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

    }

    //Method for updating user's name bade on ID;
    public void changeName(int userId, String newName) throws SQLException {

    }

    //Method for deleting user's base on ID;
    public void deleteUser(int userId) throws SQLException {

    }
}
