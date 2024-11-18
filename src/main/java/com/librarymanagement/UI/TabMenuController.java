package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.event.ActionEvent;

public class TabMenuController {
    public void handleToHomePageUser(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showHomeScreen();
    }

    public void hanldeToBorrowedDocuments(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showBorrowedDocumentsPage();
    }

    public void handleLogout(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showLoginScreen();
    }
}
