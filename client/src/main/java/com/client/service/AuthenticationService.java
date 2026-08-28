package com.client.service;

import com.client.model.LoginRequest;
import com.client.model.LoginResponse;

public class AuthenticationService {

    private final ApiClient apiClient;

    public AuthenticationService() {
        apiClient = new ApiClient();
    }

    public LoginResponse login(String username, String password) throws Exception {
        LoginRequest request = new LoginRequest(username, password);
        return apiClient.login(request, LoginResponse.class);
    }
}