package com.manage.lms.library.infrastructure.repository;


import com.manage.lms.library.domain.model.Book;
import com.manage.lms.library.domain.model.BookStatus;
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

    @Override
    public Member update(Member member) {

        String sql = """
                UPDATE members
                SET first_name = ?,
                    last_name = ?
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());
            statement.setInt(3, member.getId());
            statement.executeUpdate();

            return member;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to update member.",
                    e
            );
        }
    }


    @Override
    public void deleteById(int id) {

        String sql = """
                DELETE FROM members
                WHERE id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to delete member.",
                    e
            );
        }
    }


    @Override
    public List<Member> searchByName(String member) {

        String sql = """
                SELECT id, first_name, last_name
                FROM members
                WHERE LOWER(last_name) LIKE LOWER(?)
                 OR  LOWER(first_name) LIKE LOWER(?)
                """;

        return search(sql, member);
    }

    private List<Member> search(String sql, String searchText) {

        List<Member> members = new ArrayList<>();

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            String searchPattern = "%" + searchText + "%";

            statement.setString(1, searchPattern);
            statement.setString(2, searchPattern);

            try (ResultSet result = statement.executeQuery()) {

                while (result.next()) {
                    members.add(mapMember(result));
                }
            }

            return members;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Unable to search members.",
                    e
            );
        }
    }

    private Member mapMember(ResultSet result)
            throws SQLException {

        Member member = new Member(
                result.getString("first_name"),
                result.getString("last_name")
        );
        member.setId(result.getInt("id"));
        return  member;
    }

}