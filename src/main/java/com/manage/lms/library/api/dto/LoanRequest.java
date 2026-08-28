package com.manage.lms.library.api.dto;

public class LoanRequest {

    private int bookId;
    private int memberId;

    public LoanRequest() {
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
}