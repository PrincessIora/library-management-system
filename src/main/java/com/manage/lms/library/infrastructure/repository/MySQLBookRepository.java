package com.manage.lms.library.infrastructure.repository;

import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.BookStatus;
import com.manage.lms.library.domain.repository.BookRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLBookRepository implements BookRepository {

    private final Connection connection;

    public MySQLBookRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Book save(Book book) {

        String sql = """
                INSERT INTO Books (title, author, year, status)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setInt(3, book.getYear());
            statement.setString(4, book.getStatus().name());

            statement.executeUpdate();

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    book.setId(generatedKeys.getInt(1));
                }
            }

            return book;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to save book.",
                    e
            );
        }
    }

    @Override
    public Optional<Book> findById(int id) {

        String sql = """
                SELECT id, title, author, year, status
                FROM Books
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(mapBook(result));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find book.",
                    e
            );
        }
    }

    @Override
    public List<Book> findAll() {

        String sql = """
                SELECT id, title, author, year, status
                FROM Books
                """;

        List<Book> books = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                books.add(mapBook(result));
            }

            return books;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to retrieve books.",
                    e
            );
        }
    }

    @Override
    public void deleteById(int id) {

        String sql = """
                DELETE FROM Books
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to delete book.",
                    e
            );
        }
    }

    @Override
    public List<Book> findByTitle(String title) {

        String sql = """
                SELECT id, title, author, year, status
                FROM Books
                WHERE LOWER(title) LIKE LOWER(?)
                """;

        return search(sql, title);
    }

    @Override
    public List<Book> findByAuthor(String author) {

        String sql = """
                SELECT id, title, author, year, status
                FROM Books
                WHERE LOWER(author) LIKE LOWER(?)
                """;

        return search(sql, author);
    }

    private List<Book> search(String sql, String searchText) {

        List<Book> books = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, "%" + searchText + "%");

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    books.add(mapBook(result));
                }
            }

            return books;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to search books.",
                    e
            );
        }
    }

    @Override
    public Book update(Book book) {

        String sql = """
                UPDATE Books
                SET title = ?,
                    author = ?,
                    year = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setInt(3, book.getYear());
            statement.setInt(4, book.getId());

            statement.executeUpdate();

            return book;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to update book.",
                    e
            );
        }
    }

    private Book mapBook(ResultSet result)
            throws SQLException {

        return new Book(
                result.getInt("id"),
                result.getString("title"),
                result.getString("author"),
                result.getInt("year"),
                BookStatus.valueOf(
                        result.getString("status")
                )
        );
    }
}