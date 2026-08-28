package com.client.service;

import com.client.model.LoanRequest;
import com.client.model.LoanResponse;

public class LoanService {

    private final ApiClient apiClient;

    public LoanService() {
        apiClient = new ApiClient();
    }

    public LoanResponse[] getLoans()
            throws Exception {

        return apiClient.get(
                "/loans",
                LoanResponse[].class
        );
    }

    public LoanResponse[] getActiveLoans()
            throws Exception {

        return apiClient.get(
                "/loans/active",
                LoanResponse[].class
        );
    }

    public LoanResponse[] getOverdueLoans()
            throws Exception {

        return apiClient.get(
                "/loans/overdue",
                LoanResponse[].class
        );
    }

    public LoanResponse[] getMemberLoanHistory(
            int memberId
    ) throws Exception {

        return apiClient.get(
                "/loans/member/" + memberId,
                LoanResponse[].class
        );
    }

    public LoanResponse[] getBookLoanHistory(
            int bookId
    ) throws Exception {

        return apiClient.get(
                "/loans/book/" + bookId,
                LoanResponse[].class
        );
    }

    public LoanResponse borrowBook(
            int bookId,
            int memberId
    ) throws Exception {

        LoanRequest request =
                new LoanRequest(
                        bookId,
                        memberId
                );

        return apiClient.post(
                "/loans/borrow",
                request,
                LoanResponse.class
        );
    }

    public LoanResponse returnBook(
            int bookId
    ) throws Exception {

        return apiClient.post(
                "/loans/return/" + bookId,
                null,
                LoanResponse.class
        );
    }
}