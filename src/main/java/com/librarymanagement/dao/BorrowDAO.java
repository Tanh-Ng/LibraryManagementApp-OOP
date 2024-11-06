package com.librarymanagement.dao;

import com.librarymanagement.model.Borrow;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    public void addBorrow(int userId, int documentId, Date borrowDate) {
    }

    public List<Borrow> getBorrowedDocumentsByUser(int userId) {
        return new ArrayList<>();
    }

    public Borrow getBorrowById(int borrowId) {
        return new Borrow(0,0,0,new Date(1));
    }

    public void deleteBorrow(int borrowId) {
    }
}
