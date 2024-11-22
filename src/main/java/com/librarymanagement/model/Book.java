package com.librarymanagement.model;

/**
 * Represents a Book, which is a specialized type of Document
 * with an associated ISBN.
 */
public class Book extends Document {

    private String isbn;



    /**
     * Constructs a Book with the specified title, author, and ISBN.
     *
     * @param title The title of the book.
     * @param author The author of the book.
     * @param isbn The ISBN of the book.
     */
    public Book(String title, String author, String isbn) {
        super(title, author); // Call to Document's constructor
        this.isbn = isbn;
    }

    public Book(int id,String title,String author,String isbn){
        super(id,title,author);
        this.isbn=isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    /**
     * Gets the ISBN of the book.
     *
     * @return The ISBN of the book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Fetches book details from an external source using the ISBN.
     *
     * @return Book details as a string or throws an exception if not found.
     */
    public String fetchFromIsbn() {
        // Logic to fetch book details from Open Library API or similar
        return "Details fetched using ISBN: " + isbn;
    }
}
