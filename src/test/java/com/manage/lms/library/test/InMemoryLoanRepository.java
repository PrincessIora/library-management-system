package com.manage.lms.library.test;

import com.manage.lms.library.domain.model.Loan;
import com.manage.lms.library.domain.repository.LoanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLoanRepository implements LoanRepository {

    private final List<Loan> loans = new ArrayList<>();

    private int nextId = 1;

    @Override
    public Loan save(Loan loan) {

        if (loan.getId() == 0) {
            loan.setId(nextId++);
            loans.add(loan);
        }

        return loan;
    }

    @Override
    public Loan update(Loan loan) {

        for (int i = 0; i < loans.size(); i++) {

            if (loans.get(i).getId() == loan.getId()) {
                loans.set(i, loan);
                return loan;
            }
        }

        return null;
    }

    @Override
    public Optional<Loan> findById(int id) {

        return loans.stream()
                .filter(loan -> loan.getId() == id)
                .findFirst();
    }

    @Override
    public Optional<Loan> findActiveLoanByBookId(int bookId) {

        return loans.stream()
                .filter(loan -> loan.getBookId() == bookId)
                .filter(loan -> loan.getReturnedDate() == null)
                .findFirst();
    }

    @Override
    public List<Loan> findAll() {

        return new ArrayList<>(loans);
    }

    @Override
    public List<Loan> findActiveLoans() {

        return loans.stream()
                .filter(loan -> loan.getReturnedDate() == null)
                .toList();
    }

    @Override
    public List<Loan> findOverdueLoans() {

        return loans.stream()
                .filter(loan -> loan.getReturnedDate() == null)
                .filter(Loan::isOverdue)
                .toList();
    }

    @Override
    public List<Loan> findByMemberId(int memberId) {

        return loans.stream()
                .filter(loan -> loan.getMemberId() == memberId)
                .toList();
    }

    @Override
    public List<Loan> findByBookId(int bookId) {

        return loans.stream()
                .filter(loan -> loan.getBookId() == bookId)
                .toList();
    }


}