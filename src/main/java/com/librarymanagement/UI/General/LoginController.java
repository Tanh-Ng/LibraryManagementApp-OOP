package com.librarymanagement.UI.General;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.model.Admin;
import com.librarymanagement.model.NormalUser;
import com.librarymanagement.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validate input fields
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        try {
            // Authenticate user
            User user = userDAO.authenticateUser(username, password);

            if (user != null) {
                // Set the current user in the application
                LibraryManagementApp.setCurrentUser(user);
                // Navigate to the appropriate screen based on user type
                if (user instanceof NormalUser) {
                    LibraryManagementApp.showHomeScreen();
                } else if (user instanceof Admin) {
                    LibraryManagementApp.showAdminPage();
                }
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    public void handleForgotPassword() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty() && (password == null || password.isEmpty())) {
            errorLabel.setText("Please enter your username to retrieve the password.");
            return;
        }

        if (password != null && !password.isEmpty() && (username == null || username.isEmpty())) {
            errorLabel.setText("Please enter your username to retrieve the password.");
            return;
        }

        try {
            User user = userDAO.authenticateUser(username, "");

            if (user != null) {
                errorLabel.setText("Your password is: " + user.getPassword());
            } else {
                errorLabel.setText("Username not found.");
            }
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }

    }
}
