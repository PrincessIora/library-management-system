package com.manage.lms.library.test;

import com.manage.lms.library.application.service.BookService;
import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.BookStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookTests {

    @Test
    void createBookCreatesBook() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book book =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        assertEquals("The Hobbit", book.getTitle());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
        assertEquals(1937, book.getYear());
    }

    @Test
    void createBookGeneratesUniqueId() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book first =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        Book second =
                service.createBook(
                        "The Fellowship of the Ring",
                        "J.R.R. Tolkien",
                        1954
                );

        assertTrue(first.getId() > 0);
        assertTrue(second.getId() > 0);
        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void newBookIsAvailable() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book book =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }


    @Test
    void createBookRejectsMissingTitle() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createBook(
                        "",
                        "J.R.R. Tolkien",
                        1937
                )
        );
    }

    @Test
    void createBookRejectsNullTitle() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createBook(
                        null,
                        "J.R.R. Tolkien",
                        1937
                )
        );
    }

    @Test
    void createBookRejectsMissingAuthor() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createBook(
                        "The Hobbit",
                        "",
                        1937
                )
        );
    }

    @Test
    void createBookRejectsNullAuthor() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createBook(
                        "The Hobbit",
                        null,
                        1937
                )
        );
    }

    @Test
    void createBookRejectsNegativeYear() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        -1
                )
        );
    }

    @Test
    void createBookRejectsFutureYear() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        int futureYear =
                java.time.Year.now().getValue() + 1;

        assertThrows(
                ValidationException.class,
                () -> service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        futureYear
                )
        );
    }

    @Test
    void createBookTrimsTitleAndAuthor() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book book =
                service.createBook(
                        "  The Hobbit  ",
                        "  J.R.R. Tolkien  ",
                        1937
                );

        assertEquals("The Hobbit", book.getTitle());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
    }


    @Test
    void getBooksReturnsAllBooks() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        service.createBook(
                "1984",
                "George Orwell",
                1949
        );

        List<Book> books =
                service.getBooks();

        assertEquals(2, books.size());
    }

    @Test
    void getBooksReturnsEmptyListWhenNoBooksExist() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        List<Book> books =
                service.getBooks();

        assertTrue(books.isEmpty());
    }


    @Test
    void getBookReturnsBookById() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book created =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        Book found =
                service.getBook(created.getId());

        assertEquals(created.getId(), found.getId());
        assertEquals("The Hobbit", found.getTitle());
        assertEquals("J.R.R. Tolkien", found.getAuthor());
    }

    @Test
    void getBookRejectsUnknownId() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.getBook(999)
        );
    }

    @Test
    void updateBookChangesBookInformation() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book created =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        Book updated =
                service.updateBook(
                        created.getId(),
                        "The Hobbit: An Unexpected Journey",
                        "J.R.R. Tolkien",
                        1937
                );

        assertEquals(
                "The Hobbit: An Unexpected Journey",
                updated.getTitle()
        );

        assertEquals(
                "J.R.R. Tolkien",
                updated.getAuthor()
        );

        assertEquals(1937, updated.getYear());
    }

    @Test
    void updateBookKeepsExistingId() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book created =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        int originalId = created.getId();

        Book updated =
                service.updateBook(
                        originalId,
                        "The Hobbit",
                        "Tolkien",
                        1937
                );

        assertEquals(originalId, updated.getId());
    }

    @Test
    void updateBookKeepsBorrowingStatus() {

        InMemoryBookRepository repository =
                new InMemoryBookRepository();

        BookService service =
                new BookService(repository);

        Book created =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        repository.setStatus(
                created.getId(),
                BookStatus.BORROWED
        );

        Book updated =
                service.updateBook(
                        created.getId(),
                        "The Hobbit Updated",
                        "J.R.R. Tolkien",
                        1937
                );

        assertEquals(
                BookStatus.BORROWED,
                updated.getStatus()
        );
    }

    @Test
    void updateBookRejectsUnknownId() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.updateBook(
                        999,
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                )
        );
    }


    @Test
    void deleteAvailableBookRemovesBook() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        Book book =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        service.deleteBook(book.getId());

        assertTrue(service.getBooks().isEmpty());
    }

    @Test
    void deleteBorrowedBookIsRejected() {

        InMemoryBookRepository repository =
                new InMemoryBookRepository();

        BookService service =
                new BookService(repository);

        Book book =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        repository.setStatus(
                book.getId(),
                BookStatus.BORROWED
        );

        assertThrows(
                ValidationException.class,
                () -> service.deleteBook(book.getId())
        );
    }

    @Test
    void deleteBorrowedBookRemainsInLibrary() {

        InMemoryBookRepository repository =
                new InMemoryBookRepository();

        BookService service =
                new BookService(repository);

        Book book =
                service.createBook(
                        "The Hobbit",
                        "J.R.R. Tolkien",
                        1937
                );

        repository.setStatus(
                book.getId(),
                BookStatus.BORROWED
        );

        assertThrows(
                ValidationException.class,
                () -> service.deleteBook(book.getId())
        );

        assertEquals(
                1,
                service.getBooks().size()
        );
    }

    @Test
    void deleteUnknownBookIsRejected() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.deleteBook(999)
        );
    }


    @Test
    void searchByTitleFindsMatchingBooks() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        service.createBook(
                "The Fellowship of the Ring",
                "J.R.R. Tolkien",
                1954
        );

        service.createBook(
                "1984",
                "George Orwell",
                1949
        );

        List<Book> results =
                service.searchByTitle("Hobbit");

        assertEquals(1, results.size());
        assertEquals(
                "The Hobbit",
                results.get(0).getTitle()
        );
    }

    @Test
    void searchByTitleIsCaseInsensitive() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        List<Book> results =
                service.searchByTitle("hObBiT");

        assertEquals(1, results.size());
    }

    @Test
    void searchByTitleSupportsPartialMatches() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        service.createBook(
                "The Hobbit: An Unexpected Journey",
                "Peter Jackson",
                2012
        );

        List<Book> results =
                service.searchByTitle("Hobbit");

        assertEquals(2, results.size());
    }

    @Test
    void searchByTitleReturnsEmptyListWhenNoMatch() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        List<Book> results =
                service.searchByTitle("Harry Potter");

        assertTrue(results.isEmpty());
    }

    @Test
    void searchByTitleRejectsBlankSearch() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.searchByTitle("")
        );
    }

    @Test
    void searchByTitleRejectsNullSearch() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.searchByTitle(null)
        );
    }

    @Test
    void searchByAuthorFindsMatchingBooks() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        service.createBook(
                "The Fellowship of the Ring",
                "J.R.R. Tolkien",
                1954
        );

        service.createBook(
                "1984",
                "George Orwell",
                1949
        );

        List<Book> results =
                service.searchByAuthor("Tolkien");

        assertEquals(2, results.size());
    }

    @Test
    void searchByAuthorIsCaseInsensitive() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        List<Book> results =
                service.searchByAuthor("tOlKiEn");

        assertEquals(1, results.size());
    }

    @Test
    void searchByAuthorSupportsPartialMatches() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        List<Book> results =
                service.searchByAuthor("Tolk");

        assertEquals(1, results.size());
    }

    @Test
    void searchByAuthorReturnsEmptyListWhenNoMatch() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        List<Book> results =
                service.searchByAuthor("George Orwell");

        assertTrue(results.isEmpty());
    }

    @Test
    void searchByAuthorRejectsBlankSearch() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.searchByAuthor("")
        );
    }

    @Test
    void searchByAuthorRejectsNullSearch() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        assertThrows(
                ValidationException.class,
                () -> service.searchByAuthor(null)
        );
    }


    @Test
    void getBooksOrderedByYearSortsOldestFirst() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "1984",
                "George Orwell",
                1949
        );

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        service.createBook(
                "Harry Potter",
                "J.K. Rowling",
                1997
        );

        List<Book> books =
                service.getBooksOrderedByYear();

        assertEquals(1937, books.get(0).getYear());
        assertEquals(1949, books.get(1).getYear());
        assertEquals(1997, books.get(2).getYear());
    }

    @Test
    void getBooksOrderedByYearDoesNotModifyOriginalOrder() {

        BookService service =
                new BookService(new InMemoryBookRepository());

        service.createBook(
                "Harry Potter",
                "J.K. Rowling",
                1997
        );

        service.createBook(
                "The Hobbit",
                "J.R.R. Tolkien",
                1937
        );

        List<Book> original =
                service.getBooks();

        List<Book> sorted =
                service.getBooksOrderedByYear();

        assertEquals(
                "Harry Potter",
                original.get(0).getTitle()
        );

        assertEquals(
                "The Hobbit",
                sorted.get(0).getTitle()
        );
    }
}