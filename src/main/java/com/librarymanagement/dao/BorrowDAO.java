package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Borrow;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    // Method to add a borrow record, with checks for user and document existence
    public void addBorrow(int userId, int documentId, Date borrowDate) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {

            // Check if the user exists
            if (!userExists(userId, conn)) {
                throw new SQLException("User ID " + userId + " not found.");
            }

            // Check if the document exists
            if (!documentExists(documentId, conn)) {
                throw new SQLException("Document ID " + documentId + " not found.");
            }

            // Insert the borrow record if checks pass
            String sql = "INSERT INTO BorrowedDocuments (user_id, document_id, borrow_date) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, documentId);
                pstmt.setDate(3, borrowDate);
                pstmt.executeUpdate();
            }
        }
    }

    // Method to check if a user exists
    private boolean userExists(int userId, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM Users WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // Method to check if a document exists
    private boolean documentExists(int documentId, Connection conn) throws SQLException {
        String sql = "SELECT 1 FROM Documents WHERE document_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, documentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Borrow> getBorrowedDocumentsByUser(int userId) throws SQLException {
        String sql = "SELECT borrow_id, user_id, document_id, borrow_date, duration_days, extend_duration_request FROM BorrowedDocuments WHERE user_id = ?";
        List<Borrow> borrowedDocuments = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Retrieve all necessary fields from the result set
                    Borrow borrow = new Borrow(
                            rs.getInt("borrow_id"),
                            rs.getInt("user_id"),
                            rs.getInt("document_id"),
                            rs.getTimestamp("borrow_date"),
                            rs.getInt("duration_days"),  // Get the duration_days field
                            rs.getBoolean("extend_duration_request") // Get the extend_duration_request field
                    );
                    borrowedDocuments.add(borrow);
                }
            }
        }
        return borrowedDocuments;
    }

    // Method to retrieve a borrow record by its ID
    public Borrow getBorrowById(int borrowId) throws SQLException {
        String sql = "SELECT borrow_id, user_id, document_id, borrow_date, duration_days, extend_duration_request FROM BorrowedDocuments WHERE borrow_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, borrowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Return a Borrow object with all necessary fields
                    return new Borrow(
                            rs.getInt("borrow_id"),
                            rs.getInt("user_id"),
                            rs.getInt("document_id"),
                            rs.getTimestamp("borrow_date"),
                            rs.getInt("duration_days"),  // Get the duration_days field
                            rs.getBoolean("extend_duration_request") // Get the extend_duration_request field
                    );
                }
            }
        }
        return null; // Return null if not found
    }
    // Method to delete a borrow record by its ID
    public void deleteBorrow(int borrowId) throws SQLException {
        String sql = "DELETE FROM BorrowedDocuments WHERE borrow_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, borrowId);
            pstmt.executeUpdate();
        }
    }

    public int getTotalBorrowedDocuments() throws SQLException {
        String sql = "SELECT COUNT(*) AS total FROM BorrowedDocuments";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    public List<Borrow> getAllBorrowedDocuments() throws SQLException {
        String sql = "SELECT borrow_id, user_id, document_id, borrow_date, duration_days, extend_duration_request FROM BorrowedDocuments";
        List<Borrow> borrowedDocuments = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Create Borrow object with the additional fields
                Borrow borrow = new Borrow(
                        rs.getInt("borrow_id"),
                        rs.getInt("user_id"),
                        rs.getInt("document_id"),
                        rs.getTimestamp("borrow_date"),
                        rs.getInt("duration_days"),  // Retrieve duration_days
                        rs.getBoolean("extend_duration_request") // Retrieve extend_duration_request flag
                );
                borrowedDocuments.add(borrow);
            }
        }
        return borrowedDocuments;
    }

    public int getBorrowIdByUserAndDocument(int userId, int documentId) throws SQLException {
        String sql = "SELECT borrow_id FROM BorrowedDocuments WHERE user_id = ? AND document_id = ?";
        int borrowId = -1; // Default to -1, indicating no result found

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId); // Set user_id parameter
            pstmt.setInt(2, documentId); // Set document_id parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    borrowId = rs.getInt("borrow_id"); // Retrieve the borrow_id if it exists
                }
            }
        } catch (SQLException e) {
            // Log the exception for debugging purposes
            System.err.println("Error retrieving borrow_id: " + e.getMessage());
            throw e; // Re-throw the exception after logging it
        }

        return borrowId; // Return the borrow_id or -1 if no matching record found
    }

    public void setExtendDurationRequest(int borrowId, boolean request) throws SQLException {
        String sql = "UPDATE BorrowedDocuments SET extend_duration_request = ? WHERE borrow_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, request);  // Set the extend_duration_request flag
            pstmt.setInt(2, borrowId);     // Specify the borrow record
            pstmt.executeUpdate();
        }
    }

    public void updateBorrowDuration(int borrowId, int additionalDays) throws SQLException {
        // Extend the borrow duration (without modifying the request flag)
        String sql = "UPDATE BorrowedDocuments SET duration_days = duration_days + ? WHERE borrow_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, additionalDays);  // Increase the duration
            pstmt.setInt(2, borrowId);         // Specify the borrow record
            pstmt.executeUpdate();
        }

        // After the duration is updated, set extend_duration_request to false
        String updateExtendRequestSql = "UPDATE BorrowedDocuments SET extend_duration_request = FALSE WHERE borrow_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateExtendRequestSql)) {
            pstmt.setInt(1, borrowId);  // Specify the borrow record
            pstmt.executeUpdate();
        }
    }

    public void deleteExpiredBorrow() throws SQLException {
        String sql = "DELETE FROM BorrowedDocuments WHERE (borrow_date + INTERVAL duration_days DAY) < NOW()";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        }
    }

}
