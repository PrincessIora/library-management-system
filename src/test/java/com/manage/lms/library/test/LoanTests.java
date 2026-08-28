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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                new MemberService(members,loans)
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
                new MemberService(members,loans)
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
                new MemberService(members,loans)
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
                new MemberService(members,loans)
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

    @Test
    void getLoanHistoryReturnsAllLoans() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        BookService bookService =
                new BookService(books);

        MemberService memberService =
                new MemberService(members,loans);

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        Book book1 =
                bookService.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        Book book2 =
                bookService.createBook(
                        "The Princess Bride",
                        "William Goldman",
                        1973
                );

        Member member =
                memberService.createMember(
                        "Twilight",
                        "Sparkle"
                );

        service.borrowBook(
                book1.getId(),
                member.getId()
        );

        service.borrowBook(
                book2.getId(),
                member.getId()
        );

        List<Loan> history =
                service.getLoanHistory();

        assertEquals(
                2,
                history.size()
        );
    }

    @Test
    void getActiveLoansReturnsOnlyUnreturnedLoans() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        BookService bookService =
                new BookService(books);

        MemberService memberService =
                new MemberService(members,loans);

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        Book book1 =
                bookService.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        Book book2 =
                bookService.createBook(
                        "The Princess Bride",
                        "William Goldman",
                        1973
                );

        Member member =
                memberService.createMember(
                        "Twilight",
                        "Sparkle"
                );

        service.borrowBook(
                book1.getId(),
                member.getId()
        );

        service.borrowBook(
                book2.getId(),
                member.getId()
        );

        service.returnBook(
                book1.getId()
        );

        List<Loan> activeLoans =
                service.getActiveLoans();

        assertEquals(
                1,
                activeLoans.size()
        );

        assertEquals(
                book2.getId(),
                activeLoans.get(0).getBookId()
        );
    }

    @Test
    void overdueLoanIsDetected() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        InMemoryLoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        Member member =
                new MemberService(members,loans)
                        .createMember(
                                "Twilight",
                                "Sparkle"
                        );

        /*
         * Create a loan with a due date in the past.
         */
        Loan overdueLoan =
                new Loan(
                        book.getId(),
                        member.getId(),
                        LocalDateTime.now().minusWeeks(3),
                        LocalDate.now().minusWeeks(1)
                );

        loans.save(overdueLoan);

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        List<Loan> overdueLoans =
                service.getOverdueLoans();

        assertEquals(
                1,
                overdueLoans.size()
        );

        assertTrue(
                overdueLoans.get(0).isOverdue()
        );
    }


    @Test
    void returnedLoanIsNotOverdue() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        InMemoryLoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        Member member =
                new MemberService(members,loans)
                        .createMember(
                                "Twilight",
                                "Sparkle"
                        );

        Loan returnedLoan =
                new Loan(
                        book.getId(),
                        member.getId(),
                        LocalDateTime.now().minusWeeks(3),
                        LocalDate.now().minusWeeks(1)
                );

        returnedLoan.setReturnedDate(
                LocalDateTime.now()
        );

        loans.save(returnedLoan);

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        List<Loan> overdueLoans =
                service.getOverdueLoans();

        assertTrue(
                overdueLoans.isEmpty()
        );

        assertFalse(
                returnedLoan.isOverdue()
        );
    }


    @Test
    void currentLoanIsNotOverdue() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        InMemoryLoanRepository loans =
                new InMemoryLoanRepository();

        Book book =
                new BookService(books)
                        .createBook(
                                "The Hobbit",
                                "J.R.R. Tolkien",
                                1937
                        );

        Member member =
                new MemberService(members,loans)
                        .createMember(
                                "Twilight",
                                "Sparkle"
                        );

        Loan currentLoan =
                new Loan(
                        book.getId(),
                        member.getId(),
                        LocalDateTime.now(),
                        LocalDate.now().plusWeeks(2)
                );

        loans.save(currentLoan);

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        List<Loan> overdueLoans =
                service.getOverdueLoans();

        assertTrue(
                overdueLoans.isEmpty()
        );

        assertFalse(
                currentLoan.isOverdue()
        );
    }

    @Test
    void getMemberLoanHistoryReturnsOnlyMembersLoans() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        BookService bookService =
                new BookService(books);

        MemberService memberService =
                new MemberService(members,loans);

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        Book book1 =
                bookService.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        Book book2 =
                bookService.createBook(
                        "The Princess Bride",
                        "William Goldman",
                        1973
                );

        Member twilight =
                memberService.createMember(
                        "Twilight",
                        "Sparkle"
                );

        Member rarity =
                memberService.createMember(
                        "Rarity",
                        "Belle"
                );

        service.borrowBook(
                book1.getId(),
                twilight.getId()
        );

        service.returnBook(
                book1.getId()
        );

        service.borrowBook(
                book2.getId(),
                rarity.getId()
        );

        List<Loan> history =
                service.getMemberLoanHistory(
                        twilight.getId()
                );

        assertEquals(
                1,
                history.size()
        );

        assertEquals(
                twilight.getId(),
                history.get(0).getMemberId()
        );
    }


    @Test
    void getMemberLoanHistoryRejectsUnknownMember() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        assertThrows(
                ValidationException.class,
                () -> service.getMemberLoanHistory(999)
        );
    }

    @Test
    void getBookLoanHistoryReturnsBooksLoans() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        BookService bookService =
                new BookService(books);

        MemberService memberService =
                new MemberService(members,loans);

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        Book book =
                bookService.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        Member twilight =
                memberService.createMember(
                        "Twilight",
                        "Sparkle"
                );

        Member rarity =
                memberService.createMember(
                        "Rarity",
                        "Belle"
                );

        /*
         * First loan.
         */
        service.borrowBook(
                book.getId(),
                twilight.getId()
        );

        service.returnBook(
                book.getId()
        );

        /*
         * Second loan for the same book.
         */
        service.borrowBook(
                book.getId(),
                rarity.getId()
        );

        List<Loan> history =
                service.getBookLoanHistory(
                        book.getId()
                );

        assertEquals(
                2,
                history.size()
        );

        assertEquals(
                book.getId(),
                history.get(0).getBookId()
        );

        assertEquals(
                book.getId(),
                history.get(1).getBookId()
        );
    }


    @Test
    void getBookLoanHistoryRejectsUnknownBook() {

        BookRepository books =
                new InMemoryBookRepository();

        MemberRepository members =
                new InMemoryMemberRepository();

        LoanRepository loans =
                new InMemoryLoanRepository();

        LoanService service =
                new LoanService(
                        books,
                        members,
                        loans
                );

        assertThrows(
                ValidationException.class,
                () -> service.getBookLoanHistory(999)
        );
    }
}