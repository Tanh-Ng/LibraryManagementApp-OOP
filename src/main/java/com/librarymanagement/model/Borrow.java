package com.librarymanagement.model;
import java.sql.Date;

public class Borrow {
    private int borrowId;
    private int userId;
    private int documentId;
    private Date borrowDate;

    public Borrow(int borrowId, int userId, int documentId, Date borrowDate) {
        this.borrowId = borrowId;
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;
    }

    // Getters and setters
    public int getBorrowId() { return borrowId; }
    public int getUserId() { return userId; }
    public int getDocumentId() { return documentId; }
    public Date getBorrowDate() { return borrowDate; }

    public void setBorrowId(int borrowId) { this.borrowId = borrowId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setDocumentId(int documentId) { this.documentId = documentId; }
    public void setBorrowDate(Date borrowDate) { this.borrowDate = borrowDate; }
}
