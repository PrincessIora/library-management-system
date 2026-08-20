package com.manage.lms.library.test;
import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.BookStatus;
import com.manage.lms.library.domain.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryBookRepository
        implements BookRepository {

    private final List<Book> books = new ArrayList<>();
    private int nextId = 1;

    @Override
    public Book save(Book book) {

        if (book.getId() == 0) {
            book.setId(nextId++);
            books.add(book);
        }

        return book;
    }

    @Override
    public Optional<Book> findById(int id) {

        return books.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books);
    }

    @Override
    public void deleteById(int id) {
        books.removeIf(
                book -> book.getId() == id
        );
    }

    @Override
    public List<Book> findByTitle(String title) {
        String searchText = title.toLowerCase();

        return books.stream()
                .filter(book ->
                        book.getTitle()
                                .toLowerCase()
                                .contains(searchText)
                )
                .toList();
    }

    @Override
    public List<Book> findByAuthor(String author) {
        String searchText = author.toLowerCase();

        return books.stream()
                .filter(book ->
                        book.getAuthor()
                                .toLowerCase()
                                .contains(searchText)
                )
                .toList();
    }


    @Override
    public Book update(Book book) {

        for (int i = 0; i < books.size(); i++) {

            if (books.get(i).getId() == book.getId()) {
                books.set(i, book);
                return book;
            }
        }

        return null;
    }

    public void setStatus(int id, BookStatus status) {

        books.stream()
                .filter(book -> book.getId() == id)
                .findFirst()
                .ifPresent(book -> book.setStatus(status));
    }
}
