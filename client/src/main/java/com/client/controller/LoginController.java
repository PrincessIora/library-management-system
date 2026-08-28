package com.client.controller;

import com.client.LibraryApplication;
import com.client.model.LoginResponse;
import com.client.model.UserResponse;
import com.client.service.AuthenticationService;
import com.client.session.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthenticationService authenticationService =
            new AuthenticationService();

    @FXML
    private void onLoginButtonClick() {

        String username =
                usernameField.getText().trim();

        String password =
                passwordField.getText();

        if (username.isBlank()
                || password.isBlank()) {

            errorLabel.setText(
                    "Please enter your username and password."
            );

            return;
        }

        try {

            LoginResponse response =
                    authenticationService.login(
                            username,
                            password
                    );

            UserResponse user =
                    response.getUser();

            UserSession.getInstance().login(
                    user.getUsername(),
                    user.getRole(),
                    response.getToken()
            );

            openDashboard();

        } catch (Exception e) {

            errorLabel.setText(
                    getLoginErrorMessage(e)
            );

            e.printStackTrace();
        }
    }

    private String getLoginErrorMessage(
            Exception e
    ) {

        String message = e.getMessage();

        if (message == null
                || message.isBlank()) {

            return "Unable to connect to the server.";
        }

        return message;
    }

    private void openDashboard() {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            LibraryApplication.class.getResource(
                                    "fxml/dashboard.fxml"
                            )
                    );

            Parent dashboard =
                    loader.load();

            Scene scene =
                    new Scene(
                            dashboard,
                            1100,
                            700
                    );

            scene.getStylesheets().add(
                    LibraryApplication.class
                            .getResource(
                                    "css/application.css"
                            )
                            .toExternalForm()
            );

            Stage stage =
                    (Stage) usernameField
                            .getScene()
                            .getWindow();

            stage.setTitle(
                    "Library Management System"
            );

            stage.setScene(scene);

            stage.setMinWidth(900);
            stage.setMinHeight(600);

        } catch (IOException | NullPointerException e) {

            errorLabel.setText(
                    "Unable to open the dashboard."
            );

            e.printStackTrace();
        }
    }
}