package com.client.service;

import com.client.model.UserRequest;
import com.client.model.UserResponse;

public class UserService {

    private final ApiClient apiClient;

    public UserService() {
        apiClient = new ApiClient();
    }

    /**
     * Get all users.
     *
     * Admin only.
     */
    public UserResponse[] getUsers()
            throws Exception {

        return apiClient.get(
                "/users",
                UserResponse[].class
        );
    }

    /**
     * Get a single user by ID.
     *
     * Admin only.
     */
    public UserResponse getUser(int id)
            throws Exception {

        return apiClient.get(
                "/users/" + id,
                UserResponse.class
        );
    }

    /**
     * Create a new user.
     *
     * Admin only.
     */
    public UserResponse createUser(
            String username,
            String password,
            String role
    ) throws Exception {

        UserRequest request =
                new UserRequest(
                        username,
                        password,
                        role
                );

        return apiClient.post(
                "/users",
                request,
                UserResponse.class
        );
    }

    /**
     * Update an existing user.
     *
     * Admin only.
     */
    public UserResponse updateUser(
            int id,
            String username,
            String password,
            String role
    ) throws Exception {

        UserRequest request =
                new UserRequest(
                        username,
                        password,
                        role
                );

        return apiClient.put(
                "/users/" + id,
                request,
                UserResponse.class
        );
    }

    /**
     * Delete a user.
     *
     * Admin only.
     */
    public void deleteUser(int id)
            throws Exception {

        apiClient.delete(
                "/users/" + id
        );
    }
}