package com.librarymanagement.model;

public class Document {
    private String title;
    private String author;
    private boolean isAvailable;

    // Constructor
    public Document(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true; // Default to available when created
    }

    /**
     * Get information about the document, including its title,
     * author, and availability status.
     */
    public void getInformation() {
        System.out.println("Title: " + title + ", Author: " + author + ", Available: " + isAvailable);
    }

    /**
     * Allows a user to borrow the document if it is available.
     */
    public void borrow() {
        if (isAvailable) {
            isAvailable = false;
        } else {
            System.out.println("Document is not available for borrowing.");
        }
    }

    /**
     * Allows a user to return a previously borrowed document.
     */
    public void returnDocument() {
        isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
