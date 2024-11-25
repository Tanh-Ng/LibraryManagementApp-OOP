package com.librarymanagement.UI;

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
                // Navigate to the appropriate screen based on user type
                if (user instanceof NormalUser) {
                    LibraryManagementApp.showHomeScreen();
                } else if (user instanceof Admin) {
                    LibraryManagementApp.showAdminPage();
                }
                // Set the current user in the application
                LibraryManagementApp.setCurrentUser(user);
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    public void handleForgotPassword() {
        // Handle forgot password logic (to be implemented)
        System.out.println("Forgot Password clicked");
    }
}
