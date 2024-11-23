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

    //test
    /**
    static {
        documents = new ArrayList<>();
        documents.add(new Book(1,"Welcome to My World", "Bolacatu", "001"));
        documents.add(new Book(2,"Exploring the Magical World", "Tanaka Hiroshi", "002"));
        documents.add(new Book(3,"The Amazing Adventure", "Sato Ken", "003"));
        documents.add(new Book(4,"Living Between Two Worlds", "Hashimoto Sachiko", "004"));
        documents.add(new Book(5,"Journey to the Stars", "Kobayashi Ichiro", "005"));
        documents.add(new Book(6,"The Miracle of Life", "Nakamura Yu", "006"));
        documents.add(new Book(7,"Life at the Peak of the World", "Suzuki Akiko", "007"));
        documents.add(new Book(8,"Overcoming the Storms", "Yamamoto Taro", "008"));
        documents.add(new Book(9,"The Power of Belief", "Takahashi Ai", "009"));
        documents.add(new Book(10,"My Dream", "Ogawa Mayu", "010"));
        documents.add(new Book(11,"The Secret to Happiness", "Tanabe Sho", "011"));
        documents.add(new Book(12,"Unconditional Love", "Ito Yuko", "012"));
        documents.add(new Book(13,"The Miraculous Steps", "Matsuda Ryo", "013"));
        documents.add(new Book(14,"The Path to Success", "Takatomi Sachiko", "014"));
        documents.add(new Book(15,"The Legend of a Star", "Sasaki Hiroshi", "015"));
        documents.add(new Book(16,"Advice from the Elders", "Murata Akira", "016"));
        documents.add(new Book(17,"Autumn in My Heart", "Kawada Rie", "017"));
        documents.add(new Book(18,"Island of the Gods", "Ito Shun", "018"));
        documents.add(new Book(19,"Where the Stars Meet", "Suzuki Kenta", "019"));
        documents.add(new Book(20,"Life After the Sorrows", "Kato Mika", "020"));
    }
     **/
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
