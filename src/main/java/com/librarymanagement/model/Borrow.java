package com.librarymanagement.model;

import java.sql.Timestamp;

public class Borrow {
    private int borrowId;
    private int userId;
    private int documentId;
    private Timestamp borrowDate;
    private int durationDays = 1; // in days
    private boolean extendDurationRequest;

    public Borrow(int borrowId, int userId, int documentId, Timestamp borrowDate) {
        this.borrowId = borrowId;
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;

    }
    // use this to borrow
    public Borrow(int borrowId, int userId, int documentId, Timestamp borrowDate,int durationDays,boolean extendDurationRequest) {
        this.borrowId = borrowId;
        this.userId = userId;
        this.documentId = documentId;
        this.borrowDate = borrowDate;
        this.durationDays = durationDays;
        this.extendDurationRequest=extendDurationRequest;
    }

    // Getters and setters
    public int getBorrowId() {
        return borrowId;
    }

    public int getUserId() {
        return userId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public Timestamp getBorrowDate() {
        return borrowDate;
    }
    public int getDurationDays() {
        return durationDays;
    }

    public boolean isExtendDurationRequest() {
        return extendDurationRequest;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public void setBorrowDate(Timestamp borrowDate) {
        this.borrowDate = borrowDate;
    }
    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public void setExtendDurationRequest(boolean extendDurationRequest) {
        this.extendDurationRequest = extendDurationRequest;
    }
}
