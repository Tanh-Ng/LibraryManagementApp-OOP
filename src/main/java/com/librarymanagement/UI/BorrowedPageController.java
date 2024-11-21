package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.util.List;

public class BorrowedPageController {
    @FXML
    public Text announcementField;

    @FXML
    private TableView<Borrow> borrowedTable;

    @FXML
    private TableColumn<Borrow, Integer> columnBorrowId;

    @FXML
    private TableColumn<Borrow, Integer> columnUserId;

    @FXML
    private TableColumn<Borrow, Integer> columnDocumentId;

    @FXML
    private TableColumn<Borrow, java.sql.Date> columnBorrowDate;

    @FXML
    private Button returnButton;

    private BorrowDAO borrowDAO = new BorrowDAO();
    private ObservableList<Borrow> borrowedList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up table columns
        columnBorrowId.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        columnUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        columnDocumentId.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        columnBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));

        // Load data into the table
        loadBorrowedData();
    }

    private void loadBorrowedData() {
        try {
            User currentUser = LibraryManagementApp.getCurrentUser();
            if (currentUser == null) {
                announcementField.setText("No user is currently logged in. Please log in to view borrowed documents.");
                return;
            }

            List<Borrow> borrowedDocuments = borrowDAO.getBorrowedDocumentsByUser(currentUser.getUserId());
            borrowedList.setAll(borrowedDocuments);
            borrowedTable.setItems(borrowedList);
        } catch (SQLException e) {
            announcementField.setText("Failed to load borrowed documents." + e.getMessage());
        }
    }

    @FXML
    private void handleReturnAction(ActionEvent event) {
        // Get the selected borrow record
        Borrow selectedBorrow = borrowedTable.getSelectionModel().getSelectedItem();
        if (selectedBorrow != null) {
            try {
                borrowDAO.deleteBorrow(selectedBorrow.getBorrowId());
                borrowedList.remove(selectedBorrow);
                announcementField.setText("Document returned successfully.");
            } catch (SQLException e) {
                announcementField.setText("Failed to return the document." + e.getMessage());
            }
        } else {
            announcementField.setText("Please select a document to return.");
        }
    }


}
