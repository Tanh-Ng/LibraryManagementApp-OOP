package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Borrow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BorrowDAOTest {

    private BorrowDAO borrowDAO;
    private static final int DUMMY_USER_ID = 1;
    private static final int DUMMY_DOCUMENT_ID = 1;

    @BeforeEach
    void setUp() throws SQLException {
        borrowDAO = new BorrowDAO();
        insertDummyData();  // Insert dummy data for `Users` and `Documents`
    }

    // Method to insert dummy data for testing
    private void insertDummyData() throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Insert a dummy user if not exists
            String insertUser = "INSERT IGNORE INTO Users (user_id, name, password) VALUES (?, 'Dummy User', 'password')";
            try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
                pstmt.setInt(1, DUMMY_USER_ID);
                pstmt.executeUpdate();
            }

            // Insert a dummy document if not exists
            String insertDocument = "INSERT IGNORE INTO Documents (document_id, title, author, is_available) VALUES (?, 'Dummy Document', 'Author', 1)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertDocument)) {
                pstmt.setInt(1, DUMMY_DOCUMENT_ID);
                pstmt.executeUpdate();
            }
        }
    }

    @Test
    void testAddBorrow_UserNotFound() {
        int invalidUserId = 999; // User ID that doesn't exist
        int documentId = DUMMY_DOCUMENT_ID;
        Date borrowDate = new Date(System.currentTimeMillis());

        SQLException exception = assertThrows(SQLException.class, () -> {
            borrowDAO.addBorrow(invalidUserId, documentId, borrowDate);
        });
        assertTrue(exception.getMessage().contains("User ID " + invalidUserId + " not found."));
    }

    @Test
    void testAddBorrow_DocumentNotFound() {
        int userId = DUMMY_USER_ID;
        int invalidDocumentId = 999; // Document ID that doesn't exist
        Date borrowDate = new Date(System.currentTimeMillis());

        SQLException exception = assertThrows(SQLException.class, () -> {
            borrowDAO.addBorrow(userId, invalidDocumentId, borrowDate);
        });
        assertTrue(exception.getMessage().contains("Document ID " + invalidDocumentId + " not found."));
    }

    @Test
    void testAddBorrow_Successful() throws SQLException {
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        Date borrowDate = new Date(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowDate);

        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        assertFalse(borrowedDocuments.isEmpty(), "Borrowed documents should not be empty after adding a borrow.");
        assertEquals(documentId, borrowedDocuments.get(0).getDocumentId(), "Document ID should match the added borrow.");
    }

    @Test
    void testGetBorrowById() throws SQLException {
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        Date borrowDate = new Date(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowDate);

        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        Borrow borrow = borrowDAO.getBorrowById(borrowedDocuments.get(0).getBorrowId());

        assertNotNull(borrow, "Borrow should not be null.");
        assertEquals(userId, borrow.getUserId(), "User ID should match.");
    }

    @Test
    void testDeleteBorrow() throws SQLException {
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        Date borrowDate = new Date(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowDate);

        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        int borrowId = borrowedDocuments.get(0).getBorrowId();

        borrowDAO.deleteBorrow(borrowId);

        Borrow deletedBorrow = borrowDAO.getBorrowById(borrowId);
        assertNull(deletedBorrow, "Borrow should be null after deletion.");
    }

    @AfterEach
    void afterEachTest() throws SQLException {
        clearTable(); // Ensures table is cleared after each test as well
    }

    void clearTable() throws SQLException {
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM BorrowedDocuments");
            stmt.execute("DELETE FROM Documents");
            stmt.execute("DELETE FROM Users"); // Clears Documents table before each test
        }
    }
}
