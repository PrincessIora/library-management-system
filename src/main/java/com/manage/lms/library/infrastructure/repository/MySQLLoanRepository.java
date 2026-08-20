package com.manage.lms.library.infrastructure.repository;

import com.manage.lms.library.domain.model.Loan;
import com.manage.lms.library.domain.repository.LoanRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLLoanRepository implements LoanRepository {

    private final Connection connection;

    public MySQLLoanRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Loan save(Loan loan) {

        String sql = """
                INSERT INTO loans
                    (book_id, member_id, borrowed_date, due_date, returned_date)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, loan.getBookId());
            statement.setInt(2, loan.getMemberId());
            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(loan.getBorrowedDate())
            );
            statement.setDate(
                    4,
                    Date.valueOf(loan.getDueDate())
            );

            if (loan.getReturnedDate() == null) {
                statement.setNull(5, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(
                        5,
                        Timestamp.valueOf(loan.getReturnedDate())
                );
            }

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    loan.setId(keys.getInt(1));
                }
            }

            return loan;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to save loan.",
                    e
            );
        }
    }

    @Override
    public Optional<Loan> findById(int id) {

        String sql = """
                SELECT id,
                       book_id,
                       member_id,
                       borrowed_date,
                       due_date,
                       returned_date
                FROM loans
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(mapLoan(result));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find loan.",
                    e
            );
        }
    }

    @Override
    public Optional<Loan> findActiveLoanByBookId(int bookId) {

        String sql = """
                SELECT id,
                       book_id,
                       member_id,
                       borrowed_date,
                       due_date,
                       returned_date
                FROM loans
                WHERE book_id = ?
                  AND returned_date IS NULL
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookId);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(mapLoan(result));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find active loan.",
                    e
            );
        }
    }

    @Override
    public Loan update(Loan loan) {

        String sql = """
                UPDATE loans
                SET book_id = ?,
                    member_id = ?,
                    borrowed_date = ?,
                    due_date = ?,
                    returned_date = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, loan.getBookId());
            statement.setInt(2, loan.getMemberId());

            statement.setTimestamp(
                    3,
                    Timestamp.valueOf(loan.getBorrowedDate())
            );

            statement.setDate(
                    4,
                    Date.valueOf(loan.getDueDate())
            );

            if (loan.getReturnedDate() == null) {
                statement.setNull(5, Types.TIMESTAMP);
            } else {
                statement.setTimestamp(
                        5,
                        Timestamp.valueOf(loan.getReturnedDate())
                );
            }

            statement.setInt(6, loan.getId());

            statement.executeUpdate();

            return loan;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to update loan.",
                    e
            );
        }
    }

    @Override
    public List<Loan> findAll() {

        String sql = """
                SELECT id,
                       book_id,
                       member_id,
                       borrowed_date,
                       due_date,
                       returned_date
                FROM loans
                """;

        return findLoans(sql);
    }

    private Loan mapLoan(ResultSet result)
            throws SQLException {

        Timestamp borrowedTimestamp =
                result.getTimestamp("borrowed_date");

        Date dueDate =
                result.getDate("due_date");

        Timestamp returnedTimestamp =
                result.getTimestamp("returned_date");

        return new Loan(
                result.getInt("id"),
                result.getInt("book_id"),
                result.getInt("member_id"),
                borrowedTimestamp.toLocalDateTime(),
                dueDate.toLocalDate(),
                returnedTimestamp == null
                        ? null
                        : returnedTimestamp.toLocalDateTime()
        );
    }

    @Override
    public List<Loan> findActiveLoans() {

        String sql = """
                SELECT id,
                       book_id,
                       member_id,
                       borrowed_date,
                       due_date,
                       returned_date
                FROM loans
                WHERE returned_date IS NULL
                """;

        return findLoans(sql);
    }

    @Override
    public List<Loan> findOverdueLoans() {

        String sql = """
                SELECT id,
                       book_id,
                       member_id,
                       borrowed_date,
                       due_date,
                       returned_date
                FROM loans
                WHERE returned_date IS NULL
                  AND due_date < CURRENT_DATE
                """;

        return findLoans(sql);
    }

    private List<Loan> findLoans(String sql) {

        List<Loan> loans = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                loans.add(mapLoan(result));
            }

            return loans;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to retrieve loans.",
                    e
            );
        }
    }

    @Override
    public List<Loan> findByMemberId(int memberId) {

        String sql = """
                SELECT id,
                       book_id,
                       member_id,
                       borrowed_date,
                       due_date,
                       returned_date
                FROM loans
                WHERE member_id = ?
                """;

        return findLoansWithId(sql, memberId);
    }

    private List<Loan> findLoansWithId(String sql, int id) {

        List<Loan> loans = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    loans.add(mapLoan(result));
                }
            }

            return loans;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to retrieve loans.",
                    e
            );
        }
    }

    @Override
    public List<Loan> findByBookId(int bookId) {

        String sql = """
                SELECT id,
                       book_id,
                       member_id,
                       borrowed_date,
                       due_date,
                       returned_date
                FROM loans
                WHERE book_id = ?
                """;

        return findLoansWithId(sql, bookId);
    }

}
