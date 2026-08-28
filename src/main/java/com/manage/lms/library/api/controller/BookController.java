package com.manage.lms.library.api.controller;

import com.manage.lms.library.api.dto.BookRequest;
import com.manage.lms.library.api.dto.BookResponse;
import com.manage.lms.library.application.service.BookService;
import com.manage.lms.library.domain.model.Book;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<BookResponse> getBooks() {
        return bookService.getBooks()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public BookResponse getBook(@PathVariable int id) {
        return BookResponse.from(bookService.getBook(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponse createBook(@RequestBody BookRequest request) {
        return BookResponse.from(
                bookService.createBook(
                        request.getTitle(),
                        request.getAuthor(),
                        request.getYear()
                )
        );

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponse updateBook(@PathVariable int id, @RequestBody BookRequest request) {
        return BookResponse.from(
                bookService.updateBook(
                id,
                request.getTitle(),
                request.getAuthor(),
                request.getYear()
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/search/title")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<BookResponse> searchByTitle(@RequestParam String title) {
        return bookService.searchByTitle(title)
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @GetMapping("/search/author")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<BookResponse> searchByAuthor(@RequestParam String author) {
        return bookService.searchByAuthor(author)
                .stream()
                .map(BookResponse::from)
                .toList();
    }

    @GetMapping("/sort/year")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<BookResponse> getBooksOrderedByYear() {
        return bookService.getBooksOrderedByYear()
                .stream()
                .map(BookResponse::from)
                .toList();
    }

}