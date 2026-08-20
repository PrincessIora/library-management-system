package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(
            String username,
            String password
    ) {

        if (username == null || username.isBlank()) {
            throw new ValidationException(
                    "Username is required."
            );
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException(
                    "Password is required."
            );
        }

        User user =
                userRepository.findByUsername(username.trim())
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Invalid username or password."
                                )
                        );

        boolean passwordCorrect =
                BCrypt.checkpw(
                        password,
                        user.getPassword()
                );

        if (!passwordCorrect) {
            throw new ValidationException(
                    "Invalid username or password."
            );
        }

        return user;
    }
}