package com.librarymanagement.UI.UserUI;


import com.librarymanagement.UI.General.ImageLoader;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;

import static com.librarymanagement.UI.General.ImageLoader.getQRCode;
import static com.librarymanagement.api.ApiClient.getQRCodeURL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static com.librarymanagement.UI.General.ImageLoader.getImage;

public class BookByTypeController {
    private final HomePageUserController userController = new HomePageUserController();

    private BorrowingButtonEvent borrowingButtonEvent;

    private TopBar topBar;

    private RefreshCallback refreshCallback;

    @FXML
    private Text theme;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private AnchorPane mainAnchorPane;

    public static List<Document> documents = new ArrayList<>();

    @FXML
    private VBox itemsContainer;

    public void initialize() {
        try {
            // TopBar modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserFXML/TopBar.fxml"));
            AnchorPane topBarAnchorPane = loader.load();
            mainAnchorPane.getChildren().addFirst(topBarAnchorPane);
            //Keep scroll pane
            mainScrollPane.toBack();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTheme(String typeOfBook, BorrowingButtonEvent borrowingButtonEvent) {
        theme.setText(typeOfBook);
        theme.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");
        documents = userController.getDocumentListByType(typeOfBook);
        if (documents.isEmpty()) {
            return;
        }
        for (Document document : documents) {
            itemsContainer.getChildren().add(createBookDetailsLine(document));
        }
        this.borrowingButtonEvent = borrowingButtonEvent;
    }

    public void handleClose() throws Exception {
        LibraryManagementApp.showHomeScreen();
    }

    public HBox createBookDetailsLine(Document document) {
        HBox hBox = new HBox();
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 220px; -fx-pref-width: 160px;");
        if (document instanceof Book book) {
            // Preload the book's image in a background thread

            ImageLoader.preloadImage(book);
            ImageLoader.preloadQRCode(getQRCodeURL(book.getInfoUrl()));

            // Create an ImageView for the book cover
            ImageView coverImageView = new ImageView();
            coverImageView.setFitHeight(150); // Set desired height
            //coverImageView.setFitWidth(120); // Set desired width
            coverImageView.setPreserveRatio(true);
            coverImageView.setImage(getImage(book.getImageUrl())); // Retrieve preloaded image

            // Position the ImageView at the top center
            AnchorPane.setTopAnchor(coverImageView, 10.0);
            AnchorPane.setLeftAnchor(coverImageView, 30.0);

            anchorPane.getChildren().addAll(coverImageView);

            VBox details = new VBox();
            details.getChildren().addAll(new Text("Title: " + book.getTitle()),
                    new Text("Author: " + book.getAuthor()),
                    new Text("Isbn:" + book.getIsbn()),
                    new Text("Publisher: " + book.getPublisher()),
                    new Text("Published Date: " + book.getPublishDate()));
            details.setStyle("-fx-font-size: 20px;");
            details.setMinWidth(600);
            ImageView qrCodeImageView = new ImageView();
            AnchorPane qrAnchorPane = new AnchorPane();
            try {
                String qrCodeUrl = getQRCodeURL(book.getInfoUrl());
                //System.out.println(qrCodeUrl);
                // Set the QR code image
                qrCodeImageView.setImage(getQRCode(qrCodeUrl));
                AnchorPane.setTopAnchor(qrCodeImageView, 30.0);
                qrAnchorPane.getChildren().add(qrCodeImageView);

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Failed to load QR code image for URL: " + book.getInfoUrl());
            }
            hBox.getChildren().addAll(anchorPane, details, qrAnchorPane);
            hBox.setSpacing(10.0);

            hBox.setOnMouseMoved(event -> {
                hBox.setStyle("-fx-background-color: #ffcccc;");
            });

            hBox.setOnMouseExited(event -> {
                hBox.setStyle("-fx-background-color: #FFFFFF;");
            });

            hBox.setOnMouseClicked(event -> {
                BookDetailsScreen bookDetailsScreen = new BookDetailsScreen(borrowingButtonEvent
                        , document, book, refreshCallback);
                try {
                    bookDetailsScreen.show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return hBox;
    }

}
