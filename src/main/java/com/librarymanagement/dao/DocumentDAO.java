package com.librarymanagement.dao;

import com.librarymanagement.model.Document;
import com.librarymanagement.model.Book;
import com.librarymanagement.database.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    // Method for adding a new document (Books only)
    public void addDocument(Document document) throws SQLException {
        String sql = "INSERT INTO Documents (title, author, is_available, isbn, book_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setBoolean(3, document.isAvailable());

            // Set ISBN for Book
            if (document instanceof Book) {
                pstmt.setString(4, ((Book) document).getIsbn());
                pstmt.setString(5, ((Book) document).getBookType().name()); // Save the book_type as a string
            }

            // Execute the insert and retrieve the generated keys
            pstmt.executeUpdate();

            // Retrieve the generated ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    document.setId(generatedId); // Set the generated ID on the document object
                } else {
                    throw new SQLException("Failed to retrieve the generated ID for document: " + document.getTitle());
                }
            }
        }
    }

    // Method to get all documents (Books only)
    public List<Document> getAllDocuments() throws SQLException {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents WHERE is_deleted = false"; // No need for document_type filter
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int documentId = rs.getInt("document_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isAvailable = rs.getBoolean("is_available");
                boolean isDeleted = rs.getBoolean("is_deleted");
                String isbn = rs.getString("isbn");

                // Get the book type (mapping from String to BookType enum)
                String bookTypeString = rs.getString("book_type");
                Book.BookType bookType = Book.BookType.valueOf(bookTypeString); // Map to BookType enum

                // Only create a Book object, since that's the only document type now
                Document document = new Book(documentId, title, author, isbn, bookType);
                document.setIsAvailable(isAvailable);
                document.setIsDeleted(isDeleted);
                documents.add(document);
            }
        }
        return documents;
    }

    // Method to get a document by ID (Books only)
    public Document getDocumentById(int documentId) throws SQLException {
        String sql = "SELECT * FROM Documents WHERE document_id = ?"; // No document_type filter anymore
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    boolean isAvailable = rs.getBoolean("is_available");
                    boolean isDeleted = rs.getBoolean("is_deleted");
                    String isbn = rs.getString("isbn");

                    // Get the book type (mapping from String to BookType enum)
                    String bookTypeString = rs.getString("book_type");
                    Book.BookType bookType = Book.BookType.valueOf(bookTypeString);

                    // Return Book object as the only document type
                    Document document = new Book(title, author, isbn, bookType);
                    document.setIsAvailable(isAvailable);
                    document.setIsDeleted(isDeleted);
                    document.setId(documentId);
                    return document;
                } else {
                    return null; // No document found
                }
            }
        }
    }

    // Method to get documents by book type
    public List<Document> getDocumentsByType(Book.BookType bookType) throws SQLException {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents WHERE book_type = ? AND is_deleted = false"; // Filter by book_type and is_deleted
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, bookType.name()); // Set the book type in the query

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int documentId = rs.getInt("document_id");
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    boolean isAvailable = rs.getBoolean("is_available");
                    boolean isDeleted = rs.getBoolean("is_deleted");
                    String isbn = rs.getString("isbn");

                    // Map the book type from String to BookType enum
                    String bookTypeString = rs.getString("book_type");
                    Book.BookType type = Book.BookType.valueOf(bookTypeString);

                    // Create a Book object (Document) and add it to the list
                    Document document = new Book(documentId, title, author, isbn, type);
                    document.setIsAvailable(isAvailable);
                    document.setIsDeleted(isDeleted);
                    documents.add(document);
                }
            }
        }
        return documents;
    }


    // Method to update document's title
    public void changeTitle(int documentId, String newTitle) throws SQLException {
        String sql = "UPDATE Documents SET title = ? WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTitle);
            pstmt.setInt(2, documentId);

            // Execute the update
            pstmt.executeUpdate();
        }
    }

    // Method to update document's author
    public void changeAuthor(int documentId, String newAuthor) throws SQLException {
        String sql = "UPDATE Documents SET author = ? WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newAuthor);
            pstmt.setInt(2, documentId);

            // Execute the update
            pstmt.executeUpdate();
        }
    }

    // Method to update document's availability
    public void changeAvailable(int documentId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE Documents SET is_available = ? WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, documentId);

            // Execute the update
            pstmt.executeUpdate();
        }
    }
    // Method to update document's book type
    public void changeBookType(int documentId, Book.BookType newBookType) throws SQLException {
        String sql = "UPDATE Documents SET book_type = ? WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newBookType.name()); // Set the new book type
            pstmt.setInt(2, documentId); // Set the document ID

            // Execute the update
            pstmt.executeUpdate();
        }
    }


    // Method to delete a document by ID
    public void deleteDocument(int documentId) throws SQLException {
        String sql = "DELETE FROM Documents WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId);

            // Execute the delete
            pstmt.executeUpdate();
        }
    }


    // Method to soft delete a document by ID
    public void softDelete(int documentId) throws SQLException {
        String sql = "UPDATE Documents SET is_deleted = 1 WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId);

            // Execute the soft delete
            pstmt.executeUpdate();
        }
    }
}
