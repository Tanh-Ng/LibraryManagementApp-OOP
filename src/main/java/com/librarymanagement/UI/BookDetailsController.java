package com.librarymanagement.UI;

import com.librarymanagement.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class BookDetailsController {

    @FXML private ImageView bookCoverImageView;
    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Label publisherLabel;
    @FXML private Label publishDateLabel;
    @FXML private Button closeButton;

    private Book book;

    // Set the book details into the popup
    public void setBookDetails(Book book) {
        this.book = book;

        // Set the cover image
        Image coverImage = new Image(book.getImageUrl());
        bookCoverImageView.setImage(coverImage);

        // Set the book details
        titleLabel.setText("Title: " + book.getTitle());
        authorLabel.setText("Author: " + book.getAuthor());
        publisherLabel.setText("Publisher: " + book.getPublisher());
        publishDateLabel.setText("Published Date: " + book.getPublishDate());
    }

    // Close the popup window
    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
