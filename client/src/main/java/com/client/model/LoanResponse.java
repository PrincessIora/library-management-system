package com.client.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoanResponse {

    private int id;
    private int bookId;
    private int memberId;
    private LocalDateTime borrowedDate;
    private LocalDate dueDate;
    private LocalDateTime returnedDate;

    public LoanResponse() {
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }
}