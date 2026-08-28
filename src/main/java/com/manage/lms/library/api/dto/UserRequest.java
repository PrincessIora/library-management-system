package com.manage.lms.library.api.dto;

import com.manage.lms.library.domain.model.UserRole;

public class UserRequest {

    private String username;
    private String password;
    private UserRole role;

    public UserRequest() {
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}