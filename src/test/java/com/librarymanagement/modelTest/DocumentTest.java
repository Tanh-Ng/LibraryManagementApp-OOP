package com.librarymanagement.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DocumentTest {

    private Document document;

    @BeforeEach
    public void setUp() {
        document = new Book("Effective Java", "Joshua Bloch","123456", Book.BookType.TEXTBOOKS);
    }

    @Test
    public void testInitialAvailability() {
        assertTrue(document.isAvailable(), "Document should be available initially.");
    }

    @Test
    public void testBorrowDocument() {
        document.borrow();
        assertFalse(document.isAvailable(), "Document should not be available after borrowing.");
    }

    @Test
    public void testReturnDocument() {
        document.borrow();
        document.returnDocument();
        assertTrue(document.isAvailable(), "Document should be available after returning.");
    }

    @Test
    public void testBorrowUnavailableDocument() {
        document.borrow();
        document.borrow();
        assertFalse(document.isAvailable(), "Document should still be unavailable after trying to borrow again.");
    }
}
