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
    void shouldAddDocument() throws SQLException {
        // Given: a book with a valid ISBN and a book type
        Book book = createBook("JUnit in Action", "Craig Walls", "9781935182023", Book.BookType.SCIENCE_FICTION);
        documentDAO.addDocument(book);

        // When: we fetch all documents
        List<Document> documents = documentDAO.getAllDocuments();

        // Then: we assert that the book is added
        assertTrue(documents.stream().anyMatch(doc -> "JUnit in Action".equals(doc.getTitle())));
    }

    @Test
    void shouldReturnAllDocuments() throws SQLException {
        // Given: two books with valid ISBNs and book types
        Book book1 = createBook("Clean Code", "Robert C. Martin", "9780132350884", Book.BookType.TEXTBOOKS);
        Book book2 = createBook("Refactoring", "Martin Fowler", "9780201485677", Book.BookType.TEXTBOOKS);

        documentDAO.addDocument(book1);
        documentDAO.addDocument(book2);

        // When: we fetch all documents
        List<Document> documents = documentDAO.getAllDocuments();

        // Then: we assert that the correct number of documents are returned
        assertEquals(2, documents.size());
    }

    @Test
    void shouldRetrieveDocumentById() throws SQLException {
        // Given: a book with a valid ISBN and book type
        Book book = createBook("Effective Java", "Joshua Bloch", "9780134685991", Book.BookType.TEXTBOOKS);
        documentDAO.addDocument(book);

        // When: we fetch the document by ID
        Document savedBook = documentDAO.getAllDocuments().get(0);
        Document retrievedBook = documentDAO.getDocumentById(savedBook.getDocumentId());

        // Then: we assert the book details are correct
        assertNotNull(retrievedBook);
        assertEquals("Effective Java", retrievedBook.getTitle());
    }

    @Test
    void shouldChangeDocumentTitle() throws SQLException {
        // Given: a book with a valid ISBN and book type
        Book book = createBook("Old Title", "Author", "1234567890", Book.BookType.BIOGRAPHY);
        documentDAO.addDocument(book);

        // When: we change the title of the book
        Document savedBook = documentDAO.getAllDocuments().get(0);
        documentDAO.changeTitle(savedBook.getDocumentId(), "New Title");

        // Then: we assert that the title has been updated
        Document updatedBook = documentDAO.getDocumentById(savedBook.getDocumentId());
        assertEquals("New Title", updatedBook.getTitle());
    }

    @Test
    void shouldChangeDocumentAuthor() throws SQLException {
        // Given: a book with a valid ISBN and book type
        Book book = createBook("Title", "Old Author", "1234567890", Book.BookType.ROMANCE);
        documentDAO.addDocument(book);

        // When: we change the author of the book
        Document savedBook = documentDAO.getAllDocuments().get(0);
        documentDAO.changeAuthor(savedBook.getDocumentId(), "New Author");

        // Then: we assert that the author has been updated
        Document updatedBook = documentDAO.getDocumentById(savedBook.getDocumentId());
        assertEquals("New Author", updatedBook.getAuthor());
    }

    @Test
    void shouldChangeDocumentAvailability() throws SQLException {
        // Given: a book with a valid ISBN and book type
        Book book = createBook("Available Test", "Author", "1234567890", Book.BookType.ART);
        documentDAO.addDocument(book);

        // When: we change the availability status of the book
        Document savedBook = documentDAO.getAllDocuments().get(0);
        documentDAO.changeAvailable(savedBook.getDocumentId(), false);

        // Then: we assert that the availability has been updated
        Document updatedBook = documentDAO.getDocumentById(savedBook.getDocumentId());
        assertFalse(updatedBook.isAvailable());
    }

    @Test
    void shouldDeleteDocument() throws SQLException {
        // Given: a book with a valid ISBN and book type
        Book book = createBook("To Be Deleted", "Author", "1234567890", Book.BookType.SCIENCE_FICTION);
        documentDAO.addDocument(book);

        // When: we delete the book
        Document savedBook = documentDAO.getAllDocuments().get(0);
        documentDAO.deleteDocument(savedBook.getDocumentId());

        // Then: we assert that the document is deleted
        Document deletedBook = documentDAO.getDocumentById(savedBook.getDocumentId());
        assertNull(deletedBook);
    }

    @Test
    void shouldSoftDeleteDocument() throws SQLException {
        // Given: a book with a valid ISBN and book type
        Book book = createBook("To Be Soft Deleted", "Author", "1234567890", Book.BookType.ROMANCE);
        documentDAO.addDocument(book);

        // When: we perform a soft delete
        Document savedBook = documentDAO.getAllDocuments().get(0);
        documentDAO.softDelete(savedBook.getDocumentId());

        // Then: we assert that the document is marked as deleted
        Document softDeletedBook = documentDAO.getDocumentById(savedBook.getDocumentId());
        assertNotNull(softDeletedBook);
        assertTrue(softDeletedBook.isDeleted(), "Document should be soft deleted.");
    }

    @AfterEach
    void afterEachTest() throws SQLException {
        clearTable(); // Ensures table is cleared after each test
    }

    // Helper method to create a Book with book type
    private Book createBook(String title, String author, String isbn, Book.BookType bookType) {
        return new Book(title, author, isbn, bookType);
    }
}
