package com.librarymanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private User user;
    private Document document;

    @BeforeEach
    void setUp() {
        // Create a user with userId, name, and password
        user = new User(1, "John Doe", "password123");
        document = new Document("Sample Title", "Sample Author");
    }

    @Test
    void testSetPasswordAndValidatePassword() {
        // Test setting and validating password
        user.setPassword("newPassword");
        assertTrue(user.validatePassword("newPassword"));
        assertFalse(user.validatePassword("wrongPassword"));
    }

    @Test
    void testBorrowDocument() {
        // Test borrowing a document
        assertTrue(document.isAvailable());
        user.borrowDocument(document);
        assertFalse(document.isAvailable());
        assertEquals(1, user.getBorrowedDocuments().size());
    }

    @Test
    void testReturnDocument() {
        // Test returning a borrowed document
        user.borrowDocument(document);
        assertFalse(document.isAvailable());
        user.returnDocument(document);
        assertTrue(document.isAvailable());
        assertEquals(0, user.getBorrowedDocuments().size());
    }
}
