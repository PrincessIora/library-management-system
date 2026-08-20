package com.manage.lms.library.test;

import com.manage.lms.library.application.service.AuthenticationService;
import com.manage.lms.library.application.service.UserService;
import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;
import com.manage.lms.library.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTests {

    @Test
    void authenticateValidUser() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository);

        userService.createUser(
                "twilight",
                "password123",
                UserRole.USER
        );

        AuthenticationService service =
                new AuthenticationService(repository);

        User authenticated =
                service.authenticate(
                        "twilight",
                        "password123"
                );

        assertNotNull(authenticated);

        assertEquals(
                "twilight",
                authenticated.getUsername()
        );

        assertEquals(
                UserRole.USER,
                authenticated.getRole()
        );
    }


    @Test
    void authenticateAdminUser() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository);

        userService.createUser(
                "celestia",
                "admin123",
                UserRole.ADMIN
        );

        AuthenticationService service =
                new AuthenticationService(repository);

        User authenticated =
                service.authenticate(
                        "celestia",
                        "admin123"
                );

        assertNotNull(authenticated);

        assertEquals(
                UserRole.ADMIN,
                authenticated.getRole()
        );
    }


    @Test
    void authenticateRejectsUnknownUsername() {

        UserRepository repository =
                new InMemoryUserRepository();

        AuthenticationService service =
                new AuthenticationService(repository);

        assertThrows(
                ValidationException.class,
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
                new UserService(repository);

        userService.createUser(
                "twilight",
                "correctPassword",
                UserRole.USER
        );

        AuthenticationService service =
                new AuthenticationService(repository);

        assertThrows(
                ValidationException.class,
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
                        new InMemoryUserRepository()
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
                        new InMemoryUserRepository()
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
    void authenticateReturnsAdminRole() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository);

        userService.createUser(
                "admin",
                "password",
                UserRole.ADMIN
        );

        AuthenticationService service =
                new AuthenticationService(repository);

        User authenticated =
                service.authenticate(
                        "admin",
                        "password"
                );

        assertEquals(
                UserRole.ADMIN,
                authenticated.getRole()
        );
    }


    @Test
    void passwordIsStoredAsHash() {

        UserRepository repository =
                new InMemoryUserRepository();

        UserService userService =
                new UserService(repository);

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
}