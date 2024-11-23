package com.librarymanagement.UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class HomePageUserController {
    public TextField searchStringField;

    //Search document after pressed Enter
    public void handleSearchDocument() {
        String searchString = searchStringField.getText();

    }

    @FXML
    private VBox itemsContainer; // The container for dynamic items (rows)

    public void initialize() {

        // Create 4 rows of AnchorPanes
        itemsContainer.getChildren().add(createRowWithButtons("Borrowed Documents"));
        for (int i = 0; i < 3; i++) {
            itemsContainer.getChildren().add(createRowWithButtons("Testss"));
        }
    }

    private VBox createRowWithButtons(String categoryText) {
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

        // Create and add AnchorPanes for content
        for (int i = 0; i < 6; i++) {
            AnchorPane anchorPane = createAnchorPane();
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

    private AnchorPane createAnchorPane() {
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
