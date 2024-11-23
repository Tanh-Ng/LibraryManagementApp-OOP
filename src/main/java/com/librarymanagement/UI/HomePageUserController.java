package com.librarymanagement.UI;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import com.librarymanagement.dao.DocumentDAO;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                event -> handlePickDocumnet(resultListView.getSelectionModel().getSelectedItem())
        );
    }

    private void handlePickDocumnet(String documentTitleAuthor) {
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
}
