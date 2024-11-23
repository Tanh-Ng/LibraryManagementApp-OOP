package com.librarymanagement.UI;

import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.dao.DocumentDAO;
import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import com.librarymanagement.model.User;
import com.librarymanagement.model.Borrow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.sql.SQLException;

public class HomePageAdminController {

    @FXML private TableView<User> userDetails;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userPasswordColumn;

    @FXML private TableView<Document> documentDetails;
    @FXML private TableColumn<Document, Integer> documentIdColumn;
    @FXML private TableColumn<Document, String> documentTitleColumn;
    @FXML private TableColumn<Document, String> authorColumn;
    @FXML private TableColumn<Document, Boolean> isAvailableColumn;
    @FXML private TableColumn<Document, String> isbnColumn;
    @FXML private TableColumn<Document, String> documentTypeColumn;

    @FXML private TableView<Borrow> borrowedTable;
    @FXML private TableColumn<Borrow, Integer> borrowIdColumn;
    @FXML private TableColumn<Borrow, Integer> userIDColumn;
    @FXML private TableColumn<Borrow, Integer> documentIDColumn;
    @FXML private TableColumn<Borrow, Date> borrowDateColumn;
    @FXML private TextField searchField;

    @FXML private Label numberOfDocuments;
    @FXML private Label numberOfUsers;
    @FXML private Label numberOfType;
    @FXML private Label numberOfIssued;

    private final UserDAO userDAO = new UserDAO();
    private final DocumentDAO documentDAO = new DocumentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<Document> documentList = FXCollections.observableArrayList();
    private ObservableList<Borrow> borrowList = FXCollections.observableArrayList();

    /**
     * Initializes the controller and sets up data for user, document, and borrow tables.
     */
    public void initialize() {
        setupUserTable();
        setupDocumentTable();
        setupBorrowedTable();
        loadUsers();
        loadDocuments();
        loadBorrowedDocuments();
    }

    /**
     * Configures columns for the user table.
     */
    private void setupUserTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userPasswordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        userDetails.setItems(userList);
    }

    /**
     * Configures columns for the document table.
     */
    private void setupDocumentTable() {
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("DocumentId"));
        documentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("Available"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        documentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("BookType"));
        documentDetails.setItems(documentList);
    }

    /**
     * Configures columns for the borrowed documents table, including custom formatting for dates.
     */
    private void setupBorrowedTable() {
        borrowIdColumn.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        documentIDColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));

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
        borrowedTable.setItems(borrowList);
    }

    /**
     * Loads all users from the database into the user table.
     */
    private void loadUsers() {
        try {
            userList.setAll(userDAO.getAllUsers());
            updateNumberOfUsers();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load users", e.getMessage());
        }
    }

    /**
     * Loads all documents from the database into the document table.
     */
    private void loadDocuments() {
        try {
            documentList.setAll(documentDAO.getAllDocuments());
            updateNumberOfDocuments();
            updateNumberOfBookTypes();
            updateNumberOfIssuedDocuments();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load documents", e.getMessage());
        }
    }

    /**
     * Loads all borrowed records from the database into the borrowed table.
     */
    private void loadBorrowedDocuments() {
        try {
            borrowList.setAll(borrowDAO.getAllBorrowedDocuments()); // Sử dụng DAO để lấy danh sách Borrow
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load borrowed documents", e.getMessage());
        }
    }

    /**
     * Updates the count of documents in the `numberOfDocuments` label.
     */
    private void updateNumberOfDocuments() {
        int count = documentList.size();
        numberOfDocuments.setText(String.valueOf(count));
    }

    /**
     * Updates the count of users in the `numberOfUsers` label.
     */
    private void updateNumberOfUsers() {
        int count = userList.size();
        numberOfUsers.setText(String.valueOf(count));
    }

    /**
     * Updates the count of distinct book types in the `numberOfType` label.
     */
    private void updateNumberOfBookTypes() {
        Set<Book.BookType> bookTypes = new HashSet<>();
        for (Document doc : documentList) {
            if (doc instanceof Book book) {
                bookTypes.add(book.getBookType());
            }
        }
        numberOfType.setText(String.valueOf(bookTypes.size()));
    }

    /**
     * Updates the count of issued documents in the `numberOfIssued` label.
     */
    private void updateNumberOfIssuedDocuments() {
        try {
            int totalBorrowed = borrowDAO.getTotalBorrowedDocuments();
            numberOfIssued.setText(String.valueOf(totalBorrowed));
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not fetch issued document count", e.getMessage());
        }
    }


    /**
     * Searches for borrowed records by user ID and updates the borrowed table with the results.
     */
    @FXML
    private void handleSearchById() {
        String userIdText = searchField.getText().trim();

        if (userIdText.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Input required", "Please enter a User ID to search.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdText);
            ObservableList<Borrow> filteredList = FXCollections.observableArrayList();

            // Filter information by User ID
            for (Borrow borrow : borrowDAO.getAllBorrowedDocuments()) {
                if (borrow.getUserId() == userId) {
                    filteredList.add(borrow);
                }
            }

            // Update data into table
            borrowedTable.setItems(filteredList);

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Invalid Input", "User ID must be a valid number.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", e.getMessage());
        }
    }

    /**
     * Displays all borrowed records in the table.
     */
    @FXML
    private void handleSeeAll() {
        loadBorrowedDocuments();
        borrowedTable.setItems(borrowList);
        borrowedTable.refresh();
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

}
