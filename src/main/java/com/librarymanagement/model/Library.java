package com.librarymanagement.model;

import java.util.List;

public class Library {
    private List<Document> documents;
    private List<User> users;

    /**
     * Adds a new document to the library.
     *
     * @param document the document to be added to the library
     * @param admin the admin performing the action
     */
    public void addDocument(Document document, Admin admin) {
        //To do
    }

    /**
     * Removes a document from the library.
     *
     * @param document document the document to be removed from the library
     * @param admin the admin performing the action
     */
    public void removeDocument(Document document, Admin admin) {
        //To do
    }

    /**
     * Updates the information of an existing document in the library.
     *
     * @param document the document with updated information
     * @param admin the admin performing the action
     */
    public void updateDocument(Document document, Admin admin) {
        //To do
    }

    /**
     * Allows a user to borrow a document from the library.
     *
     * @param user
     * @param admin admin the admin performing the action
     */
    public void borrowDocument(User user, Admin admin) {
        //To do
    }

    /**
     * Allows a user to return a previously borrowed document to the library.
     *
     * @param user user the user returning the document
     * @param admin admin the admin performing the action
     */
    public void returnDocument(User user, Admin admin) {
        //To do
    }



}
