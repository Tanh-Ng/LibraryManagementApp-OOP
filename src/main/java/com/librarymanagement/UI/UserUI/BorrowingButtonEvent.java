package com.librarymanagement.UI.UserUI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.Document;
import javafx.scene.control.Button;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class BorrowingButtonEvent {
    private final BorrowDAO borrowDAO;
    private final List<Borrow> borrowedDocuments;

    public BorrowingButtonEvent(BorrowDAO borrowDAO, List<Borrow> borrowedDocuments) {
        this.borrowDAO = borrowDAO;
        this.borrowedDocuments = borrowedDocuments;
    }

    void updateBorrowButtonState(Button button, Document document) {
        if (!document.isAvailable()) {
            button.setText("Unavailable");
            button.setDisable(true);
            button.setStyle("-fx-background-color: #ffcc66; -fx-text-fill: black;"); // Yellow-orange background
        } else if (isBorrowed(document)) {
            button.setText("Borrowed");
            button.setDisable(false);
            button.setStyle("-fx-background-color: #ff9999; -fx-text-fill: black;");
        } else {
            button.setText("Borrow");
            button.setDisable(false);
            button.setStyle("-fx-background-color: #99ff99; -fx-text-fill: black;");
        }
    }

    void buttonClicked(Button button, Document document) {
        if (isBorrowed(document)) {
            returnDocument(document);
        } else {
            borrowDocument(document);
        }
        updateBorrowButtonState(button, document);
    }

    public void borrowDocument(Document document) {
        // Borrow document logic
        try {
            borrowDAO.addBorrow(LibraryManagementApp.getCurrentUser().getUserId(), document.getDocumentId(), new Date(System.currentTimeMillis()));
            borrowedDocuments.add(new Borrow(borrowedDocuments.size(), LibraryManagementApp.getCurrentUser().getUserId()
                    , document.getDocumentId(), new Timestamp(System.currentTimeMillis())));
        } catch (Exception e) {
            System.out.println("Error borrowing document: " + e.getMessage());
        }
    }

    public void returnDocument(Document document) {
        // Return document logic
        try {
            Borrow borrow = borrowedDocuments.stream()
                    .filter(b -> b.getDocumentId() == document.getDocumentId())
                    .findFirst()
                    .orElse(null);
            if (borrow != null) {
                borrowDAO.deleteBorrow(borrow.getBorrowId());
                borrowedDocuments.remove(borrow);
            }
        } catch (Exception e) {
            System.out.println("Error returning document: " + e.getMessage());
        }
    }

    private boolean isBorrowed(Document document) {
        return borrowedDocuments.stream().anyMatch(b -> b.getDocumentId() == document.getDocumentId());
    }
}
