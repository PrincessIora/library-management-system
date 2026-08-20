package com.manage.lms.library.infrastructure.repository;


import com.manage.lms.library.domain.model.Member;
import com.manage.lms.library.domain.repository.MemberRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLMemberRepository implements MemberRepository {
    private final Connection connection;

    public MySQLMemberRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Member save(Member member) {

        String sql = """
                INSERT INTO members (first_name, last_name)
                VALUES (?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    member.setId(generatedKeys.getInt(1));
                }
            }

            return member;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save member.", e);
        }
    }

    @Override
    public Optional<Member> findById(int id) {

        String sql = """
                SELECT id, first_name, last_name
                FROM members
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    Member member = new Member(
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name")
                    );

                    member.setId(resultSet.getInt("id"));

                    return Optional.of(member);
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find member.", e);
        }
    }

    @Override
    public List<Member> findAll() {

        String sql = """
                SELECT id, first_name, last_name
                FROM members
                ORDER BY id
                """;

        List<Member> members = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                Member member = new Member(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name")
                );

                member.setId(resultSet.getInt("id"));

                members.add(member);
            }

            return members;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve members.", e);
        }
    }
}