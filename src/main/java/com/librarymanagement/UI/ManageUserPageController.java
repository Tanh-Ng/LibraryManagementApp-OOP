package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.event.ActionEvent;

public class ManageUserPageController {
    public void handleToHomePageUser(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

}
