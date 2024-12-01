package com.librarymanagement.UI.UserUI;

import com.librarymanagement.UI.General.BookDetailsController;
import com.librarymanagement.UI.General.ImageLoader;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.Document;
import com.librarymanagement.dao.DocumentDAO;

import javafx.fxml.FXML;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import java.util.List;
import javafx.util.Duration;

import static com.librarymanagement.UI.General.ImageLoader.getImage;

public class HomePageUserController implements RefreshCallback {
    private final BorrowDAO borrowDAO = LibraryManagementApp.getBorrowDAO();

    private BorrowingButtonEvent borrowingButtonEvent;

    @FXML
    private VBox itemsContainer; // The container for dynamic items (rows)

    @FXML
    private  AnchorPane mainAnchorPane;

    @FXML
    private ScrollPane mainScrollPane;


    public static List<Document> documents = LibraryManagementApp.getDocuments();

    public List<Borrow> borrowedDocuments = LibraryManagementApp.getBorrowList();

    public void initialize() {
        try {
            // TopBar modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserFXML/TopBar.fxml"));
            AnchorPane topBarAnchorPane = loader.load();
            TopBar topBar = loader.getController();
            topBar.switchRefresh(this);
            mainAnchorPane.getChildren().addFirst(topBarAnchorPane);
            // Data fetching
            borrowingButtonEvent = new BorrowingButtonEvent(borrowDAO, borrowedDocuments);
            mainScrollPane.toBack();
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
            if (mouseX + 567 > 1200) {
                bookDetailsBox.setLayoutX(mouseX - 567 - 5);
            } else {
                bookDetailsBox.setLayoutX(mouseX + 5);
            }

            if (mouseY + 400.0 > 700){
                bookDetailsBox.setLayoutY(mouseY - 400 - 5);
            } else {
                bookDetailsBox.setLayoutY(mouseY + 5);
            }

            //Print
            mainAnchorPane.getChildren().set(mainAnchorPane.getChildren().size() - 1, bookDetailsBox);
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

        //Book by type on

        seeMoreButton.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/UserFXML/BookByType.fxml"));
                Scene newScene = new Scene(loader.load());
                BookByTypeController controller = loader.getController();
                controller.setTheme(categoryText, borrowingButtonEvent);
                LibraryManagementApp.showBookByTypePage(newScene);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

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
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
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
                    try {
                        showBookDetails(book.getTitle() + " ------ ", event);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                });
                pauseTransition.playFromStart();
            });

            // When mouse exited -> delete book details screen
            anchorPane.setOnMouseExited(event -> {
                anchorPane.setStyle("-fx-background-color: lightgray; -fx-pref-height: 220px; -fx-pref-width: 160px;");
                mainAnchorPane.getChildren().set(mainAnchorPane.getChildren().size() - 1, new HBox());
                pauseTransition.stop();
            });

            // When mouse clicked -> show page details
            anchorPane.setOnMouseClicked(event -> {
                BookDetailsScreen bookDetailsScreen = new BookDetailsScreen(borrowingButtonEvent
                        , document, book, this::refresh);
                try {
                    bookDetailsScreen.show();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // Borrow button at the bottom
            Button borrowButton = new Button();
            borrowingButtonEvent.updateBorrowButtonState(borrowButton, document);

            borrowButton.setOnAction(event -> {
                borrowingButtonEvent.buttonClicked(borrowButton, document
                        , borrowButton.getText().equals("Borrow")
                            ? 1 : 0);
                borrowingButtonEvent.updateBorrowButtonState(borrowButton, document);
                refresh(String.valueOf(book.getBookType()));
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
        placeholderPane.setStyle("-fx-background-color: white; -fx-pref-height: 220px; -fx-pref-width: 160px;");
        return placeholderPane;
    }


    // Get document list by type
    public List<Document> getDocumentListByType(String categoryText) {
        // Validate input and ensure the categoryText matches an enum value in BookType
        if (categoryText == null || categoryText.isEmpty()) {
            return null; // Return an empty list for invalid input
        }

        if (categoryText.equals("Borrowed Documents")) {
            return getBorrowedDocumentsList();
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

    @Override
    // Refresh the VBox of borrowed documents list after borrowing or returning
    public void refresh(String category) {
        try {
            // Remove the first child (the Borrowed Documents section)
            if (!itemsContainer.getChildren().isEmpty()) {
                itemsContainer.getChildren().removeFirst();
            }

            // Add the updated Borrowed Documents section
            itemsContainer.getChildren().addFirst(createDocumentList("Borrowed Documents"));

            VBox categoryListVBox = createDocumentList(category); // This regenerates the document list for the category
            for (int i = 1; i < itemsContainer.getChildren().size(); i++) {  // Start from index 1 to skip "Borrowed Documents"
                VBox existingVBox = (VBox) itemsContainer.getChildren().get(i);
                Text categoryLabel = (Text) ((HBox) existingVBox.getChildren().getFirst()).getChildren().getFirst();

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
}
