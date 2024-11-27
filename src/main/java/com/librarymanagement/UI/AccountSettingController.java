package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AccountSettingController {
    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField reEnterNewPasswordField;

    public void handleChangePassword(ActionEvent actionEvent) throws SQLException {
        if (oldPasswordField.getText().equals(LibraryManagementApp.getCurrentUser().getPassword())
                || newPasswordField.equals(reEnterNewPasswordField)) {
            return;
        }
        LibraryManagementApp.getCurrentUser().setPassword(newPasswordField.getText());
        UserDAO userDAO = new UserDAO();
        userDAO.changePassword(LibraryManagementApp.getCurrentUser().getUserId(), newPasswordField.getText());
    }

    public void handleCancel(ActionEvent actionEvent) {
        // Get the stage from the event source and close it
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
