package com.librarymanagement.UI.General;

import com.librarymanagement.UI.UserUI.BorrowingButtonEvent;
import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        Image coverImage = new Image(book.getImageUrl());
        bookCoverImageView.setImage(coverImage);
        setQrCodeImage(book.getInfoUrl());
        // Set the book details
        titleLabel.setText("Title: " + book.getTitle());
        authorLabel.setText("Author: " + book.getAuthor());
        publisherLabel.setText("Publisher: " + book.getPublisher());
        publishDateLabel.setText("Published Date: " + book.getPublishDate());
    }

    // Fetch and set the barcode image
    private void setQrCodeImage(String documentUrl) {
        try {
            // QR Code API URL
            String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + documentUrl;

            // Load the QR code image
            Image qrCodeImage = new Image(qrCodeUrl);

            // Set the QR code image
            qrCodeImageView.setImage(qrCodeImage);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load QR code image for URL: " + documentUrl);
        }
    }



    // Close the popup window
    @FXML
    private void handleClose(){
        LibraryManagementApp.goBack();
    }
}
