package com.librarymanagement.UI.UserUI;

import com.librarymanagement.UI.General.BookDetailsController;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class BookDetailsScreen {

    private final BorrowingButtonEvent borrowingButtonEvent;
    private final Document document;
    private final Book book;
    private final RefreshCallback refreshCallback;


    public BookDetailsScreen(BorrowingButtonEvent borrowingButtonEvent, Document document, Book book, RefreshCallback refreshCallback) {
        this.borrowingButtonEvent = borrowingButtonEvent;
        this.document = document;
        this.book = book;
        this.refreshCallback = refreshCallback;
    }

    public void show() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/BookDetails.fxml"));
        AnchorPane bookDetailsPane = loader.load();

        // Initialize controller and set book details
        BookDetailsController controller = loader.getController();
        controller.setBookDetails(book);

        // Create borrow button
        Button borrowButton = createBorrowButton();

        // Create error label
        Label errorLabel = createErrorLabel();

        // Initialize duration array
        int[] duration = {1}; // Default to 1 day

        // Create and configure days text field
        TextField daysTextField = createDaysTextField(borrowButton, errorLabel, duration);

        // Configure borrow button action
        configureBorrowButtonAction(borrowButton, daysTextField, duration);

        // Add elements to the pane
        bookDetailsPane.getChildren().addAll(borrowButton, daysTextField, errorLabel);

        // Load in the application
        Scene scene = new Scene(bookDetailsPane);
        LibraryManagementApp.showBookDetailsPage(scene);
    }

    private Button createBorrowButton() {
        Button borrowButton = new Button();
        borrowButton.setPrefWidth(150);
        borrowButton.setLayoutX(300);
        borrowButton.setLayoutY(400);
        borrowingButtonEvent.updateBorrowButtonState(borrowButton, document);
        return borrowButton;
    }

    private Label createErrorLabel() {
        Label errorLabel = new Label();
        errorLabel.setLayoutX(200);
        errorLabel.setLayoutY(430);
        errorLabel.setStyle("-fx-background-color: red;");
        return errorLabel;
    }

    private TextField createDaysTextField(Button borrowButton, Label errorLabel, int[] duration) {
        TextField daysTextField = new TextField();
        configureDaysTextField(borrowButton, daysTextField, errorLabel, duration);
        return daysTextField;
    }

    private void configureDaysTextField(Button borrowButton, TextField daysTextField, Label errorLabel, int[] duration) {
        if ("Borrow".equals(borrowButton.getText())) {
            setUpTextField(daysTextField, "Enter borrowing duration (default: 1 day)", true, 1, duration);
        } else if ("Borrowed".equals(borrowButton.getText())) {
            setUpTextField(daysTextField, "Enter extended duration if needed", true, 0, duration);
        } else {
            setUpTextField(daysTextField, "", false, 0, duration);
        }

        daysTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("^(?!0$)\\d{1,2}$")) {
                errorLabel.setText("Please enter a number from 1 to 99");
                borrowButton.setDisable(true);
            } else {
                errorLabel.setText("");
                borrowButton.setDisable(false);
                duration[0] = Integer.parseInt(newValue); // Update duration in array
            }
        });
    }

    private void setUpTextField(TextField textField, String promptText, boolean isEnabled, int defaultValue, int[] duration) {
        textField.setVisible(isEnabled);
        textField.setDisable(!isEnabled);
        textField.setPrefWidth(150);
        textField.setLayoutX(140);
        textField.setLayoutY(400);
        textField.setPromptText(promptText);
        duration[0] = defaultValue;
    }

    private void configureBorrowButtonAction(Button borrowButton, TextField daysTextField, int[] duration) {
        borrowButton.setOnAction(e -> {
            borrowingButtonEvent.buttonClicked(borrowButton, document, duration[0]);
            daysTextField.clear();

            // Use callback to refresh borrowed documents in the parent class
            if (refreshCallback != null) {
                refreshCallback.refresh(String.valueOf(book.getBookType()));
            }
        });
    }
}
