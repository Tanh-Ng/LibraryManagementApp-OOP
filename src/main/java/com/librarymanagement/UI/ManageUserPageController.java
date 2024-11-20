package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.model.NormalUser;
import com.librarymanagement.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class ManageUserPageController {
    public TextField userIdField;
    public TextField userNameField;
    public PasswordField userPasswordField;
    public TableView<User> userTable; // TableView để hiển thị danh sách người dùng
    public TableColumn<User, Integer> userIdColumn;
    public TableColumn<User, String> userNameColumn;
    public TableColumn<User, String> userPasswordColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserDAO userDAO = new UserDAO();

    public void initialize() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userTable.setItems(userList);
        loadUsers();
    }

    public void handleToHomePageUser(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    //add new User
    public void handleAddUser(ActionEvent actionEvent) {
        String userName = userNameField.getText();
        String userPassword = userPasswordField.getText();

        if (userName.isEmpty() || userPassword.isEmpty()) {
            System.out.println("Please fill in all the information!");
            return;
        }
        User newUser = new NormalUser(0, userName, userPassword);
        try {
            userDAO.addUser(newUser);
            userList.add(newUser);
            userNameField.clear();
            userPasswordField.clear();
            System.out.println("User has been added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not add user: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            userList.setAll(userDAO.getAllUsers());
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not load the user list: " + e.getMessage());
        }
    }


    public void handleDeleteUser(ActionEvent actionEvent) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            System.out.println("Please select a user to delete.");
            return;
        }
        try {
            userDAO.deleteUser(selectedUser.getUserId());
            userList.remove(selectedUser);
            System.out.println("User has been deleted successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Could not delete user: " + e.getMessage());
        }
    }
}
