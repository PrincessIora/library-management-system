package com.manage.lms.library.api.controller;

import com.manage.lms.library.api.dto.UserRequest;
import com.manage.lms.library.api.dto.UserResponse;
import com.manage.lms.library.application.service.UserService;
import com.manage.lms.library.domain.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(
            @RequestBody UserRequest request
    ) {

        User user = userService.createUser(
                request.getUsername(),
                request.getPassword(),
                request.getRole()
        );

        return UserResponse.from(user);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {

        return userService.getAllUsers()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUserById(
            @PathVariable int id
    ) {

        User user = userService.getUserById(id);

        return UserResponse.from(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUser(@PathVariable int id, @RequestBody UserRequest request) {
        return UserResponse.from(
                userService.updateUser(
                        id,
                        request.getUsername(),
                        request.getPassword(),
                        request.getRole()
                )
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }
}