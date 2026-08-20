package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;
import com.manage.lms.library.domain.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(
            String username,
            String password,
            UserRole role
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

        if (role == null) {
            throw new ValidationException(
                    "User role is required."
            );
        }

        if (userRepository.findByUsername(username.trim()).isPresent()) {
            throw new ValidationException(
                    "Username already exists."
            );
        }

        String passwordHash =
                BCrypt.hashpw(
                        password,
                        BCrypt.gensalt()
                );

        User user = new User(
                username.trim(),
                passwordHash,
                role
        );

        return userRepository.save(user);
    }
}