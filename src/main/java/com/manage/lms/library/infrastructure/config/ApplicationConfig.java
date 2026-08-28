package com.manage.lms.library.infrastructure.config;

import com.manage.lms.library.application.service.AuthenticationService;
import com.manage.lms.library.application.service.BookService;
import com.manage.lms.library.application.service.LoanService;
import com.manage.lms.library.application.service.MemberService;
import com.manage.lms.library.application.service.UserService;
import com.manage.lms.library.domain.repository.BookRepository;
import com.manage.lms.library.domain.repository.LoanRepository;
import com.manage.lms.library.domain.repository.MemberRepository;
import com.manage.lms.library.domain.repository.UserRepository;
import com.manage.lms.library.infrastructure.database.DatabaseConnection;
import com.manage.lms.library.infrastructure.repository.MySQLBookRepository;
import com.manage.lms.library.infrastructure.repository.MySQLLoanRepository;
import com.manage.lms.library.infrastructure.repository.MySQLMemberRepository;
import com.manage.lms.library.infrastructure.repository.MySQLUserRepository;

import com.manage.lms.library.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class ApplicationConfig {

    public ApplicationConfig(PasswordEncoder encoder, JwtService jwtservice) {
        this.encoder = encoder;
        this.jwtService= jwtservice;
    }

    @Bean
    public Connection databaseConnection(
            @Value("${database.username}") String username,
            @Value("${database.password}") String password
    ) throws SQLException {

        DatabaseConnection databaseConnection =
                new DatabaseConnection();

        return databaseConnection.connect(
                username,
                password
        );
    }

    @Bean
    public BookRepository bookRepository(
            Connection connection
    ) {
        return new MySQLBookRepository(connection);
    }

    @Bean
    public MemberRepository memberRepository(
            Connection connection
    ) {
        return new MySQLMemberRepository(connection);
    }

    @Bean
    public LoanRepository loanRepository(
            Connection connection
    ) {
        return new MySQLLoanRepository(connection);
    }

    @Bean
    public UserRepository userRepository(
            Connection connection
    ) {
        return new MySQLUserRepository(connection);
    }

    @Bean
    public BookService bookService(
            BookRepository repository
    ) {
        return new BookService(repository);
    }

    @Bean
    public MemberService memberService(
            MemberRepository memberRepository,
            LoanRepository loanRepository
    ) {
        return new MemberService(
                memberRepository,
                loanRepository
        );
    }

    @Bean
    public LoanService loanService(
            BookRepository bookRepository,
            MemberRepository memberRepository,
            LoanRepository loanRepository
    ) {
        return new LoanService(
                bookRepository,
                memberRepository,
                loanRepository
        );
    }

    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    @Bean
    public AuthenticationService authenticationService(
            UserRepository repository
    ) {
        return new AuthenticationService(repository, encoder, jwtService);
    }

    @Bean
    public UserService userService(
            UserRepository repository
    ) {
        return new UserService(repository, encoder);
    }
}