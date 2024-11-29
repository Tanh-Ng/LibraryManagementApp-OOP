package com.librarymanagement.UI.UserUI;

import com.librarymanagement.UI.General.ImageLoader;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.DocumentDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BookByTypeController {
    private final HomePageUserController userController = new HomePageUserController();

    private TopBar topBar;

    @FXML
    private Text theme;

    @FXML
    private ScrollPane mainScrollPane;
    private boolean isAdjustingScroll = false;
    private boolean mouseScroll = false;

    @FXML
    private AnchorPane mainAnchorPane;

    public List<Document> documents;

    @FXML
    private VBox itemsContainer;

    public void initialize() {
        try {
            // DAO initialization and data fetching
            topBar.setDocuments(documents);

            // Load images beforehand using multi-thread
//            documents.forEach(doc -> {
//                if (doc instanceof Book book) {
//                    ImageLoader.preloadImage(book.getImageUrl());
//                }
//            });

            //Keep scroll pane
            mainScrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
                if (!isAdjustingScroll && !mouseScroll) {
                    isAdjustingScroll = true;
                    mainScrollPane.setVvalue(oldValue.doubleValue());
                    isAdjustingScroll = false;
                }
            });

            mainScrollPane.setOnScroll(event -> mouseScroll = true);
            mainScrollPane.setOnMouseEntered(event ->  mouseScroll = true);
            mainAnchorPane.toBack();

            // Populate the rows
            itemsContainer.getChildren().add(userController.createDocumentList("Borrowed Documents", mainAnchorPane));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTheme(String typeOfBook) {
        theme.setText(typeOfBook);
        theme.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
    }

    public void setListDocument(List<Document> documents) {
        this.documents = documents;
    }

    public void handleClose() {
        LibraryManagementApp.goBack();
    }

    public HBox createBookDetailsLine(Document document) {
        HBox res = new HBox();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 220px; -fx-pref-width: 160px;");
        return res;
    }
}
