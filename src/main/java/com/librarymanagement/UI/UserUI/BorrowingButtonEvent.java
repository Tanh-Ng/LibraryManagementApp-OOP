package com.librarymanagement.UI.UserUI;

import com.librarymanagement.app.LibraryManagementApp;
import com.librarymanagement.dao.BorrowDAO;
import com.librarymanagement.model.Borrow;
import com.librarymanagement.model.Document;
import javafx.scene.control.Button;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class BorrowingButtonEvent {
    private final BorrowDAO borrowDAO;
    private List<Borrow> borrowedDocuments;

    public BorrowingButtonEvent(BorrowDAO borrowDAO, List<Borrow> borrowedDocuments) {
        this.borrowDAO = borrowDAO;
        this.borrowedDocuments = borrowedDocuments;
    }

    public void updateBorrowButtonState(Button button, Document document) {
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

    public void buttonClicked(Button button, Document document, int duration) {
        if (isBorrowed(document) && duration == 0) {
            returnDocument(document);
        } else if (isBorrowed(document) && duration != 0) {
            requestExtendDuration(document, duration);
        } else {
            borrowDocument(document, duration);
        }
        updateBorrowButtonState(button, document);
    }

    public void borrowDocument(Document document, int duration) {
        // Borrow document logic
        try {
            borrowDAO.addBorrow(LibraryManagementApp.getCurrentUser().getUserId()
                    , document.getDocumentId(), new Timestamp(System.currentTimeMillis()), duration);
            borrowedDocuments.add(new Borrow(borrowDAO.getBorrowIdByUserAndDocument(
                    LibraryManagementApp.getCurrentUser().getUserId(), document.getDocumentId())
                ,LibraryManagementApp.getCurrentUser().getUserId(), document.getDocumentId(), new Timestamp(System.currentTimeMillis()), duration, 0));
        } catch (Exception e) {
            System.out.println("Error borrowing document: " + e.getMessage());
        }
    }

    private void requestExtendDuration(Document document, int duration) {
        // Extend duration logic
        try {
            Borrow borrow = borrowedDocuments.stream()
                    .filter(b -> b.getDocumentId() == document.getDocumentId())
                    .findFirst()
                    .orElse(null);
            if (borrow != null) {
                borrowDAO.setExtendDurationRequest(borrow.getBorrowId(), duration);
                borrow.setExtendDurationRequest(duration);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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