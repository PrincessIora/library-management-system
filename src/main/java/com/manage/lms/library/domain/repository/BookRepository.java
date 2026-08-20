package com.manage.lms.library.domain.repository;

import com.manage.lms.library.domain.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    Book update(Book book);

    Optional<Book> findById(int id);

    List<Book> findAll();

    void deleteById(int id);

    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);
}