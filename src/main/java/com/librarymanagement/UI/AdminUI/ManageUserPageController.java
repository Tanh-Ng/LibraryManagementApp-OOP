package com.librarymanagement.UI.AdminUI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
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
    public TextField textField;

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
    public void handleAddUser(ActionEvent actionEvent) throws SQLException {
        String userName = userNameField.getText();
        String userPassword = userPasswordField.getText();

        if (userName.isEmpty() || userPassword.isEmpty()) {
            showAlert(AlertType.WARNING, "Input Error", "Please fill in all the information!");
            return;
        }
        User newUser = new NormalUser(0, userName, userPassword);
        // Check if the username already exists
        if (userDAO.isUserExists(userName)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists!");
            return;
        }
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
                if (userDAO.isIdExists(newUserId)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "ID already exists!");
                    return;
                }
                BorrowDAO borrowDAO = new BorrowDAO();
                if (borrowDAO.hasBorrowedDocuments(currentUserId)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "User cannot update ID while having borrowed books.");
                    return;
                }
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
    public void handleDeleteUser(ActionEvent actionEvent) throws SQLException {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            System.out.println("Please select a user to delete.");
            return;
        }
        BorrowDAO borrowDAO = new BorrowDAO();
        if (borrowDAO.hasBorrowedDocuments(selectedUser.getUserId())) {
            showAlert(Alert.AlertType.ERROR, "Error", "User cannot delete user while having borrowed books.");
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

    /**
     * Handles the "Search By User ID" button action to display only the matching user in the table.
     *
     * @param actionEvent The event triggered by the user action.
     */
    @FXML
    private void handleSearchById(ActionEvent actionEvent) {
        String userIdText = textField.getText().trim();

        if (userIdText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input required", "Please enter a User ID to search.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText);
            ObservableList<User> filteredList = FXCollections.observableArrayList();

            // Filter users by User ID
            for (User user : userDAO.getAllUsers()) {
                if (user.getUserId() == userId) {
                    filteredList.add(user);
                }
            }

            if (filteredList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Not Found", "No user found with ID: " + userId);
            }

            // Update data in the table
            userTable.setItems(filteredList);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "User ID must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }


    /**
     * Handles the "See All" button action to display all users in the table.
     *
     * @param actionEvent The event triggered by the user action.
     */
    public void handleSeeAll(ActionEvent actionEvent) {
        // Reload the full list of users from the database
        loadUsers();
        userTable.setItems(userList);
        userTable.refresh();
        showAlert(AlertType.INFORMATION, "Success", "All users are now displayed.");
    }
}
