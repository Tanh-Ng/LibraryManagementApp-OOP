package com.librarymanagement.model;

/**
 * Represents user admin with special permissions.
 */
public class Admin extends User {

    /**
     * Constructs an Admin user with a userId, name, and password.
     *
     * @param userId The ID of the admin.
     * @param name The name of the admin.
     * @param password The password for the admin.
     */
    public Admin(int userId, String name, String password) {
        super(userId, name, password);  // Call to User's constructor
    }


    /**
     * Adds a document to the library.
     *
     * @param document the document to be added
     */
    public void addDocument(Document document) {
        //To do
    }

    /**
     * Remove a document from the library.
     *
     * @param document the document to be removed.
     */
    public void removeDocument(Document document) {
        //To do
    }

    /**
     * Updates a document's detail in the library.
     *
     * @param document the document to be updated.
     */
    public void updateDocument(Document document) {
        //To do
    }
}
