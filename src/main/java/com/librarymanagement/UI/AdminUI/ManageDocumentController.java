package com.librarymanagement.UI.AdminUI;

import com.librarymanagement.UI.General.BookDetailsController;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.librarymanagement.dao.DocumentDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

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
    @FXML private TableColumn<Document, String> documentTypeColumn;

    @FXML
    private TextField isbnSearchField;
    private final DocumentDAO documentDAO = new DocumentDAO();

    @FXML
    private TextField textField;
    @FXML
    private ObservableList<Document> documentList = FXCollections.observableArrayList();


    /**
     * Initializes the controller and sets up the table view and book type choice box.
     */
    @FXML
    public void initialize() {
        documentIdColumn.setCellValueFactory(new PropertyValueFactory<>("DocumentId"));
        documentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        documentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("BookType"));
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
        // Set up row selection listener
        documentTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Fill the text fields with the selected document's details
                fillFieldsWithSelectedDocument(newValue);
            }
        });
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
            documentList.setAll(documentDAO.getAllDocuments());
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
            String newId = documentIDField.getText();
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

            // Check if the new ID is different from the current ID
            int currentDocumentId = selectedDocument.getDocumentId();
            if (!newId.equals(String.valueOf(currentDocumentId))) {
                // If the ID is different, update the document_id in the database
                int newDocumentId = Integer.parseInt(newId); // Convert the new ID from String to Integer
                documentDAO.changeDocumentId(currentDocumentId, newDocumentId);
                selectedDocument.setId(newDocumentId); // Update the selectedDocument's ID
            }

            documentDAO.changeTitle(selectedDocument.getDocumentId(), newTitle);
            documentDAO.changeAuthor(selectedDocument.getDocumentId(), newAuthor);
            documentDAO.changeAvailable(selectedDocument.getDocumentId(), newIsAvailable);
            documentDAO.changeBookType(selectedDocument.getDocumentId(), newBookType);
            if (selectedDocument instanceof Book) {
                ((Book) selectedDocument).setIsbn(newIsbn);
                ((Book) selectedDocument).setBookType(newBookType); // Update book type
            }

            // Update the table view and clear fields
            selectedDocument.setTitle(newTitle);
            selectedDocument.setAuthor(newAuthor);
            selectedDocument.setIsAvailable(newIsAvailable);
            documentTableView.refresh();

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
            documentDAO.deleteDocument(selectedDocument.getDocumentId());

            // Remove from table view
            documentTableView.getItems().remove(selectedDocument);

            showAlert(Alert.AlertType.INFORMATION, "Success", "The book has been successfully deleted!");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the book.");
        }
    }

    @FXML
    private void handleSearchByIsbn() {
        // Get ISBN from the input field
        String isbn = isbnSearchField.getText().trim();

        if (isbn.isEmpty()) {
            // Show an alert if ISBN is empty
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "ISBN cannot be empty.");
            return;
        }

        // Create a new Book object with the ISBN
        Book book = new Book(isbn);

        // Fetch book details using the API
        String bookDetails = book.fetchFromIsbn();

        // If the book details were fetched successfully, show them in the popup
        if (bookDetails.startsWith("Title:")) {
            try {
                // Load the BookDetails.fxml file
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/BookDetails.fxml"));
                AnchorPane anchorPane = loader.load();

                // Get the controller instance and set book details
                BookDetailsController bookDetailsController = loader.getController();
                bookDetailsController.setBookDetails(book);

                // Create a new stage for the popup
                Scene scene = new Scene(anchorPane);
                LibraryManagementApp.showBookDetailsPage(scene);

            } catch (Exception e) {
                e.printStackTrace();
                // Show an alert for any errors during the process
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading book details.");
            }
        } else {
            // Show an alert if book details are not found
            showAlert(Alert.AlertType.ERROR, "Error", bookDetails);
        }
    }

    @FXML
    public void handleAddFromApi(ActionEvent actionEvent) {
        // Get ISBN from the input field
        String isbn = isbnSearchField.getText().trim();

        if (isbn.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "ISBN cannot be empty.");
            return;
        }

        // Create a new Book object with the ISBN
        Book book = new Book(isbn);

        // Fetch book details using the API
        String bookDetails = book.fetchFromIsbn();

        // If the book details were fetched successfully, add them to the table
        if (bookDetails.startsWith("Title:")) {
            try {
                // Retrieve essential details from the fetched book information
                String title = book.getTitle();
                String author = book.getAuthor();
                String fetchedIsbn = book.getIsbn();
                Book.BookType bookType = Book.BookType.TEXTBOOKS;
                boolean isAvailable = true;  // Default availability for fetched books
                String imageUrl=book.getImageUrl();
                String infoUrl=book.getInfoUrl();
                String publisher=book.getPublisher();
                String publishDate=book.getPublishDate();
                if (title == null || author == null || bookType == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Some required book details are missing.");
                    return;
                }

                // Create a new Book instance with fetched details
                Book newBook = new Book(title, author, fetchedIsbn, bookType);
                newBook.setIsAvailable(isAvailable);
                newBook.setImageUrl(imageUrl);
                newBook.setInfoUrl(infoUrl);
                newBook.setPublisher(publisher);
                newBook.setPublishDate(publishDate);

                // Add the new book to the database
                documentDAO.addDocument(newBook);

                //
                documentDAO.addDisplayInformation(newBook);
                // Add the book to the table view
                documentTableView.getItems().add(newBook);

                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "The book has been successfully added!");

                // Clear the ISBN search field
                isbnSearchField.clear();

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the book.");
            }
        } else {
            // Show an alert if book details are not found
            showAlert(Alert.AlertType.ERROR, "Error", bookDetails);
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
    private void fillFieldsWithSelectedDocument(Document selectedDocument) {
        if (selectedDocument instanceof Book) {
            Book selectedBook = (Book) selectedDocument;

            // Set text fields with the document's details
            titleField.setText(selectedBook.getTitle());
            authorNameField.setText(selectedBook.getAuthor());
            isbnField.setText(selectedBook.getIsbn());
            isAvailableField.setValue(selectedBook.isAvailable());
            bookTypeChoiceBox.setValue(selectedBook.getBookType()); // Set book type
            isbnSearchField.setText(selectedBook.getIsbn());
            // Optionally set document ID (if needed)
            documentIDField.setText(String.valueOf(selectedBook.getDocumentId()));
        } else {
            // Clear the fields if the selected document is not a book
            clearFields();
        }
    }

    public void handleSearchById(ActionEvent actionEvent) {
        String searchIdString = textField.getText().trim();  // Get the value from the search field (Document ID)

        if (searchIdString.isEmpty()) {
            // If Document ID is empty, show an alert
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Please enter a valid Document ID.");
            return;
        }

        try {
            // Convert searchIdString to an integer
            int searchId = Integer.parseInt(searchIdString);

            // Assuming you have a method to search by Document ID from the database or the list
            ObservableList<Document> searchResults = FXCollections.observableArrayList();

            // Loop through the list of documents and check if the ID matches
            for (Document doc : documentList) { // documentList is your collection of documents
                if (doc.getDocumentId() == searchId) {
                    searchResults.add(doc);
                }
            }

            if (searchResults.isEmpty()) {
                // Show an alert if no document is found
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No document found with Document ID: " + searchId);
            } else {
                // Update the table with the search results
                documentTableView.setItems(searchResults);
            }
        } catch (NumberFormatException e) {
            // If unable to convert string to number, show an error alert
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Document ID must be a valid number.");
        }
    }

    public void handleSeeAll(ActionEvent actionEvent) {
        // Ensure that documentList is fully loaded from the database
        loadDocumentData();

        if (documentList.isEmpty()) {
            // If no documents were found, show an alert
            showAlert(Alert.AlertType.INFORMATION, "No Documents", "No documents found in the system.");
        } else {
            // Display all documents in the table
            documentTableView.setItems(documentList);
        }
    }


}
