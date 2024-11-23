package com.librarymanagement.model;

import com.librarymanagement.api.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a Book, which is a specialized type of Document with an associated ISBN and book type.
 */
public class Book extends Document {

    private String isbn;
    private BookType bookType;

    // Enum for BookType
    public enum BookType {
        TEXTBOOKS,
        ROMANCE,
        SCIENCE_FICTION,
        FANTASY,
        BIOGRAPHY,
        RELIGIOUS,
        ART;
    }

    // Constructors
    public Book(String title, String author, String isbn, BookType bookType) {
        super(title, author);
        this.isbn = isbn;
        this.bookType = bookType;
    }

    public Book(int id, String title, String author, String isbn, BookType bookType) {
        super(id, title, author);
        this.isbn = isbn;
        this.bookType = bookType;
    }

    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public BookType getBookType() {
        return bookType;
    }

    public void setBookType(BookType bookType) {
        this.bookType = bookType;
    }

    // Method to fetch details from ISBN (using ApiClient)
    public String fetchFromIsbn() {
        // Use the OpenLibraryApiClient to fetch the book details
        JSONObject bookData = ApiClient.fetchBookDetailsByIsbn(isbn);

        if (bookData != null) {
            String title = bookData.optString("title", "N/A");
            String author = getAuthorFromJson(bookData);
            String publisher = getPublisherFromJson(bookData);
            String publishDate = bookData.optString("publish_date", "Unknown");

            return String.format("Title: %s, Author: %s, Publisher: %s, Published on: %s", title, author, publisher, publishDate);
        } else {
            return "Book details not found for ISBN: " + isbn;
        }
    }

    // Helper method to extract author from JSON
    private String getAuthorFromJson(JSONObject bookData) {
        JSONArray authorsArray = bookData.optJSONArray("authors");
        if (authorsArray != null && authorsArray.length() > 0) {
            JSONObject authorObject = authorsArray.getJSONObject(0); // Get the first author
            return authorObject.optString("name", "Unknown");
        }
        return "Unknown";
    }

    // Helper method to extract publisher from JSON
    private String getPublisherFromJson(JSONObject bookData) {
        JSONArray publishersArray = bookData.optJSONArray("publishers");
        if (publishersArray != null && publishersArray.length() > 0) {
            JSONObject publisherObject = publishersArray.getJSONObject(0); // Get the first publisher
            return publisherObject.optString("name", "Unknown");
        }
        return "Unknown";
    }
}
