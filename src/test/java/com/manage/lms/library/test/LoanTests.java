package com.manage.lms.library.test;

import com.manage.lms.library.application.service.BookService;
import com.manage.lms.library.application.service.LoanService;
import com.manage.lms.library.application.service.MemberService;
import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.BookStatus;
import com.manage.lms.library.domain.model.Loan;
import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.repository.BookRepository;
import com.manage.lms.library.domain.repository.LoanRepository;
import com.manage.lms.library.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoanTests {
    @Test
    public void borrowBookCreatesLoan() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        Member member =
                new MemberService(members)
                        .createMember(
                                "Twilight",
                                "Sparkle"
                        );

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        Loan loan =
                service.borrowBook(
                        book.getId(),
                        member.getId()
                );

        assertNotNull(loan);

        assertEquals(
                book.getId(),
                loan.getBookId()
        );

        assertEquals(
                member.getId(),
                loan.getMemberId()
        );

        assertNotNull(
                loan.getBorrowedDate()
        );

        assertNotNull(
                loan.getDueDate()
        );

        assertNull(
                loan.getReturnedDate()
        );

        assertEquals(
                BookStatus.BORROWED,
                books.findById(book.getId())
                        .orElseThrow()
                        .getStatus()
        );
    }

    @Test
    void borrowBookRejectsAlreadyBorrowedBook() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        Member member =
                new MemberService(members)
                        .createMember(
                                "Twilight",
                                "Sparkle"
                        );

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        service.borrowBook(
                book.getId(),
                member.getId()
        );

        assertThrows(
                ValidationException.class,
                () -> service.borrowBook(
                        book.getId(),
                        member.getId()
                )
        );
    }

    @Test
    void borrowBookRejectsUnknownBook() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        Member member =
                new MemberService(members)
                        .createMember(
                                "Twilight",
                                "Sparkle"
                        );

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        assertThrows(
                ValidationException.class,
                () -> service.borrowBook(
                        999,
                        member.getId()
                )
        );
    }


    @Test
    void borrowBookRejectsUnknownMember() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        assertThrows(
                ValidationException.class,
                () -> service.borrowBook(
                        book.getId(),
                        999
                )
        );
    }


    @Test
    void returnBookMakesBookAvailable() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        Member member =
                new MemberService(members)
                        .createMember(
                                "Twilight",
                                "Sparkle"
                        );

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        Loan loan =
                service.borrowBook(
                        book.getId(),
                        member.getId()
                );

        Loan returnedLoan =
                service.returnBook(
                        book.getId()
                );

        assertNotNull(returnedLoan.getReturnedDate());

        assertEquals(
                BookStatus.AVAILABLE,
                books.findById(book.getId())
                        .orElseThrow()
                        .getStatus()
        );

        assertEquals(
                loan.getId(),
                returnedLoan.getId()
        );
    }

    @Test
    void returnBookRejectsAvailableBook() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        assertThrows(
                ValidationException.class,
                () -> service.returnBook(book.getId())
        );
    }






}
