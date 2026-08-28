package com.client.controller;

import com.client.LibraryApplication;
import com.client.session.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label userLabel;

    @FXML
    private Button usersButton;

    private final UserSession session =
            UserSession.getInstance();

    @FXML
    private void initialize() {

        updateUserInterface();

        showDashboard();
    }

    private void updateUserInterface() {

        String username = session.getUsername();
        String role = session.getRole();

        if (username == null) {
            username = "Guest";
        }

        if (role == null) {
            role = "USER";
        }

        userLabel.setText(
                "👤 " + username + " (" + role + ")"
        );

        /*
         * Users is an ADMIN-only screen.
         *
         * setVisible(false) removes the visual element.
         * setManaged(false) removes the space it would occupy.
         */

        boolean isAdmin = session.isAdmin();

        usersButton.setVisible(isAdmin);
        usersButton.setManaged(isAdmin);
    }

    @FXML
    private void onDashboardButtonClick() {
        showDashboard();
    }

    @FXML
    private void onBooksButtonClick() {
        loadContent(
                "/com/client/fxml/books.fxml"
        );
    }

    @FXML
    private void onMembersButtonClick() {
        loadContent(
                "/com/client/fxml/members.fxml"
        );
    }

    @FXML
    private void onLoansButtonClick() {
        loadContent(
                "/com/client/fxml/loans.fxml"
        );
    }

    @FXML
    private void onUsersButtonClick() {

        /*
         * Authorization check.
         *
         * The button is hidden for normal users,
         * but we still check the role here.
         */

        if (!session.isAdmin()) {

            showAccessDenied();

            return;
        }

        loadContent(
                "/com/client/fxml/users.fxml"
        );
    }

    private void showDashboard() {

        loadContent(
                "/com/client/fxml/dashboard-home.fxml"
        );
    }

    private void loadContent(String fxmlPath) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    LibraryApplication.class.getResource(
                            fxmlPath
                    )
            );

            Parent content = loader.load();

            contentArea.getChildren().setAll(
                    content
            );

        } catch (IOException | NullPointerException e) {

            e.printStackTrace();

            Label error = new Label(
                    "Unable to load this screen."
            );

            error.getStyleClass()
                    .add("error-label");

            contentArea.getChildren().setAll(
                    error
            );
        }
    }

    private void showAccessDenied() {

        VBox accessDenied = new VBox();

        accessDenied.setSpacing(10);
        accessDenied.setAlignment(
                javafx.geometry.Pos.CENTER
        );

        Label icon = new Label("🔒");

        icon.setStyle(
                "-fx-font-size: 42px;"
        );

        Label title = new Label(
                "Access Denied"
        );

        title.getStyleClass()
                .add("welcome-title");

        Label message = new Label(
                "Only administrators can access User Management."
        );

        message.getStyleClass()
                .add("welcome-subtitle");

        accessDenied.getChildren().addAll(
                icon,
                title,
                message
        );

        contentArea.getChildren().setAll(
                accessDenied
        );
    }

    @FXML
    private void onLogoutButtonClick() {

        session.logout();

        try {

            FXMLLoader loader = new FXMLLoader(
                    LibraryApplication.class.getResource(
                            "fxml/login.fxml"
                    )
            );

            Parent login = loader.load();

            contentArea.getScene().setRoot(login);

            contentArea.getScene()
                    .getWindow();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}