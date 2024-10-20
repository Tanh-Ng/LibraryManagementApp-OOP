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
    public static void addDocument(Document document, Admin admin) {
        documents.add(document);
        System.out.println("Document added to library by " + admin.getName() + ".");
    }

    /**
     * Returns the list of documents in the library.
     *
     * @return the documents in the library
     */
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * Removes a document from the library.
     *
     * @param document the document to be removed from the library
     * @param admin the admin performing the action
     */
    public static void removeDocument(Document document, Admin admin) {
        documents.remove(document);
        System.out.println("Document removed from library by " + admin.getName() + ".");
    }

    /**
     * Updates the information of an existing document in the library.
     *
     * @param targetDocument the document to be updated
     * @param updatedDocument the updated document
     * @param admin the admin performing the action
     */
    public static void updateDocument(Document targetDocument, Document updatedDocument, Admin admin) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).equals(targetDocument)) {
                documents.set(i, updatedDocument);
                System.out.println("Document " + targetDocument.getTitle() + " updated by " 
                                    + admin.getName() + " to" + updatedDocument.getTitle() + ".");
                return;
            }
        }
    }

    /**
     * Allows a user to borrow a document from the library.
     *
     * @param user the user borrowing the document
     * @param document the document being borrowed
     */
    public void borrowDocument(User user, Document document) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).equals(document)) {
                if (document.isAvailable()) {
                    user.borrowDocument(document);
                    System.out.println("Document " + document.getTitle() + " borrowed by " + user.getName() + ".");
                    return;
                } else {
                    System.out.println("Document " + document.getTitle() + " is not available for borrowing.");
                    return;
                }
            }
        }
    }

    /**
     * Allows a user to return a previously borrowed document to the library.
     *
     * @param user the user returning the document
     * @param document the document being returned
     */
    public void returnDocument(User user, Document document) {
        user.returnDocument(document);
        System.out.println("Document " + document.getTitle() + " returned by " + user.getName() + ".");
    }
}
