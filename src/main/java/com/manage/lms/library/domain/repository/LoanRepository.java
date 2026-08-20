package com.manage.lms.library.domain.repository;

import com.manage.lms.library.domain.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    Loan save(Loan loan);

    Loan update(Loan loan);

    Optional<Loan> findById(int id);

    Optional<Loan> findActiveLoanByBookId(int bookId);

    List<Loan> findAll();

    List<Loan> findActiveLoans();

    List<Loan> findOverdueLoans();

    List<Loan> findByMemberId(int memberId);

    List<Loan> findByBookId(int bookId);

}
