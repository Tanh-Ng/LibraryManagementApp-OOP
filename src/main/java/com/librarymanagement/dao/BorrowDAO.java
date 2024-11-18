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

    // Method to retrieve borrowed documents by user ID
    public List<Borrow> getBorrowedDocumentsByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM BorrowedDocuments WHERE user_id = ?";
        List<Borrow> borrowedDocuments = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Borrow borrow = new Borrow(rs.getInt("borrow_id"), rs.getInt("user_id"), rs.getInt("document_id"), rs.getDate("borrow_date"));
                    borrowedDocuments.add(borrow);
                }
            }
        }
        return borrowedDocuments;
    }

    // Method to retrieve a borrow record by its ID
    public Borrow getBorrowById(int borrowId) throws SQLException {
        String sql = "SELECT * FROM BorrowedDocuments WHERE borrow_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, borrowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Borrow(rs.getInt("borrow_id"), rs.getInt("user_id"), rs.getInt("document_id"), rs.getDate("borrow_date"));
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
}
