package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.event.ActionEvent;

public class ManageDocumentController {
    public void handleToHomePageAdmin(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }
}

