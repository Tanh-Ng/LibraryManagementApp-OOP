package com.librarymanagement.UI.UserUI;

import com.librarymanagement.UI.General.BookDetailsController;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TopBar {
    @FXML
    private TextField searchStringField;

    @FXML
    private ListView<String> resultListView;

    @FXML
    private AnchorPane mainAnchorPane;

    private static final List<Document> documents = LibraryManagementApp.getDocuments();

    private RefreshCallback refreshCallback;

    public void switchRefresh(RefreshCallback refreshCallback) {
        // Switch context dynamically
        this.refreshCallback = refreshCallback;
    }

    public AnchorPane getMainAnchorPane() { return mainAnchorPane; }

    /// Search function
    //Search document after clicked
    public void handleSearchDocument() throws Exception {
        mainAnchorPane.setPickOnBounds(false);
        searchStringField.textProperty().addListener(
                (observable, oldValue, newValue) -> onSearch(newValue)
        );
    }

    private void onSearch(String newValue) {
        if (Objects.equals(newValue, "")) {
            resultListView.setVisible(false);
            resultListView.setDisable(true);
            mainAnchorPane.setPrefHeight(80);
        } else {
            resultListView.getItems().clear();
            resultListView.setVisible(true);
            resultListView.setDisable(false);
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

            // size of search list
            resultListView.setFixedCellSize(23.75);
            resultListView.setPrefHeight(23.75 * resultListView.getItems().size());

            // size of toolbar
            mainAnchorPane.setPrefHeight(400);
        }

        //Duration for Mouse moving
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
                    //Mouse entered 1 sec -> show details box
                    setOnMouseMoved(event -> {
                        setStyle("-fx-background-color: #ffcccc; -fx-text-fill: black;");
                        pauseTransition.setOnFinished(e -> {
                            if (!Objects.equals(item, "No document found.")) {
                                try {
                                    showBookDetails(item, event);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        });
                        pauseTransition.playFromStart();
                    });

                    //Mouse Exited -> delete box
                    setOnMouseExited(event -> {
                        setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;");
                        pauseTransition.stop();
                        mainAnchorPane.getChildren().set(mainAnchorPane.getChildren().size() - 1, new HBox());
                    });
                }
            }
        });
    }

    @FXML
    private void handlePickDocument() throws Exception {
        String documentTitle =  resultListView.getSelectionModel().getSelectedItem().split(" ------ ")[0];
        Book pickedBook = new Book("Null");
        for (Document find : documents) {
            if (find.getTitle().toLowerCase().equals(documentTitle.toLowerCase())) {
                pickedBook = (Book) find;
                break;
            }
        }

        if (!Objects.equals(documentTitle, "No document found.")
                && !Objects.equals(pickedBook.getIsbn(), "Null")) {

            //Show pages
            BookDetailsScreen bookDetailsScreen = new BookDetailsScreen(
                    new BorrowingButtonEvent(LibraryManagementApp.getBorrowDAO(), LibraryManagementApp.getBorrowList())
                    , pickedBook, pickedBook, refreshCallback);
            try {
                bookDetailsScreen.show();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void showBookDetails(String title, MouseEvent event) throws Exception{
        Book pickedBook =new Book("Null");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserFXML/BookDetailsBox.fxml"));
        HBox bookDetailsBox = loader.load();
        for (Document searchDocument : documents) {
            if (searchDocument.getTitle().equalsIgnoreCase(title.split(" ------ ")[0])) {
                pickedBook = (Book) searchDocument;
                break;
            }
        }
        // If the book details were fetched successfully, show them in the popup
        if (!pickedBook.getIsbn().equals("NULL")) {
            //setBookDetails();
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();

            //Set picked book
            bookDetailsBox.setStyle("-fx-background-color: #FFFFFF; -fx-text-fill: black;");
            BookDetailsController controller = loader.getController();
            controller.setBookDetails(pickedBook);

            //position for box
            if (mouseX > 633) {
                bookDetailsBox.setLayoutX(mouseX - 572);
            } else {
                bookDetailsBox.setLayoutX(mouseX + 5);
            }

            if (mouseY > 300){
                bookDetailsBox.setLayoutY(mouseY - 405);
            } else {
                bookDetailsBox.setLayoutY(mouseY + 5);
            }

            //Print
            mainAnchorPane.getChildren().set(mainAnchorPane.getChildren().size() - 1, bookDetailsBox);
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
}