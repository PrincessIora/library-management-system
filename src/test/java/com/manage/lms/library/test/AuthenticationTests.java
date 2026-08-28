package com.manage.lms.library.test;

import com.manage.lms.library.application.service.AuthenticationService;
import com.manage.lms.library.application.service.UserService;
import com.manage.lms.library.domain.exception.AuthenticationException;
import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;
import com.manage.lms.library.domain.repository.UserRepository;
import com.manage.lms.library.infrastructure.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;


public class AuthenticationTests {

    private String jwtSecret = "your-very-long-secret-key-that-is-at-least-32-characters-long";

    private long jwtExpiration = 3600000;

    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        jwtService = new JwtService(
                jwtSecret,
                jwtExpiration
        );
    }

    @Test
    void authenticateValidUser() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository, passwordEncoder);

        userService.createUser(
                "twilight",
                "password123",
                UserRole.USER
        );

        AuthenticationService service =
                new AuthenticationService(
                        repository,
                        passwordEncoder,
                        jwtService
                );

        String token =
                service.authenticate(
                        "twilight",
                        "password123"
                );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }


    @Test
    void authenticateAdminUser() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository, passwordEncoder);

        userService.createUser(
                "celestia",
                "admin123",
                UserRole.ADMIN
        );

        AuthenticationService service =
                new AuthenticationService(
                        repository,
                        passwordEncoder,
                        jwtService
                );

        String token =
                service.authenticate(
                        "celestia",
                        "admin123"
                );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }


    @Test
    void authenticateRejectsUnknownUsername() {

        UserRepository repository =
                new InMemoryUserRepository();

        AuthenticationService service =
                new AuthenticationService(
                        repository,
                        passwordEncoder,
                        jwtService
                );

        assertThrows(
                AuthenticationException.class,
                () -> service.authenticate(
                        "unknown",
                        "password123"
                )
        );
    }


    @Test
    void authenticateRejectsIncorrectPassword() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository, passwordEncoder);

        userService.createUser(
                "twilight",
                "correctPassword",
                UserRole.USER
        );

        AuthenticationService service =
                new AuthenticationService(
                        repository,
                        passwordEncoder,
                        jwtService
                );

        assertThrows(
                AuthenticationException.class,
                () -> service.authenticate(
                        "twilight",
                        "wrongPassword"
                )
        );
    }


    @Test
    void authenticateRejectsBlankUsername() {

        AuthenticationService service =
                new AuthenticationService(
                        new InMemoryUserRepository(),
                        passwordEncoder,
                        jwtService
                );

        assertThrows(
                ValidationException.class,
                () -> service.authenticate(
                        "",
                        "password123"
                )
        );
    }


    @Test
    void authenticateRejectsBlankPassword() {

        AuthenticationService service =
                new AuthenticationService(
                        new InMemoryUserRepository(),
                        passwordEncoder,
                        jwtService
                );

        assertThrows(
                ValidationException.class,
                () -> service.authenticate(
                        "twilight",
                        ""
                )
        );
    }


    @Test
    void authenticateReturnsJwtForAdmin() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository, passwordEncoder);

        userService.createUser(
                "admin",
                "password",
                UserRole.ADMIN
        );

        AuthenticationService service =
                new AuthenticationService(
                        repository,
                        passwordEncoder,
                        jwtService
                );

        String token =
                service.authenticate(
                        "admin",
                        "password"
                );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }


    @Test
    void passwordIsStoredAsHash() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository, passwordEncoder);

        User user =
                userService.createUser(
                        "twilight",
                        "password123",
                        UserRole.USER
                );

        assertNotEquals(
                "password123",
                user.getPassword()
        );

        assertTrue(
                user.getPassword().startsWith("$2")
        );
    }


    @Configuration
    @PropertySource("classpath:application.properties")
    static class TestConfig {
    }
}