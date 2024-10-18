package com.librarymanagement.model;

/**
 * Represent a normal user without admin permissions.
 */
public class NormalUser extends User {

    /**
     * Constructs a Normal user with a userId, name, and password.
     *
     * @param userId   The ID of the user.
     * @param name     The name of the user.
     * @param password The password for the user.
     */
    public NormalUser(int userId, String name, String password) {
        super(userId, name, password);
    }
}
