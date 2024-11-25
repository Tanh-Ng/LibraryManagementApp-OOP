package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.model.NormalUser;
import com.librarymanagement.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.sql.SQLException;

public class ManageUserPageController {
    @FXML
    public TextField userIdField;
    @FXML
    public TextField userNameField;
    @FXML
    public PasswordField userPasswordField;
    @FXML
    public TableView<User> userTable;
    @FXML
    public TableColumn<User, Integer> userIdColumn;
    @FXML
    public TableColumn<User, String> userNameColumn;
    @FXML
    public TableColumn<User, String> userPasswordColumn;

    @FXML
    private ObservableList<User> userList = FXCollections.observableArrayList();
    @FXML
    private UserDAO userDAO = new UserDAO();

    /**
     * Initializes the user management page by setting up the table columns
     * and loading all users from the database.
     */
    public void initialize() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userTable.setItems(userList);
        loadUsers();
        // Set up row selection listener
        userTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                fillFieldsWithSelectedUser(newValue);
            }
        });
    }

    /**
     * Navigates to the Admin home page.
     *
     * @param actionEvent The event triggered by the user action.
     * @throws Exception If navigation fails.
     */
    public void handleToHomePageAdmin(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    /**
     * Adds a new user with the given username and password.
     * Displays alerts for successful or failed user creation.
     *
     * @param actionEvent The event triggered by the user action.
     */
    public void handleAddUser(ActionEvent actionEvent) {
        String userName = userNameField.getText();
        String userPassword = userPasswordField.getText();

        if (userName.isEmpty() || userPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please fill in all the information!");
            return;
        }
        User newUser = new NormalUser(0, userName, userPassword);
        try {
            userDAO.addUser(newUser);
            userList.add(newUser);
            userNameField.clear();
            userPasswordField.clear();
            userTable.refresh();
            showAlert(AlertType.INFORMATION, "Success", "User has been added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not add user: " + e.getMessage());
        }

    }

    /**
     * Handles the update user action. Updates the user's information in the database.
     *
     * @param actionEvent The event triggered by the user action (click on the update button).
     */
    public void handleUpdateUser(ActionEvent actionEvent) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        String userIdStr = userIdField.getText();
        String userName = userNameField.getText();
        String userPassword = userPasswordField.getText();

        // Validate input fields
        if (userIdStr.isEmpty() || userName.isEmpty() || userPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please fill in all the information!");
            return;
        }

        try {
            int newUserId = Integer.parseInt(userIdStr); // Convert the userId to an integer

            int currentUserId = selectedUser.getUserId(); // Get the current user ID

            // Update the user ID if it's different
            if (newUserId != currentUserId) {
                UserDAO userDAO = new UserDAO();
                userDAO.changeUserId(currentUserId, newUserId); // Update the user ID in the database
                selectedUser.setUserId(newUserId); // Update the user's ID in the UI
            }

            // Update the user in the database
            userDAO.changeName(newUserId, userName);
            userDAO.changePassword(newUserId, userPassword);
            selectedUser.setPassword(userPassword);
            selectedUser.setName(userName);
            userTable.refresh();
            showAlert(AlertType.INFORMATION, "Success", "User has been updated successfully!");

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Error", "User ID must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not update user: " + e.getMessage());
        }

    }


    /**
     * Loads all users from the database and updates the user list.
     */
    private void loadUsers() {
        try {
            userList.setAll(userDAO.getAllUsers());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not load the user list: " + e.getMessage());
        }
    }

    /**
     * Deletes the selected user from the user list and database.
     * Displays alerts for successful or failed deletion.
     *
     * @param actionEvent The event triggered by the user action.
     */
    public void handleDeleteUser(ActionEvent actionEvent) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            System.out.println("Please select a user to delete.");
            return;
        }
        try {
            userDAO.deleteUser(selectedUser.getUserId());
            userList.remove(selectedUser);
            userTable.refresh();
            System.out.println("User has been deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Could not delete user: " + e.getMessage());
        }

    }

    private void fillFieldsWithSelectedUser(User selectedUser) {
        userIdField.setText(String.valueOf(selectedUser.getUserId()));
        userNameField.setText(selectedUser.getName());
        userPasswordField.setText(selectedUser.getPassword());
    }

    /**
     * Displays an alert with the specified type, title, and message.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION, WARNING).
     * @param title     The title of the alert window.
     * @param message   The message to be displayed in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
