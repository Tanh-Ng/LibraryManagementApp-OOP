package com.librarymanagement.UI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.Document;
import com.librarymanagement.dao.DocumentDAO;

import javafx.fxml.FXML;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import javafx.stage.Stage;
import javafx.util.Duration;

import static com.librarymanagement.UI.ImageLoader.getImage;

public class HomePageUserController {
    private final DocumentDAO documentDAO = new DocumentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @FXML
    public TextField searchStringField;

    @FXML
    public ListView<String> resultListView;

    @FXML
    private VBox itemsContainer; // The container for dynamic items (rows)

    public static List<Document> documents;

    public List<Borrow> borrowedDocuments;

    public void initialize() {
        try {
            // DAO initialization and data fetching
            borrowedDocuments = borrowDAO.getAllBorrowedDocuments();
            documents = documentDAO.getAllDocuments();

            // Load images beforehand using multi-thread
            documents.forEach(doc -> {
                if (doc instanceof Book book) {
                    ImageLoader.preloadImage(book.getImageUrl());
                }
            });

            // Populate the rows
            itemsContainer.getChildren().add(createDocumentList("Borrowed Documents"));
            itemsContainer.getChildren().add(createDocumentList("SCIENCE_FICTION"));
            itemsContainer.getChildren().add(createDocumentList("FANTASY"));
            itemsContainer.getChildren().add(createDocumentList("ROMANCE"));
            itemsContainer.getChildren().add(createDocumentList("TEXTBOOKS"));
            itemsContainer.getChildren().add(createDocumentList("BIOGRAPHY"));
            itemsContainer.getChildren().add(createDocumentList("RELIGIOUS"));
            itemsContainer.getChildren().add(createDocumentList("ART"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// Menu tab
    // Logout
    public void handleLogout() throws Exception {
        LibraryManagementApp.showLoginScreen();
    }

    // Change password
    public void handleAccountSettings() throws IOException {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserFXML/AccountSetting.fxml"));
        AnchorPane anchorPane = loader.load();

        // Create a new pop-up window;
        Stage stage = new Stage();
        stage.setTitle("Account Settings");
        Scene scene = new Scene(anchorPane);
        stage.setScene(scene);

        // Show the stage
        stage.show();
    }


    /// Search function
    //Search document after clicked
    public void handleSearchDocument() throws Exception {
        searchStringField.textProperty().addListener(
                (observable, oldValue, newValue) -> onSearch(newValue)
        );
    }

    //Search button
    public void handleOnClickSearch() {
        String searchString = searchStringField.getText();
        searchStringField.setText("");
        resultListView.setVisible(true);
        onSearch(searchString);
    }

    private void onSearch(String newValue) {
        if (Objects.equals(newValue, "")) {
            resultListView.setVisible(false);
        } else {
            resultListView.getItems().clear();
            resultListView.setVisible(true);
            //list search Document
            for (Document searchDocument : documents) {
                if (searchDocument.getTitle().toLowerCase().contains(newValue.toLowerCase())) {
                    resultListView.getItems().add(searchDocument.getTitle() + " ------ " + searchDocument.getAuthor());
                }
            }
            //value if no Document
            if (resultListView.getItems().isEmpty()) {
                resultListView.getItems().add("No document found.");
            }
            resultListView.setFixedCellSize(23.75);
            resultListView.setPrefHeight(23.75 * resultListView.getItems().size());
        }
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
        //event when mouse entered and exited
        resultListView.setCellFactory(ListView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setOnMouseMoved(event -> {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                        pauseTransition.setOnFinished(e -> {
                            showBookDetails(item, event);
                        });
                        pauseTransition.playFromStart();
                    });
                    setOnMouseExited(event -> {
                        setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;");
                        bookDetailsBox.setVisible(false);
                        pauseTransition.stop();
                    });
                }
            }
        });
    }


    private static Book pickedBook = new Book("NULL");
    @FXML
    private ImageView bookCoverImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label publishDateLabel;
    @FXML
    private ImageView qrCodeImageView;

    // Set the book details into the popup
    public void setBookDetails() {
        // Set the cover image
        Image coverImage = new Image(pickedBook.getImageUrl());
        bookCoverImageView.setImage(coverImage);
        setQrCodeImage(pickedBook.getInfoUrl());
        // Set the book details
        titleLabel.setText("Title: " + pickedBook.getTitle());
        authorLabel.setText("Author: " + pickedBook.getAuthor());
        publisherLabel.setText("Publisher: " + pickedBook.getPublisher());
        publishDateLabel.setText("Published Date: " + pickedBook.getPublishDate());
    }

    // Fetch and set the barcode image
    private void setQrCodeImage(String documentUrl) {
        try {
            // QR Code API URL
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + documentUrl;

            // Load the QR code image
            Image qrCodeImage = new Image(qrCodeUrl);

            // Set the QR code image
            qrCodeImageView.setImage(qrCodeImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public HBox bookDetailsBox;

    @FXML
    private void handlePickDocument(MouseEvent event) {
        String documentTitleAuthor = resultListView.getSelectionModel().getSelectedItem();
        showBookDetails(documentTitleAuthor, event);
    }

    private void showBookDetails(String title, MouseEvent event) {
        for (Document searchDocument : documents) {
            if (searchDocument.getTitle().equalsIgnoreCase(title.split(" ------ ")[0])) {
                pickedBook = (Book) searchDocument;
                break;
            }
        }
        // If the book details were fetched successfully, show them in the popup
        if (!pickedBook.getIsbn().equals("NULL")) {
            setBookDetails();
            bookDetailsBox.setLayoutY(event.getSceneY() + 5);
            bookDetailsBox.setLayoutX(event.getSceneX() + 5);
            bookDetailsBox.setVisible(true);
        }
    }

    /// Generate Document Lists
    // Create a VBox consist of Documents based on its type
    private VBox createDocumentList(String categoryText) {
        VBox vbox = new VBox(10);
        vbox.setStyle("-fx-padding: 10;");

        // Create a horizontal container for the category text and "See More" button
        HBox topHBox = new HBox(10);
        topHBox.setStyle("-fx-padding: 5;");

        // Category label (on the left)
        Text categoryLabel = new Text(categoryText);
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // "See More" button (on the right)
        Button seeMoreButton = new Button("See More");
        seeMoreButton.setStyle("-fx-font-size: 12px;");
        HBox.setHgrow(seeMoreButton, Priority.ALWAYS);

        topHBox.getChildren().addAll(categoryLabel, seeMoreButton);

        // Create left and right buttons for scrolling (inside the HBox for the ScrollPane)
        Button leftButton = new Button("<");
        leftButton.setStyle("-fx-font-size: 18px;");
        Button rightButton = new Button(">");
        rightButton.setStyle("-fx-font-size: 18px;");

        // Create the ScrollButtonHBox to hold the content
        HBox contentHBox = new HBox(10);
        contentHBox.setStyle("-fx-padding: 10;");

        // Get documents for this category
        List<Document> documentsByType = categoryText.equals("Borrowed Documents")
                ? getBorrowedDocumentsList()
                : getDocumentListByType(categoryText);
        final int paneCount = 5; // Number of panes to show at a time
        final int[] currentIndex = {0}; // Keep track of the starting index

        // Initialize content for the first view
        updateContent(contentHBox, documentsByType, currentIndex[0], paneCount, leftButton, rightButton);

        // Attach event handlers to buttons
        leftButton.setOnAction(event -> {
            if (currentIndex[0] - paneCount >= 0) {
                currentIndex[0] -= paneCount;
                updateContent(contentHBox, documentsByType, currentIndex[0], paneCount, leftButton, rightButton);
            }
        });

        rightButton.setOnAction(event -> {
            if (currentIndex[0] + paneCount < documentsByType.size()) {
                currentIndex[0] += paneCount;
                updateContent(contentHBox, documentsByType, currentIndex[0], paneCount, leftButton, rightButton);
            }
        });

        // Wrap the HBox and buttons in a ScrollPane
        HBox scrollButtonsHBox = new HBox(10, leftButton, contentHBox, rightButton);
        scrollButtonsHBox.setAlignment(Pos.CENTER);

        // Add everything to the main VBox
        vbox.getChildren().addAll(topHBox, scrollButtonsHBox);

        return vbox;
    }

    // Method to update content in the HBox
    private void updateContent(HBox contentHBox, List<Document> documents, int startIndex, int count, Button leftButton, Button rightButton) {
        contentHBox.getChildren().clear();
        int endIndex = Math.min(startIndex + count, documents.size());

        // Add actual panes for the documents in range
        for (int i = startIndex; i < endIndex; i++) {
            AnchorPane anchorPane = createAnchorPane(documents.get(i));
            contentHBox.getChildren().add(anchorPane);
        }

        // Add placeholder panes if fewer than 5 panes are displayed
        for (int i = endIndex; i < startIndex + count; i++) {
            AnchorPane placeholderPane = createPlaceholderPane();
            contentHBox.getChildren().add(placeholderPane);
        }

        // Update button visibility
        leftButton.setDisable(startIndex == 0); // Disable "left" if at the beginning
        rightButton.setDisable(startIndex + count >= documents.size()); // Disable "right" if at the end
    }

    private AnchorPane createAnchorPane(Document document) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 220px; -fx-pref-width: 160px;");
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(0.5));
        if (document instanceof Book book) {
            // Preload the book's image in a background thread
            String imageUrl = book.getImageUrl();
            ImageLoader.preloadImage(imageUrl);

            // Create an ImageView for the book cover
            ImageView coverImageView = new ImageView();
            coverImageView.setFitHeight(150); // Set desired height
            //coverImageView.setFitWidth(120); // Set desired width
            coverImageView.setPreserveRatio(true);
            coverImageView.setImage(getImage(imageUrl)); // Retrieve preloaded image

            // Position the ImageView at the top center
            AnchorPane.setTopAnchor(coverImageView, 10.0);
            AnchorPane.setLeftAnchor(coverImageView, 30.0);

            // Event when mouse move -> show details
            anchorPane.setOnMouseMoved(event -> {
                anchorPane.setStyle("-fx-background-color: #ffcccc; " +
                        "-fx-pref-height: 220px; -fx-pref-width: 160px;");
                pauseTransition.setOnFinished(e -> {
                    showBookDetails(book.getTitle() + " ------ ", event);
                });
                pauseTransition.playFromStart();
            });

            // When mouse exited -> delete book details screen
            anchorPane.setOnMouseExited(event -> {
                anchorPane.setStyle("-fx-background-color: lightgray; " +
                        "-fx-pref-height: 220px; -fx-pref-width: 160px;");
                bookDetailsBox.setVisible(false);
                pauseTransition.stop();
            });

            // Borrow button at the bottom
            Button borrowButton = new Button();
            updateBorrowButtonState(borrowButton, document);

            borrowButton.setOnAction(event -> {
                if (isBorrowed(document)) {
                    returnDocument(document);
                } else {
                    borrowDocument(document);
                }
                updateBorrowButtonState(borrowButton, document);
                refreshBorrowedDocumentsList(String.valueOf(book.getBookType()));
            });
            borrowButton.setPrefWidth(100);
            AnchorPane.setBottomAnchor(borrowButton, 10.0);
            AnchorPane.setLeftAnchor(borrowButton, 30.0);

            anchorPane.getChildren().addAll(coverImageView, borrowButton);
        }
        return anchorPane;
    }

    // Create a placeholder AnchorPane for empty spaces
    private AnchorPane createPlaceholderPane() {
        AnchorPane placeholderPane = new AnchorPane();
        placeholderPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 220px; -fx-pref-width: 160px;");
        return placeholderPane;
    }

    private void updateBorrowButtonState(Button button, Document document) {
        if (!isAvailable(document)) {
            button.setText("Unavailable");
            button.setDisable(true);
            button.setStyle("-fx-background-color: #ffcc66; -fx-text-fill: black;"); // Yellow-orange background
        } else if (isBorrowed(document)) {
            button.setText("Borrowed");
            button.setDisable(false);
            button.setStyle("-fx-background-color: #ff9999; -fx-text-fill: black;");
        } else {
            button.setText("Borrow");
            button.setDisable(false);
            button.setStyle("-fx-background-color: #99ff99; -fx-text-fill: black;");
        }
    }

    private boolean isAvailable(Document document) {
        return document.isAvailable();
    }

    private boolean isBorrowed(Document document) {
        return borrowedDocuments.stream().anyMatch(b -> b.getDocumentId() == document.getDocumentId());
    }

    private void borrowDocument(Document document) {
        try {
            borrowDAO.addBorrow(LibraryManagementApp.getCurrentUser().getUserId(), document.getDocumentId(), new Date(System.currentTimeMillis()));
            borrowedDocuments.add(new Borrow(borrowedDocuments.size(), LibraryManagementApp.getCurrentUser().getUserId()
                    , document.getDocumentId(), new Timestamp(System.currentTimeMillis())));
        } catch (Exception e) {
            System.out.println("Error borrowing document: " + e.getMessage());
        }
    }

    private void returnDocument(Document document) {
        try {
            Borrow borrow = borrowedDocuments.stream()
                    .filter(b -> b.getDocumentId() == document.getDocumentId())
                    .findFirst()
                    .orElse(null);
            if (borrow != null) {
                borrowDAO.deleteBorrow(borrow.getBorrowId());
                borrowedDocuments.remove(borrow);
            }
        } catch (Exception e) {
            System.out.println("Error returning document: " + e.getMessage());
        }
    }

    // Refresh the VBox of borrowed documents list after borrowing or returning
    private void refreshBorrowedDocumentsList(String category) {
        try {
            // Remove the first child (the Borrowed Documents section)
            if (!itemsContainer.getChildren().isEmpty()) {
                itemsContainer.getChildren().remove(0);
            }

            // Add the updated Borrowed Documents section
            itemsContainer.getChildren().add(0, createDocumentList("Borrowed Documents"));

            VBox categoryListVBox = createDocumentList(category); // This regenerates the document list for the category
            for (int i = 1; i < itemsContainer.getChildren().size(); i++) {  // Start from index 1 to skip "Borrowed Documents"
                VBox existingVBox = (VBox) itemsContainer.getChildren().get(i);
                Text categoryLabel = (Text) ((HBox) existingVBox.getChildren().get(0)).getChildren().get(0);

                // If the category matches, replace the VBox with the new one
                if (categoryLabel.getText().equals(category)) {
                    itemsContainer.getChildren().set(i, categoryListVBox);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get document list by type
    private List<Document> getDocumentListByType(String categoryText) {
        // Validate input and ensure the categoryText matches an enum value in BookType
        if (categoryText == null || categoryText.isEmpty()) {
            return null; // Return an empty list for invalid input
        }

        // Parse the categoryText into a BookType enum
        Book.BookType selectedType;
        try {
            selectedType = Book.BookType.valueOf(categoryText.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid category: " + categoryText);
            return null;
        }

        // Filter and sort documents by the selected BookType
        return documents.stream()
                .filter(doc -> doc instanceof Book && ((Book) doc).getBookType() == selectedType)
                .toList();
    }

    // Convert borrow list to document list
    private List<Document> getBorrowedDocumentsList() {
        return documents.stream()
                .filter(doc -> borrowedDocuments.stream()
                        .anyMatch(borrow -> borrow.getDocumentId() == doc.getDocumentId()))
                .toList();
    }

}
