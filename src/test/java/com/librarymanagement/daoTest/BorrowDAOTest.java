package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Borrow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
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
        Timestamp borrowDate = new Timestamp(System.currentTimeMillis());

        SQLException exception = assertThrows(SQLException.class, () -> {
            borrowDAO.addBorrow(invalidUserId, documentId, borrowDate,1);
        });
        assertTrue(exception.getMessage().contains("User ID " + invalidUserId + " not found."));
    }

    @Test
    void testAddBorrow_DocumentNotFound() {
        int userId = DUMMY_USER_ID;
        int invalidDocumentId = 999; // Document ID that doesn't exist
        java.sql.Timestamp borrowTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
        int duration = 1;

        SQLException exception = assertThrows(SQLException.class, () -> {
            borrowDAO.addBorrow(userId, invalidDocumentId, borrowTimestamp, duration);
        });
        assertTrue(exception.getMessage().contains("Document ID " + invalidDocumentId + " not found."));
    }


    @Test
    void testAddBorrow_Successful() throws SQLException {
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        java.sql.Timestamp borrowTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
        int duration = 1; // Replace with an appropriate value

        borrowDAO.addBorrow(userId, documentId, borrowTimestamp, duration);

        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        assertFalse(borrowedDocuments.isEmpty(), "Borrowed documents should not be empty after adding a borrow.");
        assertEquals(documentId, borrowedDocuments.get(0).getDocumentId(), "Document ID should match the added borrow.");
    }

    @Test
    void testGetBorrowById() throws SQLException {
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        java.sql.Timestamp borrowTimestamp = new java.sql.Timestamp(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowTimestamp,1);

        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        Borrow borrow = borrowDAO.getBorrowById(borrowedDocuments.get(0).getBorrowId());

        assertNotNull(borrow, "Borrow should not be null.");
        assertEquals(userId, borrow.getUserId(), "User ID should match.");
    }

    @Test
    void testDeleteBorrow() throws SQLException {
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        java.sql.Timestamp borrowTimestamp = new java.sql.Timestamp(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowTimestamp,1);

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


    @Test
    void testUpdateBorrowDuration() throws SQLException {
        // Insert a borrow record with default duration of 1 day
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        java.sql.Timestamp borrowTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
        int request = 5;
        borrowDAO.addBorrow(userId, documentId, borrowTimestamp,1);

        // Get the borrow ID
        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        int borrowId = borrowedDocuments.get(0).getBorrowId();

        // Check the initial state of the extend_duration_request flag (it should be false initially)
        Borrow borrowBeforeUpdate = borrowDAO.getBorrowById(borrowId);
        assertEquals(borrowBeforeUpdate.getExtendDurationRequest(),0, "Initially, the extend_duration_request should be 0.");

        // Set the extend_duration_request to true (this simulates a user requesting an extension)
        borrowDAO.setExtendDurationRequest(borrowId,request);

        // Verify that the extend_duration_request flag is true after the request
        Borrow borrowAfterRequest = borrowDAO.getBorrowById(borrowId);
        assertNotEquals(borrowAfterRequest.getExtendDurationRequest(),0, "After the request, the extend_duration_request should not be 0.");

        // Extend the borrow duration by 5 days and process the request
        borrowDAO.updateBorrowDuration(borrowId, request);

        // Fetch the updated borrow record
        Borrow updatedBorrow = borrowDAO.getBorrowById(borrowId);

        // Verify the duration has increased by 5 days (default duration is 1 day, so it should now be 6 days)
        assertEquals(6, updatedBorrow.getDurationDays(), "The borrow duration should be extended by "+request+" days.");

        // Verify that the extend_duration_request flag is now false after the extension is processed
        assertEquals(updatedBorrow.getExtendDurationRequest(),0, "The extend_duration_request should be set to 0 after the extension.");
    }

    void testDeleteExpiredBorrow() throws SQLException {
        // Insert a borrow record with a borrow date that is far in the past
        int userId = DUMMY_USER_ID;
        int documentId = DUMMY_DOCUMENT_ID;
        java.sql.Timestamp borrowTimestamp = new java.sql.Timestamp(System.currentTimeMillis()- 1000000);
        borrowDAO.addBorrow(userId, documentId, borrowTimestamp,1);

        // Fetch the borrow record before deletion
        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        int borrowId = borrowedDocuments.get(0).getBorrowId();

        // Execute the deletion of expired borrow records
        borrowDAO.deleteExpiredBorrow();

        // Verify that the record was deleted
        Borrow deletedBorrow = borrowDAO.getBorrowById(borrowId);
        assertNull(deletedBorrow, "Expired borrow record should be deleted.");
    }
}
