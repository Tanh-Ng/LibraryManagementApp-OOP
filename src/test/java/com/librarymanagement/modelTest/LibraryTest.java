package com.librarymanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private Admin admin;
    private Document document;

    @BeforeEach
    void setUp() {
        library = new Library();
        admin = new Admin(1, "Admin", "password123");
        document = new Book("Sample Title", "Sample Author","12345", Book.BookType.TEXTBOOKS);
    }

    @Test
    void testAddDocument() {
        // Add document to library
        library.addDocument(document, admin);
        assertTrue(library.getDocuments().contains(document), "Document should be added to library");
    }

    @Test
    void testRemoveDocument() {
        // Add document first
        library.addDocument(document, admin);
        // Remove document
        library.removeDocument(document, admin);
        assertFalse(library.getDocuments().contains(document), "Document should be removed from library");
    }

    @Test
    void testUpdateDocument() {
        // Add document first
        library.addDocument(document, admin);
        // Create a new document with updated information
        Document updatedDocument = new Book("Updated Title", "Updated Author","12345", Book.BookType.TEXTBOOKS);
        library.updateDocument(document, updatedDocument, admin);
        assertTrue(library.getDocuments().contains(updatedDocument), "Document should be updated in the library");
    }

    @Test
    void testBorrowDocument() {
        User user = new NormalUser(1, "John Doe", "password123");
        // Add document to library
        library.addDocument(document, admin);
        // Borrow the document
        library.borrowDocument(user, document);
        // Check if user has borrowed the document
        assertTrue(user.getBorrowedDocuments().contains(document), "User should have borrowed the document");
    }

    @Test
    void testReturnDocument() {
        User user = new NormalUser(1, "John Doe", "password123");
        // Add document to library
        library.addDocument(document, admin);
        // Borrow and then return the document
        library.borrowDocument(user, document);
        library.returnDocument(user, document);
        assertFalse(user.getBorrowedDocuments().contains(document), "User should have returned the document");
    }
}
