package com.manage.lms.library.api.dto;

import com.manage.lms.library.domain.model.Loan;

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

    public LoanResponse(
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

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getBookId(),
                loan.getMemberId(),
                loan.getBorrowedDate(),
                loan.getDueDate(),
                loan.getReturnedDate()
        );
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