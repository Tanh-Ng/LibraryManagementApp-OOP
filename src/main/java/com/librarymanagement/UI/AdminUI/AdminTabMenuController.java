package com.librarymanagement.UI.AdminUI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.event.ActionEvent;


public class AdminTabMenuController {
    public void handleToAdminHomePage(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    public void handleToManageDocuments(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showManageDocumentPage();
    }

    public void handleToManageUsers(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showManageUserPage();
    }

    public void handleLogout(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showLoginScreen();
    }
}
