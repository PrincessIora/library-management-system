package com.manage.lms.library.infrastructure.repository;

import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;
import com.manage.lms.library.domain.repository.UserRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLUserRepository implements UserRepository {

    private final Connection connection;

    public MySQLUserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User save(User user) {

        String sql = """
                INSERT INTO users (username, password, role)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {

                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to save user.",
                    e
            );
        }
    }

    @Override
    public Optional<User> findById(int id) {

        String sql = """
                SELECT id, username, password, role
                FROM users
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(mapUser(result));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find user.",
                    e
            );
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {

        String sql = """
                SELECT id, username, password, role
                FROM users
                WHERE username = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {
                    return Optional.of(mapUser(result));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to find user.",
                    e
            );
        }
    }

    @Override
    public List<User> findAll() {

        String sql = """
                SELECT id, username, password, role
                FROM users
                """;

        List<User> users = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {
                users.add(mapUser(result));
            }

            return users;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to retrieve users.",
                    e
            );
        }
    }

    private User mapUser(ResultSet result)
            throws SQLException {

        User user = new User(
                result.getString("username"),
                result.getString("password"),
                UserRole.valueOf(
                        result.getString("role")
                )
        );

        user.setId(result.getInt("id"));

        return user;
    }
}