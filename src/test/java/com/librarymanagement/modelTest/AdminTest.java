package com.librarymanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    @Test
    void testAddDocument() {
        Admin admin = new Admin(1, "Admin", "password");
        Document document = new Book("Sample Document", "Sample Author","12345", Book.BookType.TEXTBOOKS);
        Library library = new Library();

        // Assuming Admin adds document to the library
        admin.addDocument(document);
        library.addDocument(document, admin);  // Mock adding to library

        // Verify document was added to the library
        assertTrue(library.getDocuments().contains(document), "Document should be added to the library");
    }

    @Test
    void testRemoveDocument() {
        Admin admin = new Admin(1, "Admin", "password");
        Document document = new Book("Sample Document", "Sample Author","124355", Book.BookType.TEXTBOOKS);
        Library library = new Library();

        // Adding document first
        admin.addDocument(document);
        library.addDocument(document, admin);

        // Remove the document
        admin.removeDocument(document);
        library.removeDocument(document, admin);

        // Verify document is no longer in the library
        assertFalse(library.getDocuments().contains(document), "Document should be removed from the library");
    }
}
