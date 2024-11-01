package com.librarymanagement.dao;

import com.librarymanagement.model.User;
import com.librarymanagement.model.Admin;
import com.librarymanagement.model.NormalUser;
import com.librarymanagement.database.DatabaseConfig;

import java.sql.*;

public class UserDAO {

    // Method to add a user to the database
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (name, password, user_type) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPassword()); // No encryption
            pstmt.setString(3, user instanceof Admin ? "Admin" : "NormalUser");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // Log the exception or handle it appropriately
            e.printStackTrace();
            throw new SQLException("Error adding user: " + user.getName(), e);
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
