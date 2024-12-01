package com.librarymanagement.UI.AdminUI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Borrow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    @FXML
    private TableColumn<Borrow, Integer> extendDurationColumn;

    @FXML
    private TextField searchField;

    private static ScheduledExecutorService scheduler;

    private BorrowDAO borrowDAO = new BorrowDAO();
    private ObservableList<Borrow> borrowList = FXCollections.observableArrayList();

    /**
     * Initializes the controller by setting up the borrowed table and loading borrowed documents.
     * Starts a background thread to periodically check for expired borrow records.
     */
    public void initialize() {
        setupBorrowedTable();
        loadBorrowedDocuments();

        startExpirationCheck();
    }

    /**
     * Sets up the table view for borrowed documents.
     * Configures the columns and formatting for displaying borrow data.
     */
    @FXML
    private void setupBorrowedTable() {
        // Set up column bindings
        borrowIdColumn.setCellValueFactory(new PropertyValueFactory<>("borrowId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("documentId"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("DurationDays"));
        extendDurationColumn.setCellValueFactory(new PropertyValueFactory<>("ExtendDurationRequest"));

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
        // Set the items for the table view
        borrowTable.setItems(borrowList);
    }

    /**
     * Loads all borrowed documents from the database and updates the table view.
     * Shows an error message if there is a problem with loading the data.
     */
    private void loadBorrowedDocuments() {
        try {
            ObservableList<Borrow> updatedList = FXCollections.observableArrayList(borrowDAO.getAllBorrowedDocuments());
            javafx.application.Platform.runLater(() -> borrowList.setAll(updatedList));
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

    @FXML
    private void handleDeleteBorrow(ActionEvent actionEvent) {
        // Get the selected borrow record from the table
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();

        if (selectedBorrow == null) {
            // Show alert if no record is selected
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Borrow Record Selected", "Please select a borrow record to delete.");
            return;
        }

        // Confirm deletion with the user
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Delete Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this borrow record?");
        confirmationAlert.setContentText("This action cannot be undone.");
        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete the borrow record using DAO
                    borrowDAO.deleteBorrow(selectedBorrow.getBorrowId());

                    // Reload the documents list and update the table
                    loadBorrowedDocuments();

                    // Show success message
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Borrow Record Deleted", "The borrow record has been deleted successfully.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while deleting the borrow record: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Handles the acceptance of a borrow duration extension request.
     * Updates the borrow record if the request is valid.
     */
    @FXML
    private void handleAcceptRequest(ActionEvent actionEvent) {
        // Get the selected borrow record from the table
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();

        if (selectedBorrow == null) {
            // Show alert if no record is selected
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Borrow Record Selected", "Please select a borrow record to accept the request.");
            return;
        }

        // Check if there's an extend request
        int extendRequest = selectedBorrow.getExtendDurationRequest();
        if (extendRequest <= 0) {
            // Show alert if there's no valid extend request
            showAlert(Alert.AlertType.INFORMATION, "No Request", "No Extend Request", "This borrow record does not have a valid extend request.");
            return;
        }

        try {
            // Update the borrow duration using DAO
            borrowDAO.updateBorrowDuration(selectedBorrow.getBorrowId(), extendRequest);

            // Reload the documents list and update the table
            loadBorrowedDocuments();

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Request Accepted", "Extend Duration Request Accepted", "The duration has been updated successfully.");
        } catch (SQLException e) {
            // Handle SQL error
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while updating the borrow record: " + e.getMessage());
        }
    }

    /**
     * Handles the denial of a borrow duration extension request.
     * Sets the extend duration request value to zero.
     */
    @FXML
    private void handleDenyRequest(ActionEvent actionEvent) {
        // Get the selected borrow record from the table
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();

        if (selectedBorrow == null) {
            // Show alert if no record is selected
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Borrow Record Selected", "Please select a borrow record to deny the request.");
            return;
        }

        // Check if there's an extend request
        int extendRequest = selectedBorrow.getExtendDurationRequest();
        if (extendRequest <= 0) {
            // Show alert if there's no valid extend request
            showAlert(Alert.AlertType.INFORMATION, "No Request", "No Extend Request", "This borrow record does not have a valid extend request.");
            return;
        }

        try {
            // Deny the extend request by setting its value to 0
            borrowDAO.denyExtendRequest(selectedBorrow.getBorrowId());

            // Reload the documents list and update the table
            loadBorrowedDocuments();

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Request Denied", "Extend Duration Request Denied", "The extend duration request has been denied successfully.");
        } catch (SQLException e) {
            // Handle SQL error
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while denying the request: " + e.getMessage());
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
            borrowTable.setItems(filteredList);

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
        borrowTable.setItems(borrowList);
        borrowTable.refresh();
    }

    /**
     * Starts a background thread that periodically checks for expired borrow records.
     * Expired records are deleted from the database every minute.
     */
    private void startExpirationCheck() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                borrowDAO.deleteExpiredBorrow();
                loadBorrowedDocuments();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Stops the background thread that checks for expired borrow records.
     */
    public static void stopExpirationCheck() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }


    /**
     * Handles the "See Request" button action.
     * Filters and displays only borrow records with an extend duration request greater than 0.
     * If no such records exist, shows an informational alert to the user.
     *
     * @param actionEvent The event triggered by clicking the "See Request" button.
     */
    @FXML
    private void handleSeeRequest(ActionEvent actionEvent) {
        try {
            // Filter borrow records with an extend duration request greater than 0
            ObservableList<Borrow> filteredList = FXCollections.observableArrayList();

            for (Borrow borrow : borrowDAO.getAllBorrowedDocuments()) {
                if (borrow.getExtendDurationRequest() > 0) {
                    filteredList.add(borrow);
                }
            }

            // Check if the filtered list is empty
            if (filteredList.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Notification", "No Extend Requests", "Currently, there are no records with extend duration requests.");
            } else {
                // Update the table with the filtered list
                borrowTable.setItems(filteredList);
                borrowTable.refresh();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while retrieving the list of extend requests: " + e.getMessage());
        }
    }
}
