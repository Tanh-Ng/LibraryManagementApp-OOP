package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.awt.*;

public class ManageDocumentController {

    public TextField titleField;
    public TextField authorNameField;
    public TextField documentIDField;
    public ChoiceBox<String> documentTypeField;


    private String title;
    private String authorName;
    private String documentID;
    private String documentType;

    private void getInfo() {
        title = titleField.getText();
        authorName = authorNameField.getText();
        documentID = documentIDField.getText();
        documentType = documentTypeField.getValue();
    }


    public void handleToHomePageAdmin(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    public void handleAddDocument(ActionEvent actionEvent) {
        getInfo();

    }

    public void handleUpdateDocument(ActionEvent actionEvent) {
    }

    public void handleDeleteDocument(ActionEvent actionEvent) {
    }
}

