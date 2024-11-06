package com.librarymanagement.dao;

import com.librarymanagement.model.Document;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Thesis;
import com.librarymanagement.database.DatabaseConfig;

import javax.print.Doc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentDAO {

    //Method for add new document
    public void addDocument(Document document) throws SQLException {
        String sql = "INSERT INTO Documents (title, author, is_available, document_type, isbn, academic_advisor) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getAuthor());
            pstmt.setBoolean(3, document.isAvailable());
            pstmt.setString(4, document instanceof Book ? "Book" : "Thesis");

            if (document instanceof Book) {
                pstmt.setString(5, ((Book) document).getIsbn());
                pstmt.setNull(6, Types.VARCHAR);
            } else if (document instanceof Thesis) {
                pstmt.setNull(5, Types.VARCHAR);
                pstmt.setString(6, ((Thesis) document).getAcademicAdvisor());
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


    // method to get all document
    public List<Document> getAllDocuments() throws SQLException {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT * FROM Documents";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int documentId = rs.getInt("document_id"); // Get the document ID
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isAvailable = rs.getBoolean("is_available");
                boolean isDeleted = rs.getBoolean("is_deleted");
                String docType = rs.getString("document_type");

                Document document;
                if ("Book".equals(docType)) {
                    String isbn = rs.getString("isbn");
                    document = new Book(documentId, title, author, isbn); // Pass documentId to constructor
                } else {
                    String academicAdvisor = rs.getString("academic_advisor");
                    document = new Thesis(documentId, title, author, academicAdvisor); // Pass documentId to constructor
                }
                document.setIsAvailable(isAvailable);
                document.setIsDeleted(isDeleted);
                documents.add(document);
            }
        }
        return documents;
    }

    // method to get document base on ID;
    public Document getDocumentById(int documentId) throws SQLException {
        String sql = "SELECT * FROM Documents WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId); // Set the document ID for the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    boolean isAvailable = rs.getBoolean("is_available");
                    boolean isDeleted = rs.getBoolean("is_deleted");
                    String docType = rs.getString("document_type");

                    Document document;
                    if ("Book".equalsIgnoreCase(docType)) {
                        String isbn = rs.getString("isbn");
                        document = new Book(title, author, isbn); // Create Book object
                    } else if ("Thesis".equalsIgnoreCase(docType)) {
                        String academicAdvisor = rs.getString("academic_advisor");
                        document = new Thesis(title, author, academicAdvisor); // Create Thesis object
                    } else {
                        return null; // If document type is unknown, return null
                    }
                    document.setIsAvailable(isAvailable); // Set availability status
                    document.setIsDeleted(isDeleted);
                    document.setId(documentId); // Set document ID
                    return document;
                } else {
                    return null; // No document found with the specified ID
                }
            }
        }
    }


    //method to update document's title base on ID ;
    public void changeTitle(int documentId, String newTitle) throws SQLException {
        String sql = "UPDATE Documents SET title = ? WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setString(1, newTitle); //Set new tile for document
                pstmt.setInt(2, documentId);  //Set the document ID for the Update
                
                //Execute the change
                pstmt.executeUpdate();
        }
    }

    //method to update document's author base on ID;
    public void changeAuthor(int documentId, String newAuthor) throws SQLException {
        String sql = "UPDATE Documents SET author = ? WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setString(1, newAuthor); //Set new author for document
                pstmt.setInt(2, documentId);  //Set the document ID for the Update
                
                //Execute the change
                pstmt.executeUpdate();
        }
    }

    //method to update document's availability base on ID;
    public void changeAvailable(int documentId, boolean isAvailable) throws SQLException {
        String sql = "UPDATE Documents SET is_available = ? WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setBoolean(1, isAvailable); //Set new availability for document
                pstmt.setInt(2, documentId); //Set the document ID for the Update
                
                //Execute the change
                pstmt.executeUpdate();
        } 
    }

    //method to delete a document base on ID;
    public void deleteDocument(int documentId) throws SQLException {
        String sql = "DELETE FROM Documents WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setInt(1, documentId); //Set the document ID for the Delete
                
                //Execute the change
                pstmt.executeUpdate();
        }
    }

    // Method to soft delete a document based on its ID
    public void softDelete(int documentId) throws SQLException {
        String sql = "UPDATE Documents SET is_deleted = 1 WHERE document_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, documentId);  // Set the document ID for the soft delete

            // Execute the soft delete
            pstmt.executeUpdate();
        }
    }


}
