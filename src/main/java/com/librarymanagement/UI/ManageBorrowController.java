package com.librarymanagement.UI;

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

    private ScheduledExecutorService scheduler;

    private BorrowDAO borrowDAO = new BorrowDAO();
    private ObservableList<Borrow> borrowList = FXCollections.observableArrayList();


    public void initialize() {
        setupBorrowedTable();
        loadBorrowedDocuments();

        startExpirationCheck();
    }
    @FXML
    private void setupBorrowedTable() {
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
        borrowTable.setItems(borrowList);
    }

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
    private void handleAcceptRequest(ActionEvent actionEvent) {
        // Lấy bản ghi được chọn trong bảng
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();

        if (selectedBorrow == null) {
            // Hiển thị thông báo nếu không có bản ghi nào được chọn
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Borrow Record Selected", "Please select a borrow record to accept the request.");
            return;
        }

        // Kiểm tra giá trị của extendDurationRequest
        int extendRequest = selectedBorrow.getExtendDurationRequest();
        if (extendRequest <= 0) {
            // Hiển thị thông báo nếu không có yêu cầu gia hạn
            showAlert(Alert.AlertType.INFORMATION, "No Request", "No Extend Request", "This borrow record does not have a valid extend request.");
            return;
        }

        try {
            // Gọi DAO để cập nhật thời gian mượn và đặt lại request
            borrowDAO.updateBorrowDuration(selectedBorrow.getBorrowId(), extendRequest);

            // Cập nhật lại danh sách hiển thị trên bảng
            loadBorrowedDocuments();

            // Hiển thị thông báo thành công
            showAlert(Alert.AlertType.INFORMATION, "Request Accepted", "Extend Duration Request Accepted", "The duration has been updated successfully.");
        } catch (SQLException e) {
            // Xử lý lỗi SQL
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "An error occurred while updating the borrow record: " + e.getMessage());
        }
    }

    @FXML
    private void handleDenyRequest(ActionEvent actionEvent) {
        // Lấy bản ghi được chọn trong bảng
        Borrow selectedBorrow = borrowTable.getSelectionModel().getSelectedItem();

        if (selectedBorrow == null) {
            // Hiển thị thông báo nếu không có bản ghi nào được chọn
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Borrow Record Selected", "Please select a borrow record to deny the request.");
            return;
        }

        // Kiểm tra giá trị của extendDurationRequest
        int extendRequest = selectedBorrow.getExtendDurationRequest();
        if (extendRequest <= 0) {
            // Hiển thị thông báo nếu không có yêu cầu gia hạn
            showAlert(Alert.AlertType.INFORMATION, "No Request", "No Extend Request", "This borrow record does not have a valid extend request.");
            return;
        }

        try {
            // Gọi DAO để đặt giá trị extendDurationRequest về 0
            borrowDAO.denyExtendRequest(selectedBorrow.getBorrowId());

            // Cập nhật lại danh sách hiển thị trên bảng
            loadBorrowedDocuments();

            // Hiển thị thông báo thành công
            showAlert(Alert.AlertType.INFORMATION, "Request Denied", "Extend Duration Request Denied", "The extend duration request has been denied successfully.");
        } catch (SQLException e) {
            // Xử lý lỗi SQL
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

    public void stopExpirationCheck() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public static void close() {
        stopExpirationCheck();
    }
}
