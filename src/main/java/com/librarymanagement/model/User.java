package com.librarymanagement.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the library.
 */
public class User {
    private int userId;
    private String name;
    private String password;
    private List<Document> borrowedDocuments;

    /**
     * Constructor to initialize the user.
     *
     * @param userId    The ID of the user.
     * @param name      The name of the user.
     * @param password  The password for the user.
     */
    public User(int userId, String name, String password) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.borrowedDocuments = new ArrayList<>();
    }

    /**
     * Borrows a document.
     *
     * @param document The document that the user wants to borrow.
     */
    public void borrowDocument(Document document) {
        if (document.isAvailable()) {
            document.borrow();
            borrowedDocuments.add(document);
        }
    }

    /**
     * Returns a borrowed document.
     *
     * @param document The document that the user wants to return.
     */
    public void returnDocument(Document document) {
        if (borrowedDocuments.contains(document)) {
            document.returnDocument();
            borrowedDocuments.remove(document);
        }
    }

    /**
     * Sets a new password for the user.
     *
     * @param password The new password that the user wants to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Validates the user's password
     *
     * @param password the password to be validated.
     * @return true if the password is valid, false otherwise.
     */
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }

    public List<Document> getBorrowedDocuments() {
        return borrowedDocuments;
    }

    //Getters
    public int getUserId() {    
        return userId;
    }

    public String getName() {
        return name;
    }

    //Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
