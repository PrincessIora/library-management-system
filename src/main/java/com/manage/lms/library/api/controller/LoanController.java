package com.manage.lms.library.api.controller;

import com.manage.lms.library.api.dto.LoanRequest;
import com.manage.lms.library.api.dto.LoanResponse;
import com.manage.lms.library.application.service.LoanService;
import com.manage.lms.library.domain.model.Loan;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/borrow")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN')")
    public LoanResponse borrowBook(
            @RequestBody LoanRequest request
    ) {
        return LoanResponse.from(
                loanService.borrowBook(
                        request.getBookId(),
                        request.getMemberId()
                )
        );
    }

    @PostMapping("/return/{bookId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public LoanResponse returnBook(
            @PathVariable int bookId
    ) {
        return LoanResponse.from(
                loanService.returnBook(bookId)
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<LoanResponse> getLoanHistory() {
        return loanService.getLoanHistory()
                .stream()
                .map(LoanResponse::from)
                .toList();
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<LoanResponse> getActiveLoans() {
        return loanService.getActiveLoans()
                .stream()
                .map(LoanResponse::from)
                .toList();
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<LoanResponse> getOverdueLoans() {
        return loanService.getOverdueLoans()
                .stream()
                .map(LoanResponse::from)
                .toList();
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<LoanResponse> getMemberLoanHistory(
            @PathVariable int memberId
    ) {
        return loanService.getMemberLoanHistory(memberId)
                .stream()
                .map(LoanResponse::from)
                .toList();
    }

    @GetMapping("/book/{bookId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<LoanResponse> getBookLoanHistory(
            @PathVariable int bookId
    ) {
        return loanService.getBookLoanHistory(bookId)
                .stream()
                .map(LoanResponse::from)
                .toList();
    }
}