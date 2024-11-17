package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ManageUserPageController {
    public TextField userIdField;
    public TextField userNameField;
    public PasswordField userPasswordField;

    private String userId;
    private String userName;
    private String userPassord;


    private void getInfo() {
        userId = userIdField.getText();
        userName = userNameField.getText();
        userPassord = userPasswordField.getText();
    }

    public void handleToHomePageUser(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    //add new User
    public void handleAddUser(ActionEvent actionEvent) {
        getInfo();

    }

    //delete User
    public void handleDeleteUser(ActionEvent actionEvent) {
        getInfo();
    }
}
