package com.librarymanagement.UI;

import com.librarymanagement.model.Book;
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
    @FXML
    public TextField searchStringField;

    @FXML
    public ListView<String> resultListView;

    public static List<Document> documents;

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
    private DocumentDAO documentDAO;

    public void initialize() throws SQLException {

        // Create 4 rows of AnchorPanes
        itemsContainer.getChildren().add(createRowWithButtons("Borrowed Documents"));
        for (int i = 0; i < 3; i++) {
            itemsContainer.getChildren().add(createRowWithButtons("Testss"));
        }
    }

    private VBox createRowWithButtons(String categoryText) throws SQLException {


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

        // Create the ScrollPane to hold the content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(200);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        HBox contentHBox = new HBox(10);
        contentHBox.setStyle("-fx-padding: 10;");

        List<Document> documentsByType = documentDAO.getDocumentsByType(Book.BookType.valueOf(categoryText));
        for (Document doc : documentsByType) {
            AnchorPane anchorPane = createAnchorPane(doc);
            contentHBox.getChildren().add(anchorPane);
        }

        // Place left and right buttons inside the HBox (along with the content)
        HBox scrollButtonsHBox = new HBox(10, leftButton, contentHBox, rightButton);
        scrollButtonsHBox.setAlignment(Pos.CENTER);

        scrollPane.setContent(scrollButtonsHBox);

        // Add everything to the main VBox
        vbox.getChildren().addAll(topHBox, scrollPane);

        return vbox;
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






}
