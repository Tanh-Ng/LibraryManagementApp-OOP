package com.librarymanagement.dao;

import com.librarymanagement.database.DatabaseConfig;
import com.librarymanagement.model.Document;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Thesis;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentDAOTest {

    private DocumentDAO documentDAO;

    @BeforeEach
    public void setUp() {
        documentDAO = new DocumentDAO();
        clearDocumentsTable();
    }

    @AfterEach
    public void tearDown() {
        //clearDocumentsTable();
    }

    // Utility method to clear the Documents table before each test
    private void clearDocumentsTable() {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Documents")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddAndRetrieveBook() throws SQLException {
        Book book = new Book("Effective Java", "Joshua Bloch", "9780134685991");

        documentDAO.addDocument(book);
        List<Document> documents = documentDAO.getAllDocuments();
        assertEquals(1, documents.size());
        Document retrievedDoc = documents.get(0);
        assertTrue(retrievedDoc instanceof Book);
        assertEquals("Effective Java", retrievedDoc.getTitle());
        assertEquals("Joshua Bloch", retrievedDoc.getAuthor());
        assertEquals("9780134685991", ((Book) retrievedDoc).getIsbn());
        assertTrue(retrievedDoc.isAvailable());
    }

    @Test
    public void testAddAndRetrieveThesis() throws SQLException {
        Thesis thesis = new Thesis("AI in Modern World", "John Doe", "Dr. Smith");

        documentDAO.addDocument(thesis);
        List<Document> documents = documentDAO.getAllDocuments();

        assertEquals(1, documents.size());
        Document retrievedDoc = documents.get(0);
        assertTrue(retrievedDoc instanceof Thesis);
        assertEquals("AI in Modern World", retrievedDoc.getTitle());
        assertEquals("John Doe", retrievedDoc.getAuthor());
        assertEquals("Dr. Smith", ((Thesis) retrievedDoc).getAcademicAdvisor());
        assertTrue(retrievedDoc.isAvailable());
    }

    @Test
    public void testGetAllDocuments() throws SQLException {
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884");
        Thesis thesis = new Thesis("Quantum Computing", "Alice Smith", "Dr. Johnson");

        documentDAO.addDocument(book);
        documentDAO.addDocument(thesis);

        List<Document> documents = documentDAO.getAllDocuments();

        assertEquals(2, documents.size());
    }
}
