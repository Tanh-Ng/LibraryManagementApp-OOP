# Database Design

This document describes the database schema and design for the LibraryManagementApp-OOP project.

## Overview
The application uses a MySQL database to store information about users, books/documents, and borrowing records.

## Main Tables

### 1. `users`
- `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `username` (VARCHAR)
- `password` (VARCHAR)
- `role` (ENUM: 'user', 'admin')
- `email` (VARCHAR)
- `...` (other user details)

### 2. `documents` (books, magazines, etc.)
- `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `title` (VARCHAR)
- `author` (VARCHAR)
- `type` (VARCHAR)
- `status` (ENUM: 'available', 'borrowed')
- `...` (other document details)

### 3. `borrows`
- `id` (INT, PRIMARY KEY, AUTO_INCREMENT)
- `user_id` (INT, FOREIGN KEY to users)
- `document_id` (INT, FOREIGN KEY to documents)
- `borrow_date` (DATE)
- `return_date` (DATE)
- `status` (ENUM: 'borrowed', 'returned', 'overdue')

## Relationships
- Each user can borrow multiple documents.
- Each document can be borrowed by multiple users (over time).
- Borrow records link users and documents.

## Configuration
- Database connection is configured in `src/main/resources/db.properties`.
- Update credentials and database name as needed.

## Note
- The actual schema may include additional fields for tracking, auditing, or extended features.
