package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    /* */

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            User user = userDAO.getUserById(Integer.parseInt(username)); // Example assumes username is ID
            if (user != null && user.validatePassword(password)) {
                //Navigate to home screen
                errorLabel.setText("");
                LibraryManagementApp.showHomeScreen(); // Placeholder function
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    public void handleForgotPassword() {
        System.out.println("ok");
    }
}