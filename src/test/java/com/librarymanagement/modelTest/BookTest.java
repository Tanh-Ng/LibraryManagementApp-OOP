package com.librarymanagement.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    public void testFetchFromIsbn_ValidIsbn() {
        // Given: a book with a valid ISBN and book type
        String isbn = "9780140327595";  // Example ISBN for "Matilda" by Roald Dahl
        Book.BookType bookType = Book.BookType.ROMANCE; // Example book type
        Book book = new Book("Matilda", "Roald Dahl", isbn, bookType);

        // When: we fetch the book details using the ISBN
        String bookDetails = book.fetchFromIsbn();

        // Then: we assert that the book details are not empty and contain expected information
        assertNotNull(bookDetails, "Book details should not be null");
        assertTrue(bookDetails.contains("Title: Matilda"), "Book title should be 'Matilda'");
        assertTrue(bookDetails.contains("Author: Roald Dahl"), "Book author should be 'Roald Dahl'");
        //assertTrue(bookDetails.contains("Book Type: ROMANCE"), "Book type should be 'ROMANCE'");
        System.out.println(bookDetails);
    }

    @Test
    public void testFetchFromIsbn_InvalidIsbn() {
        // Given: a book with an invalid ISBN and a default book type
        String isbn = "0000000000000x";  // Invalid ISBN
        Book.BookType bookType = Book.BookType.ROMANCE; // Default book type
        Book book = new Book("Unknown Book", "Unknown Author", isbn, bookType);

        // When: we fetch the book details using the ISBN
        String bookDetails = book.fetchFromIsbn();

        // Then: we assert that the book details indicate not found
        assertNotNull(bookDetails, "Book details should not be null");
        assertTrue(bookDetails.contains("Book details not found for ISBN"), "The message should indicate the book is not found");

        System.out.println(bookDetails);
    }
}
