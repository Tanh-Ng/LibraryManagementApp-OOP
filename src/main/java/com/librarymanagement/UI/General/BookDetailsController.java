package com.librarymanagement.UI.General;

import com.librarymanagement.app.LibraryManagementApp;

import static com.librarymanagement.UI.General.ImageLoader.getImage;
import static com.librarymanagement.api.ApiClient.getQRCodeURL;

import com.librarymanagement.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;


public class BookDetailsController {

    @FXML
    private ImageView bookCoverImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label publisherLabel;
    @FXML
    private Label publishDateLabel;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private Button closeButton;

    // Set the book details into the popup
    public void setBookDetails(Book book) {

        // Set the cover image
        bookCoverImageView.setImage(getImage(book.getImageUrl()));
        //Image coverImage = new Image(book.getImageUrl());
        //bookCoverImageView.setImage(coverImage);
        String QrURL = getQRCodeURL(book.getInfoUrl());
        if (QrURL != null) {
            Image QRImage = new Image(QrURL);
            qrCodeImageView.setImage(QRImage);
        }
        // Set the book details
        titleLabel.setText("Title: " + book.getTitle());
        authorLabel.setText("Author: " + book.getAuthor());
        publisherLabel.setText("Publisher: " + book.getPublisher());
        publishDateLabel.setText("Published Date: " + book.getPublishDate());
    }

    // Close the popup window
    @FXML
    private void handleClose() {
        LibraryManagementApp.goBack();
    }
}
