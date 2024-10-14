package com.librarymanagement.model;

import java.util.List;

/**
 * Represents a user in the library.
 */
public class User {
    private int userId;
    private String name;
    private List<Document> borrowedDocuments;

    /**
     * Borrows a document.
     *
     * @param document The document that the user wants to borrow.
     */
    public void borrowDocument(Document document) {
        //To do
    }

    /**
     * Returns a borrowed document.
     *
     * @param document The document that the user wants to return.
     */
    public void returnDocument(Document document) {
        //To do
    }

    /**
     * Sets a new password for the user.
     *
     * @param password The new password that the user wants to set.
     */
    public void setPassword(String password) {
        //To do
    }

    /**
     * Validates the user's password
     *
     * @param password the password to be validated.
     * @return true if the password is valid, false otherwise.
     */
    public boolean validatePassword(String password) {
        //To do
        return false;
    }

}
