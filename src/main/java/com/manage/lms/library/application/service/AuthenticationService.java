package com.manage.lms.library.application.service;

import com.manage.lms.library.domain.exception.AuthenticationException;
import com.manage.lms.library.domain.exception.ValidationException;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.repository.UserRepository;
import com.manage.lms.library.infrastructure.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String authenticate(
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

        User user = userRepository
                .findByUsername(username.trim())
                .orElseThrow(() ->
                        new AuthenticationException(
                                "Invalid username or password."
                        )
                );

        boolean passwordCorrect =
                passwordEncoder.matches(
                        password,
                        user.getPassword()
                );

        if (!passwordCorrect) {
            throw new AuthenticationException(
                    "Invalid username or password."
            );
        }

        return jwtService.generateToken(user);
    }
}