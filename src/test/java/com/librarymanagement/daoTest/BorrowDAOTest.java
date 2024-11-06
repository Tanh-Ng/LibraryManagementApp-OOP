package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Borrow;
import org.junit.jupiter.api.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BorrowDAOTest {

    private static BorrowDAO borrowDAO;

    @BeforeAll
    static void setUp() {
        borrowDAO = new BorrowDAO();
    }
    @BeforeEach
    void clearTable() throws SQLException {
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Documents"); // Clears Documents table before each test
        }
    }

    @Test
    void testAddBorrow() throws Exception {
        int userId = 1;
        int documentId = 1;
        Date borrowDate = new Date(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowDate);

        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        assertFalse(borrowedDocuments.isEmpty(), "Borrowed documents should not be empty after adding a borrow.");
        assertEquals(documentId, borrowedDocuments.get(0).getDocumentId(), "Document ID should match the added borrow.");
    }

    @Test
    void testGetBorrowedDocumentsByUser() throws Exception {
        int userId = 1;
        int documentId = 1;
        Date borrowDate = new Date(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowDate);
        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        assertNotNull(borrowedDocuments, "Result should not be null.");
    }

    @Test
    void testGetBorrowById() throws Exception {
        int userId = 1;
        int documentId = 1;
        Date borrowDate = new Date(System.currentTimeMillis());

        borrowDAO.addBorrow(userId, documentId, borrowDate);

        List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(userId);
        Borrow borrow = borrowDAO.getBorrowById(borrowedDocuments.get(0).getBorrowId());

        assertNotNull(borrow, "Borrow should not be null.");
        assertEquals(userId, borrow.getUserId(), "User ID should match.");
    }

    @Test
    void testDeleteBorrow() throws Exception {
        int userId = 1;
        int documentId = 1;
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
}
