package com.librarymanagement.model;

import java.util.Objects;

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

     // Getters
     public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    // Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // Override
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Document document = (Document) obj;
        return Objects.equals(title, document.title) &&
                Objects.equals(author, document.author) &&
                isAvailable == document.isAvailable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, isAvailable);
    }

    
}
