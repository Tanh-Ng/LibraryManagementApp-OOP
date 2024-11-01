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
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            pstmt.executeUpdate();
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
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isAvailable = rs.getBoolean("is_available");
                String docType = rs.getString("document_type");

                Document document;
                if ("Book".equals(docType)) {
                    String isbn = rs.getString("isbn");
                    document = new Book(title, author, isbn);
                } else {
                    String academicAdvisor = rs.getString("academic_advisor");
                    document = new Thesis(title, author, academicAdvisor);
                }
                document.setIsAvailable(isAvailable);
                documents.add(document);
            }
        }
        return documents;
    }

    // method to get document base on ID;
    //public Document getDocument(int documentId) throws SQLException{
    //}

    //method to update document's title base on ID;
    public void changeTitle(int documentId, String newTitle) throws SQLException {

    }

    //method to update document's author base on ID;
    public void changeAuthor(int documentId, String newAuthor) throws SQLException {

    }

    //method to update document's availability base on ID;
    public void changeAvailable(int documentId, boolean isAvailable) throws SQLException {

    }

    //method to delete a document base on ID;
    public void deleteDocument(int documentId) throws SQLException {

    }

}
