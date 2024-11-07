package com.librarymanagement.dao;

import com.librarymanagement.model.Borrow;
import com.librarymanagement.database.DatabaseConfig;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {
    // Thêm bản ghi mượn tài liệu vào cơ sở dữ liệu
    public void addBorrow(int userId, int documentId, Date borrowDate) throws SQLException {
        String sql = "INSERT INTO Borrows (user_id, document_id, borrow_date) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, documentId);
            pstmt.setDate(3, borrowDate);
            pstmt.executeUpdate();
        }
    }

    // Lấy danh sách tài liệu đã mượn của một người dùng cụ thể
    public List<Borrow> getBorrowedDocumentsByUser(int userId) throws SQLException {
        List<Borrow> borrows = new ArrayList<>();
        String sql = "SELECT * FROM Borrows WHERE user_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Borrow borrow = new Borrow(
                            rs.getInt("borrow_id"),
                            rs.getInt("user_id"),
                            rs.getInt("document_id"),
                            rs.getDate("borrow_date")
                    );
                    borrows.add(borrow);
                }
            }
        }
        return borrows;
    }

    // Lấy bản ghi mượn theo ID của bản ghi đó
    public Borrow getBorrowById(int borrowId) throws SQLException {
        String sql = "SELECT * FROM Borrows WHERE borrow_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, borrowId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Borrow(
                            rs.getInt("borrow_id"),
                            rs.getInt("user_id"),
                            rs.getInt("document_id"),
                            rs.getDate("borrow_date")
                    );
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy bản ghi với borrowId đó
    }

    // Xóa bản ghi mượn dựa vào ID
    public void deleteBorrow(int borrowId) throws SQLException {
        String sql = "DELETE FROM Borrows WHERE borrow_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, borrowId);
            pstmt.executeUpdate();
        }
    }
}

