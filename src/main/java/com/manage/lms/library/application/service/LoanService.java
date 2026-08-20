package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.BookStatus;
import com.manage.lms.library.domain.model.Loan;
import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.repository.BookRepository;
import com.manage.lms.library.domain.repository.LoanRepository;
import com.manage.lms.library.domain.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LoanService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public LoanService(
            BookRepository bookRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository
    ) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    public Loan borrowBook(int bookId, int memberId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ValidationException(
                                "Book with ID " + bookId + " was not found."
                        )
                );

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new ValidationException(
                                "Member with ID " + memberId + " was not found."
                        )
                );

        if (book.getStatus() == BookStatus.BORROWED) {
            throw new ValidationException(
                    "Book is already borrowed."
            );
        }

        LocalDateTime borrowedDate = LocalDateTime.now();

        LocalDate dueDate = LocalDate.now().plusWeeks(2);

        Loan loan = new Loan(
                book.getId(),
                member.getId(),
                borrowedDate,
                dueDate
        );

        book.setStatus(BookStatus.BORROWED);

        bookRepository.update(book);

        return loanRepository.save(loan);
    }

    public Loan returnBook(int bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ValidationException(
                                "Book with ID " + bookId + " was not found."
                        )
                );

        Loan loan = loanRepository.findActiveLoanByBookId(bookId)
                .orElseThrow(() ->
                        new ValidationException(
                                "Book is not currently borrowed."
                        )
                );

        loan.setReturnedDate(LocalDateTime.now());

        book.setStatus(BookStatus.AVAILABLE);

        bookRepository.update(book);

        return loanRepository.update(loan);
    }

    public List<Loan> getLoanHistory() {
        return loanRepository.findAll();
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findActiveLoans();
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.findOverdueLoans();
    }

    public List<Loan> getMemberLoanHistory(int memberId) {

        memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new ValidationException(
                                "Member with ID " + memberId + " was not found."
                        )
                );

        return loanRepository.findByMemberId(memberId);
    }

    public List<Loan> getBookLoanHistory(int bookId) {

        bookRepository.findById(bookId)
                .orElseThrow(() ->
                        new ValidationException(
                                "Book with ID " + bookId + " was not found."
                        )
                );

        return loanRepository.findByBookId(bookId);
    }

}