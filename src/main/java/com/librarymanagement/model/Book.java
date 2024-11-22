package com.librarymanagement.model;

import com.librarymanagement.api.ApiClient;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a Book, which is a specialized type of Document with an associated ISBN.
 */
public class Book extends Document {

    private String isbn;
    private ApiClient apiClient;

    public Book(String title, String author, String isbn) {
        super(title, author); // Call to Document's constructor
        this.isbn = isbn;
        this.apiClient = new ApiClient(); // Initialize the API client
    }

    public Book(int id, String title, String author, String isbn) {
        super(id, title, author);
        this.isbn = isbn;
        this.apiClient = new ApiClient(); // Initialize the API client
    }

    public String getIsbn() {
        return isbn;
    }

    public String fetchFromIsbn() {
        // Use the OpenLibraryApiClient to fetch the book details
        JSONObject bookData = ApiClient.fetchBookDetailsByIsbn(isbn);

        if (bookData != null) {
            // Get book details (title, author, publisher, and publish date)
            String title = bookData.optString("title", "N/A");

            // Handle authors (JSONArray of JSONObject)
            String author = "Unknown";
            JSONArray authorsArray = bookData.optJSONArray("authors");
            if (authorsArray != null && authorsArray.length() > 0) {
                JSONObject authorObject = authorsArray.getJSONObject(0);  // Get the first author
                author = authorObject.optString("name", "Unknown");
            }

            // Handle publishers (JSONArray of String or JSONObject)
            String publisher = "Unknown";
            JSONArray publishersArray = bookData.optJSONArray("publishers");
            if (publishersArray != null && publishersArray.length() > 0) {
                JSONObject publisherObject = publishersArray.getJSONObject(0);  // Get the first publisher
                publisher = publisherObject.optString("name", "Unknown");
            }

            // Handle publish date
            String publishDate = bookData.optString("publish_date", "Unknown");

            return String.format("Title: %s, Author: %s, Publisher: %s, Published on: %s", title, author, publisher, publishDate);
        } else {
            return "Book details not found for ISBN: " + isbn;
        }
    }
}
