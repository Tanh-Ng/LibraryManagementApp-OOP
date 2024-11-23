package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.librarymanagement.dao.DocumentDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;

import java.sql.SQLException;
import java.util.List;

public class ManageDocumentController {

    @FXML private TextField titleField;
    @FXML private TextField authorNameField;
    @FXML private TextField documentIDField;
    @FXML private ChoiceBox<Boolean> isAvailableField;
    @FXML private TextField isbnField;
    @FXML private ChoiceBox<Book.BookType> bookTypeChoiceBox; // ChoiceBox for book type

    @FXML private TableView<Document> documentTableView;
    @FXML private TableColumn<Document, Integer> documentIdColumn;
    @FXML private TableColumn<Document, String> documentTitleColumn;
    @FXML private TableColumn<Document, String> authorColumn;
    @FXML private TableColumn<Document, Boolean> isAvailableColumn;
    @FXML private TableColumn<Document, String> isbnColumn;

    private final DocumentDAO documentDAO = new DocumentDAO();

    /**
     * Initializes the controller and sets up the table view and book type choice box.
     */
    @FXML
    public void initialize() {
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        documentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        isbnColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof Book) {
                return new SimpleStringProperty(((Book) data.getValue()).getIsbn());
            }
            return new SimpleStringProperty("");
        });

        // Initialize the book type choice box with available book types
        bookTypeChoiceBox.getItems().setAll(Book.BookType.values());

        // Load document data into the table view
        loadDocumentData();
    }

    /**
     * Navigates back to the Admin home page.
     */
    public void handleToHomePageAdmin(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    /**
     * Handles adding a new book document to the library.
     */
    public void handleAddDocument(ActionEvent actionEvent) {
        try {
            // Retrieve input data
            String title = titleField.getText();
            String author = authorNameField.getText();
            String isbn = isbnField.getText();
            boolean isAvailable = isAvailableField.getValue();
            Book.BookType bookType = bookTypeChoiceBox.getValue(); // Get selected book type

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || bookType == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields!");
                return;
            }

            // Create a new book with book type
            Document book = new Book(title, author, isbn, bookType);
            book.setIsAvailable(isAvailable);

            // Add book to the database
            documentDAO.addDocument(book);

            // Add book to the table view
            documentTableView.getItems().add(book);

            // Show success message and clear input fields
            showAlert(Alert.AlertType.INFORMATION, "Success", "The book has been successfully added!");
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the book.");
        }
    }

    /**
     * Clears all input fields.
     */
    private void clearFields() {
        titleField.clear();
        authorNameField.clear();
        documentIDField.clear();
        isAvailableField.setValue(null);
        isbnField.clear();
        bookTypeChoiceBox.setValue(null); // Clear book type choice box
    }

    /**
     * Loads document data from the database into the table view.
     */
    private void loadDocumentData() {
        try {
            List<Document> documents = documentDAO.getAllDocuments();
            documentTableView.getItems().clear();
            documentTableView.getItems().addAll(documents);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading data.");
        }
    }

    /**
     * Handles updating an existing book document.
     */
    public void handleUpdateDocument(ActionEvent actionEvent) {
        Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a document to update.");
            return;
        }

        try {
            // Get updated details from input fields
            String newTitle = titleField.getText();
            String newAuthor = authorNameField.getText();
            String newIsbn = isbnField.getText();
            boolean newIsAvailable = isAvailableField.getValue();
            Book.BookType newBookType = bookTypeChoiceBox.getValue(); // Get updated book type

            // Validate that the book type is selected
            if (newBookType == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a book type.");
                return;
            }

            // Update in database
            documentDAO.changeTitle(selectedDocument.getId(), newTitle);
            documentDAO.changeAuthor(selectedDocument.getId(), newAuthor);
            documentDAO.changeAvailable(selectedDocument.getId(), newIsAvailable);

            if (selectedDocument instanceof Book) {
                ((Book) selectedDocument).setIsbn(newIsbn);
                ((Book) selectedDocument).setBookType(newBookType); // Update book type
            }

            // Update the table view and clear fields
            selectedDocument.setTitle(newTitle);
            selectedDocument.setAuthor(newAuthor);
            selectedDocument.setIsAvailable(newIsAvailable);
            documentTableView.refresh();
            clearFields();

            showAlert(Alert.AlertType.INFORMATION, "Success", "The book has been successfully updated!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the book.");
        }
    }

    /**
     * Handles deleting a book document.
     */
    public void handleDeleteDocument(ActionEvent actionEvent) {
        Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();

        if (selectedDocument == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a document to delete.");
            return;
        }

        try {
            // Delete from database
            documentDAO.deleteDocument(selectedDocument.getId());

            // Remove from table view
            documentTableView.getItems().remove(selectedDocument);

            showAlert(Alert.AlertType.INFORMATION, "Success", "The book has been successfully deleted!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the book.");
        }
    }

    /**
     * Displays an alert dialog with the specified details.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
