package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Borrow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn; 
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ManageBorrowController {

    @FXML
    private TableView<Borrow> borrowTable;

    @FXML
    private TableColumn<Borrow, Integer> borrowIdColumn;

    @FXML
    private TableColumn<Borrow, Integer> userIdColumn;

    @FXML
    private TableColumn<Borrow, Integer> documentIdColumn;

    @FXML
    private TableColumn<Borrow, Date> borrowDateColumn;

    @FXML
    private TableColumn<Borrow, Integer> durationColumn;

    private BorrowDAO borrowDAO = new BorrowDAO();
    private ObservableList<Borrow> borrowList = FXCollections.observableArrayList();


    public void initialize() {
        setupBorrowedTable();
        loadBorrowedDocuments();
    }
    @FXML
    private void setupBorrowedTable() {
        borrowIdColumn.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("DurationDays"));

        // Format the borrow date to include time
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        borrowDateColumn.setCellFactory(new Callback<TableColumn<Borrow, Date>, TableCell<Borrow, Date>>() {
            @Override
            public TableCell<Borrow, Date> call(TableColumn<Borrow, Date> param) {
                return new TableCell<Borrow, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            setText(sdf.format(item));
                        }
                    }
                };
            }
        });
        borrowTable.setItems(borrowList);
    }

    private void loadBorrowedDocuments() {
        try {
            borrowList.setAll(borrowDAO.getAllBorrowedDocuments());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load borrowed documents", e.getMessage());
        }
    }

    /**
     * Utility method to show alerts to the user.
     *
     * @param alertType The type of alert (e.g., ERROR, WARNING, INFORMATION).
     * @param title     The title of the alert window.
     * @param header    The header text for the alert.
     * @param content   The main content message of the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Navigates back to the Admin home page.
     */
    public void handleToHomePageAdmin(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }
}
