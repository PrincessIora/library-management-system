package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.BookStatus;
import com.manage.lms.library.domain.repository.BookRepository;
import java.util.Comparator;
import java.util.List;

public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(String title, String author, int year) {

        validateBookInformation(title, author, year);

        Book newBook = new Book(title.trim(), author.trim(), year);
        return bookRepository.save(newBook);
    }

    public void deleteBook(int id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "Book with ID " + id + " was not found."
                        )
                );
        if (book.getStatus() == BookStatus.BORROWED) {
            throw new ValidationException(
                    "Borrowed books must be returned before they can be removed."
            );
        }

        bookRepository.deleteById(id);
    }

    public Book updateBook(int id,
                    String title,
                    String author,
                    int year) {
        validateBookInformation(title, author, year);

        Book book = bookRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "Book with ID " + id + " was not found."
                        )
                );
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setYear(year);

        return bookRepository.update(book);
    }

    public List<Book> getBooks() {

        return bookRepository.findAll();
    }

    public List<Book> searchByTitle(String title) {

        if (title == null || title.isBlank()) {
            throw new ValidationException(
                    "Search title cannot be empty."
            );
        }

        return bookRepository.findByTitle(title.trim());
    }

    public List<Book> searchByAuthor(String author) {

        if (author == null || author.isBlank()) {
            throw new ValidationException(
                    "Search author cannot be empty."
            );
        }

        return bookRepository.findByAuthor(author.trim());
    }

    public List<Book> getBooksOrderedByYear() {

        return bookRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Book::getYear))
                .toList();
    }

    public Book getBook(int id) {
        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "Book with ID " + id + " was not found."
                        )
                );
    }

    private void validateBookInformation(String title, String author, int year) {
        if (title == null || title.isBlank()) {
            throw new ValidationException("Book title is required.");
        }

        if (author == null || author.isBlank()) {
            throw new ValidationException("Book author is required.");
        }

        if (year < 0) {
            throw new ValidationException("Book cannot have a negative year.");

        }
        if (year > java.time.Year.now().getValue()) {
            throw new ValidationException("Book cannot have a future year.");
        }
    }

}
