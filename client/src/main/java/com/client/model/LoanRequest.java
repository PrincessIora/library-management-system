package com.client.model;

public class LoanRequest {

    private int bookId;
    private int memberId;

    public LoanRequest() {
    }

    public LoanRequest(
            int bookId,
            int memberId
    ) {
        this.bookId = bookId;
        this.memberId = memberId;
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