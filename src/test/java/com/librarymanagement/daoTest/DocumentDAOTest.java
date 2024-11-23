package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DocumentDAOTest {

    private static DocumentDAO documentDAO;

    @BeforeAll
    static void setup() {
        documentDAO = new DocumentDAO();
    }

    @BeforeEach
    void clearTable() throws SQLException {
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM Documents"); // Clears Documents table before each test
        }
    }

    @Test
    void testAddDocument() throws SQLException {
        Document book = new Book("JUnit in Action", "Craig Walls", "9781935182023");
        documentDAO.addDocument(book);

        List<Document> documents = documentDAO.getAllDocuments();
        assertTrue(documents.stream().anyMatch(doc -> "JUnit in Action".equals(doc.getTitle())));
    }

    @Test
    void testGetAllDocuments() throws SQLException {
        Document book1 = new Book("Clean Code", "Robert C. Martin", "9780132350884");
        Document book2 = new Book("Refactoring", "Martin Fowler", "9780201485677");

        documentDAO.addDocument(book1);
        documentDAO.addDocument(book2);

        List<Document> documents = documentDAO.getAllDocuments();
        assertEquals(2, documents.size());
    }

    @Test
    void testGetDocumentById() throws SQLException {
        Document book = new Book("Effective Java", "Joshua Bloch", "9780134685991");
        documentDAO.addDocument(book);

        List<Document> documents = documentDAO.getAllDocuments();
        Document savedBook = documents.get(0);

        Document retrievedBook = documentDAO.getDocumentById(savedBook.getId());
        assertNotNull(retrievedBook);
        assertEquals("Effective Java", retrievedBook.getTitle());
    }

    @Test
    void testChangeTitle() throws SQLException {
        Document book = new Book("Old Title", "Author", "1234567890");
        documentDAO.addDocument(book);

        List<Document> documents = documentDAO.getAllDocuments();
        Document savedBook = documents.get(0);

        documentDAO.changeTitle(savedBook.getId(), "New Title");

        Document updatedBook = documentDAO.getDocumentById(savedBook.getId());
        assertEquals("New Title", updatedBook.getTitle());
    }

    @Test
    void testChangeAuthor() throws SQLException {
        Document book = new Book("Title", "Old Author", "1234567890");
        documentDAO.addDocument(book);

        List<Document> documents = documentDAO.getAllDocuments();
        Document savedBook = documents.get(0);

        documentDAO.changeAuthor(savedBook.getId(), "New Author");

        Document updatedBook = documentDAO.getDocumentById(savedBook.getId());
        assertEquals("New Author", updatedBook.getAuthor());
    }

    @Test
    void testChangeAvailable() throws SQLException {
        Document book = new Book("Available Test", "Author", "1234567890");
        documentDAO.addDocument(book);

        List<Document> documents = documentDAO.getAllDocuments();
        Document savedBook = documents.get(0);

        documentDAO.changeAvailable(savedBook.getId(), false);

        Document updatedBook = documentDAO.getDocumentById(savedBook.getId());
        assertFalse(updatedBook.isAvailable());
    }

    @Test
    void testDeleteDocument() throws SQLException {
        Document book = new Book("To Be Deleted", "Author", "1234567890");
        documentDAO.addDocument(book);

        List<Document> documents = documentDAO.getAllDocuments();
        Document savedBook = documents.get(0);

        documentDAO.deleteDocument(savedBook.getId());

        Document deletedBook = documentDAO.getDocumentById(savedBook.getId());
        assertNull(deletedBook);
    }

    @Test
    void testSoftDeleteDocument() throws SQLException {
        Document book = new Book("To Be Soft Deleted", "Author", "1234567890");
        documentDAO.addDocument(book);

        List<Document> documents = documentDAO.getAllDocuments();
        Document savedBook = documents.get(0);

        documentDAO.softDelete(savedBook.getId());

        Document softDeletedBook = documentDAO.getDocumentById(savedBook.getId());
        assertNotNull(softDeletedBook);
        assertTrue(softDeletedBook.isDeleted()); // Assuming isDeleted() checks the deletion status
    }

    @AfterEach
    void afterEachTest() throws SQLException {
        clearTable(); // Ensures table is cleared after each test as well
    }
}
