package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import com.librarymanagement.dao.DocumentDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import com.librarymanagement.model.Thesis;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.sql.SQLException;
import java.awt.*;

public class ManageDocumentController {

    @FXML public TextField titleField;
    @FXML public TextField authorNameField;
    @FXML public TextField documentIDField;
    @FXML public ChoiceBox<String> documentTypeField;
    @FXML public ChoiceBox<Boolean> isAvailableField;
    @FXML public TextField isbnField;
    @FXML public TextField academicAdvisorField;

    @FXML private TableView<Document> documentTableView;
    @FXML private TableColumn<Document, Integer> documentIdColumn;
    @FXML private TableColumn<Document, String> documentTitleColumn;
    @FXML private TableColumn<Document, String> authorColumn;
    @FXML private TableColumn<Document, Boolean> isAvailableColumn;
    @FXML private TableColumn<Document, String> typeColumn;
    @FXML private TableColumn<Document, String> isbnColumn;
    @FXML private TableColumn<Document, String> academicAdvisorColumn;

    private DocumentDAO documentDAO = new DocumentDAO();

    private String title;
    private String authorName;
    private String documentID;
    private String documentType;
    private String isbn;
    private String academicAdvisor;

    /**
     * Initializes the controller and sets up the table view.
     * This includes setting up the columns with their appropriate cell value factories
     * and loading data from the database.
     */
    @FXML
    public void initialize() {
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        documentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
      typeColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));

        // Dynamically set values for `isbn` and `academicAdvisor` based on the document type
        isbnColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof Book) {
                return new SimpleStringProperty(((Book) data.getValue()).getIsbn());
            }
            return new SimpleStringProperty("");
        });
        academicAdvisorColumn.setCellValueFactory(data -> {
            if (data.getValue() instanceof Thesis) {
                return new SimpleStringProperty(((Thesis) data.getValue()).getAcademicAdvisor());
            }
            return new SimpleStringProperty("");
        });
        // Load data from the database into the table
        loadDocumentData();
    }

    /**
     * Navigates to the Admin home page.
     *
     * @param actionEvent The event triggered by the admin action.
     * @throws Exception If navigation fails.
     */
    public void handleToHomePageAdmin(ActionEvent actionEvent) throws Exception {
        LibraryManagementApp.showAdminPage();
    }

    /**
     * Handles adding a new document to the library.
     * The method gathers input data, validates it, and adds the document
     * to both the database and the table view.
     *
     * @param actionEvent the action event triggered when the "Add Document" button is clicked
     */
    public void handleAddDocument(ActionEvent actionEvent) {
        try {
            // Retrieve information from UI fields
            String title = titleField.getText();
            String author = authorNameField.getText();
            String documentType = documentTypeField.getValue();
            boolean isAvailable = isAvailableField.getValue();

            // Check document type
            Document document;
            if ("Book".equalsIgnoreCase(documentType)) {
                String isbn = isbnField.getText();
                document = new Book(title, author, isbn);
            } else if ("Thesis".equalsIgnoreCase(documentType)) {
                String academicAdvisor = academicAdvisorField.getText();
                document = new Thesis(title, author, academicAdvisor);
            } else {
                // Show error if document type is invalid
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a valid document type!");
                return;
            }
            document.setIsAvailable(isAvailable);

            // Add document to the database
            documentDAO.addDocument(document);

            // Add document to the table view
            documentTableView.getItems().add(document);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "The document has been successfully added!");

            // Clear input fields after adding
            clearFields();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the document to the database.");
        }

    }

    /**
     * Displays an alert with the specified type, title, and message.
     *
     * @param alertType The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert window.
     * @param message The message to be displayed in the alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears all input fields in the form.
     */
    private void clearFields() {
        titleField.clear();
        authorNameField.clear();
        documentIDField.clear();
        documentTypeField.setValue(null);
        isAvailableField.setValue(null);
        isbnField.clear();
        academicAdvisorField.clear();
    }

    /**
     * Retrieves all documents from the database and loads them into the table view.
     * Clears existing data in the table view before adding the new documents.
     * Displays an error alert if there is an issue during the data retrieval process.
     */
    private void loadDocumentData() {
        try {
            // Retrieve all documents from the database
            List<Document> documents = documentDAO.getAllDocuments();

            // Clear any existing data in the table view
            documentTableView.getItems().clear();

            // Add all documents to the table view
            documentTableView.getItems().addAll(documents);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading document data from the database.");
        }
    }

    /**
     * Handles updating an existing document in the library.
     * This method allows users to modify document details and saves changes to the database.
     *
     * @param actionEvent the action event triggered when the "Update Document" button is clicked
     */
    public void handleUpdateDocument(ActionEvent actionEvent) {
        Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();
        if (selectedDocument == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a document to update.");
            return;
        }

        String currentDocumentID = documentIDField.getText();
        if (currentDocumentID == null || currentDocumentID.isEmpty()) {
            // Display document details in input fields for editing
            documentIDField.setText(String.valueOf(selectedDocument.getId()));
            titleField.setText(selectedDocument.getTitle());
            authorNameField.setText(selectedDocument.getAuthor());
            documentTypeField.setValue(selectedDocument instanceof Book ? "Book" : "Thesis");
            isAvailableField.setValue(selectedDocument.isAvailable());
            if (selectedDocument instanceof Book) {
                isbnField.setText(((Book) selectedDocument).getIsbn());
                academicAdvisorField.clear();
            } else if (selectedDocument instanceof Thesis) {
                academicAdvisorField.setText(((Thesis) selectedDocument).getAcademicAdvisor());
                isbnField.clear();
            }
            showAlert(Alert.AlertType.INFORMATION, "Notice", "You are editing a document. Please update the information and click Update again.");
        } else {
            try {
                // Retrieve updated information from input fields
                int documentId = Integer.parseInt(documentIDField.getText());
                String newTitle = titleField.getText();
                String newAuthor = authorNameField.getText();
                boolean newIsAvailable = isAvailableField.getValue();
                String documentType = documentTypeField.getValue();
                String newIsbn = isbnField.getText();
                String newAcademicAdvisor = academicAdvisorField.getText();

                // Update database and table view accordingly
                documentDAO.changeTitle(documentId, newTitle);
                documentDAO.changeAuthor(documentId, newAuthor);
                documentDAO.changeAvailable(documentId, newIsAvailable);
                if (!selectedDocument.getDocumentType().equals(documentType)) {
                    documentDAO.updateDocumentType(documentId, documentType);

                    // Handle type change logic
                    if ("Book".equalsIgnoreCase(documentType)) {
                        selectedDocument = new Book(newTitle, newAuthor, newIsbn);
                    } else if ("Thesis".equalsIgnoreCase(documentType)) {
                        selectedDocument = new Thesis(newTitle, newAuthor, newAcademicAdvisor);
                    }
                    selectedDocument.setId(documentId);
                    selectedDocument.setIsAvailable(newIsAvailable);

                    // Update on TableView
                    documentTableView.getItems().remove(selectedDocument);
                    documentTableView.getItems().add(selectedDocument);
                } else {
                    // Update existing document properties
                    selectedDocument.setTitle(newTitle);
                    selectedDocument.setAuthor(newAuthor);
                    selectedDocument.setIsAvailable(newIsAvailable);

                    if (selectedDocument instanceof Book) {
                        ((Book) selectedDocument).setIsbn(newIsbn);
                    } else if (selectedDocument instanceof Thesis) {
                        ((Thesis) selectedDocument).setAcademicAdvisor(newAcademicAdvisor);
                    }
                }

                // Refresh table
                documentTableView.refresh();

               //Clear all fields
                clearFields();

                // Show notice
                showAlert(Alert.AlertType.INFORMATION, "Success", "Document details have been successfully updated!");

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the document in the database.");
            }
        }
    }

    public void handleDeleteDocument(ActionEvent actionEvent) {
        Document selectedDocument = documentTableView.getSelectionModel().getSelectedItem();

        if (selectedDocument == null) {
            // Show notice if no document is chosen
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a document to delete.");
            return;
        }

        try {
            // Delete document from database
            documentDAO.deleteDocument(selectedDocument.getId());

            // Delete document from TableView
            documentTableView.getItems().remove(selectedDocument);

            // Show successfull notice
            showAlert(Alert.AlertType.INFORMATION, "Success", "The document has been successfully deleted!");
        } catch (SQLException e) {
            e.printStackTrace();
            // Show error notice
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the document from the database.");
        }
    }
}

