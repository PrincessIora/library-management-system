package com.manage.lms.library.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Loan {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDateTime borrowedDate;
    private LocalDate dueDate;
    private LocalDateTime returnedDate;

    public Loan(int bookId, int memberId, LocalDateTime borrowedDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
    }

    public Loan(
            int id,
            int bookId,
            int memberId,
            LocalDateTime borrowedDate,
            LocalDate dueDate,
            LocalDateTime returnedDate
    ) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDateTime borrowedDate) {
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDateTime returnedDate) {
        this.returnedDate = returnedDate;
    }

    public boolean isOverdue() {
        return returnedDate == null &&
                dueDate.isBefore(LocalDate.now());
    }


}