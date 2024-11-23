package com.librarymanagement.model;

public class Document {
    private int DocumentId;
    private String title;
    private String author;
    protected boolean isAvailable;
    private boolean isDeleted;


    // Constructor
    public Document(String title, String author) {
        this.title = title;
        this.author = author;
        this.isAvailable = true; // Default to available when created
        this.isDeleted=false;
    }

    public Document(int DocumentId, String title, String author) {
        this.DocumentId = DocumentId;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.isDeleted=false;
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

    public int getDocumentId() {
        return DocumentId;
    }

    public String getAuthor() {
        return author;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isDeleted() {
        return isDeleted;
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

    public void setId(int DocumentId) {
        this.DocumentId = DocumentId;
    }

    public void setIsDeleted(boolean isDeleted){
        this.isDeleted=isDeleted;
    }
}
