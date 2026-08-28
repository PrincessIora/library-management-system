package com.client.service;

import com.client.session.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final UserSession session = UserSession.getInstance();

    public ApiClient() {
        httpClient = HttpClient.newBuilder().build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    } /* * ========================================================= * GET * ========================================================= */

    public String get(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint)).header("Accept", "application/json").GET();
        addAuthenticationHeader(requestBuilder);
        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    } /* * ========================================================= * GET + JSON RESPONSE * ========================================================= */

    public <T> T get(String endpoint, Class<T> responseType) throws IOException, InterruptedException {
        String response = get(endpoint);
        return objectMapper.readValue(response, responseType);
    } /* * ========================================================= * POST * ========================================================= */

    public String post(String endpoint, String json) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint)).header("Content-Type", "application/json").header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(json));
        addAuthenticationHeader(requestBuilder);
        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    } /* * ========================================================= * POST OBJECT + JSON RESPONSE * ========================================================= */

    public <T> T post(String endpoint, Object requestBody, Class<T> responseType) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(requestBody);
        String response = post(endpoint, json);
        return objectMapper.readValue(response, responseType);
    } /* * ========================================================= * POST OBJECT + NO RESPONSE BODY * ========================================================= */

    public void post(String endpoint, Object requestBody) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(requestBody);
        post(endpoint, json);
    } /* * ========================================================= * PUT * ========================================================= */

    public String put(String endpoint, String json) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint)).header("Content-Type", "application/json").header("Accept", "application/json").PUT(HttpRequest.BodyPublishers.ofString(json));
        addAuthenticationHeader(requestBuilder);
        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    } /* * ========================================================= * PUT OBJECT + JSON RESPONSE * ========================================================= */

    public <T> T put(String endpoint, Object requestBody, Class<T> responseType) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(requestBody);
        String response = put(endpoint, json);
        return objectMapper.readValue(response, responseType);
    } /* * ========================================================= * DELETE * ========================================================= */

    public String delete(String endpoint) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(BASE_URL + endpoint)).header("Accept", "application/json").DELETE();
        addAuthenticationHeader(requestBuilder);
        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    } /* * ========================================================= * DELETE WITH JSON RESPONSE * ========================================================= */

    public <T> T delete(String endpoint, Class<T> responseType) throws IOException, InterruptedException {
        String response = delete(endpoint);
        return objectMapper.readValue(response, responseType);
    } /* * ========================================================= * LOGIN * * Login is intentionally NOT sent with the JWT because * the user does not have a JWT yet. * ========================================================= */

    public <T> T login(Object requestBody, Class<T> responseType) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL + "/auth/login")).header("Content-Type", "application/json").header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(json)).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String body = handleResponse(response);
        return objectMapper.readValue(body, responseType);
    } /* * ========================================================= * JWT AUTHENTICATION * ========================================================= */

    private void addAuthenticationHeader(HttpRequest.Builder requestBuilder) {
        String token = session.getToken();
        if (token != null && !token.isBlank()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }
    } /* * ========================================================= * RESPONSE HANDLING * ========================================================= */

    private String handleResponse(HttpResponse<String> response) throws IOException {
        int statusCode = response.statusCode();
        String body = response.body(); /* * 2xx = success */
        if (statusCode >= 200 && statusCode < 300) {
            return body;
        } /* * 401 = authentication failed */
        if (statusCode == 401) {
            session.logout();
            throw new IOException("Authentication failed. " + "Please log in again.");
        } /* * 403 = authenticated but unauthorized */
        if (statusCode == 403) {
            throw new IOException("Access denied. " + "You do not have permission " + "to perform this action.");
        } /* * Other errors */
        throw new IOException("API request failed. HTTP " + statusCode + ": " + body);
    }
}