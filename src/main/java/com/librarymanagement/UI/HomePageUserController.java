package com.librarymanagement.UI;

import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.Document;
import  com.librarymanagement.dao.DocumentDAO;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomePageUserController {
    private final DocumentDAO documentDAO = new DocumentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @FXML
    public TextField searchStringField;

    @FXML
    public ListView<String> resultListView;

    public static List<Document> documents;

    public List<Borrow> borrowedDocuments;

    public void initialize() {
        try {
            // DAO initialization and data fetching
            borrowedDocuments = borrowDAO.getAllBorrowedDocuments();
            documents = documentDAO.getAllDocuments();
            // Populate the rows
            //itemsContainer.getChildren().add(createRowWithButtons("Borrowed Documents"));
            itemsContainer.getChildren().add(createDocumentList("FANTASY"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Search document after clicked
    public void handleSearchDocument() throws Exception {
        DocumentDAO documentDao = new DocumentDAO();
        try {
            documents = documentDao.getAllDocuments();
        } catch (Exception e) {
            resultListView.getItems().add("Error: " + e.getMessage());
        }
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
        }
        else {
            resultListView.getItems().clear();
            resultListView.setVisible(true);
            //list search Document
            for (Document searchDocument : documents) {
                if(searchDocument.getTitle().toLowerCase().contains(newValue.toLowerCase())) {
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
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
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
    @FXML private ImageView bookCoverImageView;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label publisherLabel;
    @FXML private Label publishDateLabel;
    @FXML private ImageView qrCodeImageView;
    @FXML private Button closeButton;

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
            if (searchDocument.getTitle().equalsIgnoreCase(title.split(" ------ ")[0])){
                pickedBook = (Book) searchDocument;
                break;
            }
        }
        // Fetch book details using the API
        String bookDetails = pickedBook.fetchFromIsbn();
        // If the book details were fetched successfully, show them in the popup
        if (bookDetails.startsWith("Title:") && !pickedBook.getIsbn().equals("NULL")) {
            setBookDetails();
            bookDetailsBox.setLayoutY(event.getSceneY()+5);
            bookDetailsBox.setLayoutX(event.getSceneX()+5);
            bookDetailsBox.setVisible(true);
        }
    }

    public void closeBookDetailsBox() {
        bookDetailsBox.setVisible(false);
    }

    @FXML
    private VBox itemsContainer; // The container for dynamic items (rows)

    private VBox createDocumentList(String categoryText) throws SQLException {
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
        List<Document> documentsByType = documentDAO.getDocumentsByType(Book.BookType.valueOf(categoryText));
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
        anchorPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 180px; -fx-pref-width: 160px;");
        if (document instanceof Book book) {
            // Create an ImageView for the book cover
            ImageView coverImageView = new ImageView();
            coverImageView.setFitHeight(180); // Set desired height
            //coverImageView.setFitWidth(120); // Set desired width
            coverImageView.setPreserveRatio(true);

            // Load the image from the URL
            if (book.getImageUrl() != null) {
                try {
                    coverImageView.setImage(new Image(book.getImageUrl(), true)); // Asynchronous loading
                } catch (Exception e) {
                    System.out.println("Failed to load image for ISBN: " + book.getIsbn());
                }
            }

            // Position the ImageView at the top center
            AnchorPane.setTopAnchor(coverImageView, 10.0);
            AnchorPane.setLeftAnchor(coverImageView, 20.0);

            // Borrow button at the bottom
            Button borrowButton = new Button("Borrow");
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
        placeholderPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 180px; -fx-pref-width: 160px;");
        return placeholderPane;
    }


}