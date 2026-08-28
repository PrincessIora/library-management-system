package com.manage.lms.library.api.dto;

import com.manage.lms.library.domain.model.User;
import com.manage.lms.library.domain.model.UserRole;

public class UserResponse {

    private int id;
    private String username;
    private UserRole role;

    public UserResponse() {
    }

    public UserResponse(int id, String username, UserRole role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }
}