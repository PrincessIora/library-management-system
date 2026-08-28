package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;
import com.manage.lms.library.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

        String passwordHash = passwordEncoder.encode(password);

        User user = new User(
                username.trim(),
                passwordHash,
                role
        );

        return userRepository.save(user);
    }

    public void deleteUser(int id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "User with ID " + id + " was not found."
                        )
                );

        userRepository.deleteById(id);

    }


    public User getUserById(int id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "User not found."
                        )
                );
    }

    public User updateUser(int id, String username, String password, UserRole role) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ValidationException(
                                "User with ID " + id + " was not found."
                        )
                );

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

        User existingUser = userRepository
                .findByUsername(username.trim())
                .orElse(null);

        if (existingUser != null && existingUser.getId() != id) {
            throw new ValidationException(
                    "Username already exists."
            );
        }

        user.setUsername(username.trim());

        String passwordHash = passwordEncoder.encode(password);

        user.setPassword(passwordHash);
        user.setRole(role);


        return userRepository.update(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}