package com.librarymanagement.UI.UserUI;

import com.librarymanagement.UI.General.BookDetailsController;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Document;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

    private Label durationText;

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

        durationText = createBorrowDuration(duration[0]);

        // Create and configure days text field
        TextField daysTextField = createDaysTextField(borrowButton, errorLabel, duration);

        // Configure borrow button action
        configureBorrowButtonAction(borrowButton, daysTextField, errorLabel, duration, durationText);

        // Add elements to the pane
        bookDetailsPane.getChildren().addAll(borrowButton, daysTextField, errorLabel, durationText);

        // Load in the application
        Scene scene = new Scene(bookDetailsPane);
        LibraryManagementApp.showBookDetailsPage(scene);
    }

    private Label createBorrowDuration(int duration) {
        Label durationText = new Label();
        durationText.setText(duration + (duration == 1 ? " day" : " days") + " left");
        borrowingButtonEvent.updateDurationTextLabel(durationText, document);
        durationText.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        durationText.setLayoutX(300);
        durationText.setLayoutY(450);
        durationText.setAlignment(Pos.CENTER);
        return durationText;
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
            if (!newValue.matches("^(?!0$)\\d{1,2}$")
            && !newValue.isEmpty()) {
                errorLabel.setText("Please enter a number from 1 to 99");
                borrowButton.setDisable(true);
            } else if (!newValue.equals("")) {
                errorLabel.setText("");
                borrowButton.setDisable(false);
                duration[0] = Integer.parseInt(newValue); // Update duration in array
            } else {
                errorLabel.setText("");
                borrowButton.setDisable(false);
                duration[0] = borrowButton.getText().equals("Borrow") ? 1 : 0;
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

    private void configureBorrowButtonAction(Button borrowButton, TextField daysTextField,
                                             Label errorLabel, int[] duration, Label durationText) {
        borrowButton.setOnAction(e -> {
            borrowingButtonEvent.buttonClicked(borrowButton, document, duration[0]);

            //Set duration text
            borrowingButtonEvent.updateDurationTextLabel(durationText, document);

            configureDaysTextField(borrowButton, daysTextField, errorLabel, duration);
            daysTextField.clear();

            // Use callback to refresh borrowed documents in the parent class
            if (refreshCallback != null) {
                refreshCallback.refresh(String.valueOf(book.getBookType()));
            }
        });
    }
}
