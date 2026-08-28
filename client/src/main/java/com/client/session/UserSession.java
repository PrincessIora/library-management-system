package com.client.session;

public class UserSession {

    private static UserSession instance;

    private String username;
    private String role;
    private String token;

    private UserSession() {
    }

    public static UserSession getInstance() {

        if (instance == null) {
            instance = new UserSession();
        }

        return instance;
    }

    public void login(
            String username,
            String role,
            String token
    ) {

        this.username = username;
        this.role = role;
        this.token = token;
    }

    public void logout() {

        username = null;
        role = null;
        token = null;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }

    public boolean isAdmin() {

        return "ADMIN".equals(role);
    }

    public boolean isLoggedIn() {

        return token != null
                && !token.isBlank();
    }
}