package com.librarymanagement.UI;

import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.Document;
import  com.librarymanagement.dao.DocumentDAO;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomePageUserController {
    private final DocumentDAO documentDAO = new DocumentDAO();
    private final BorrowDAO borrowDAO = new BorrowDAO();

    @FXML
    public TextField searchStringField;

    @FXML
    public ListView<String> resultListView;

    public static List<Document> documents;

    public List<Borrow> borrowedDocuments;

    public void initialize() throws SQLException {
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
    public void handleSearchDocument() throws Exception{
        /*
        DocumentDAO documentDao = new DocumentDAO();
        try {
            documents = documentDao.getAllDocuments();
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
         */
        searchStringField.textProperty().addListener(
                (observable, oldValue, newValue) -> onSearch(newValue)
        );
    }

    private void onSearch(String newValue) {
        if (Objects.equals(newValue, "")) {
            resultListView.setVisible(false);
        }
        else {
            resultListView.setVisible(true);
            String searchString = searchStringField.getText();
            resultListView.getItems().clear();
            for (Document searchDocument : documents) {
                if(searchDocument.getTitle().toLowerCase().contains(searchString.toLowerCase())) {
                    resultListView.getItems().add(searchDocument.getTitle() + " by " + searchDocument.getAuthor());
                }
            }
            if (resultListView.getItems().isEmpty()) {
                resultListView.getItems().add("No document found.");
            }
            resultListView.setFixedCellSize(23.75);
            resultListView.setMinHeight(23.75 * resultListView.getItems().size() + 1.5);
        }

        resultListView.setCellFactory(ListView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setOnMouseEntered(event -> {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                    });
                    setOnMouseExited(event -> {
                        setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;");
                    });
                }
            }
        });

        resultListView.setOnMouseClicked(
                event -> handlePickDocument(resultListView.getSelectionModel().getSelectedItem())
        );
    }

    private void handlePickDocument(String documentTitleAuthor) {
        Document pickedDocument = new Document("Null", "Null");
        for (Document searchDocument : documents) {
            if (searchDocument.getTitle().equalsIgnoreCase(documentTitleAuthor.split(" by ")[0])) {
                pickedDocument = searchDocument;
                break;
            }
        }
        Stage stage = new Stage();
        VBox vbox = new VBox();
        Label titleLabel = new Label("Title: " + pickedDocument.getTitle());
        Label authorLabel = new Label("Author: " + pickedDocument.getAuthor());

        vbox.getChildren().addAll(titleLabel, authorLabel);
        Scene scene = new Scene(vbox, 500, 500);

        stage.setTitle("Document Details");
        stage.setScene(scene);
        stage.show();
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

        // Borrow button at the bottom
        Button borrowButton = new Button("Borrow");
        borrowButton.setPrefWidth(100);
        AnchorPane.setBottomAnchor(borrowButton, 10.0);
        AnchorPane.setLeftAnchor(borrowButton, 30.0);

        anchorPane.getChildren().add(borrowButton);
        return anchorPane;
    }

    // Create a placeholder AnchorPane for empty spaces
    private AnchorPane createPlaceholderPane() {
        AnchorPane placeholderPane = new AnchorPane();
        placeholderPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 180px; -fx-pref-width: 160px;");
        return placeholderPane;
    }


}
