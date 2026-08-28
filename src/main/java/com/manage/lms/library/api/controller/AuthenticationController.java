package com.manage.lms.library.api.controller;

import com.manage.lms.library.api.dto.LoginRequest;
import com.manage.lms.library.api.dto.LoginResponse;
import com.manage.lms.library.api.dto.UserResponse;
import com.manage.lms.library.application.service.AuthenticationService;
import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    public AuthenticationController(
            AuthenticationService authenticationService,
            UserRepository userRepository
    ) {
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {

        String token = authenticationService.authenticate(
                request.getUsername(),
                request.getPassword()
        );

        User user = userRepository
                .findByUsername(request.getUsername().trim())
                .orElseThrow();

        return new LoginResponse(
                token,
                UserResponse.from(user)
        );
    }
}