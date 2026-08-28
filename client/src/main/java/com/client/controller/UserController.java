package com.client.controller;

import com.client.model.UserResponse;
import com.client.service.UserService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;

public class UserController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<UserRow> userTable;

    @FXML
    private TableColumn<UserRow, Integer> idColumn;

    @FXML
    private TableColumn<UserRow, String> usernameColumn;

    @FXML
    private TableColumn<UserRow, String> roleColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label messageLabel;

    @FXML
    private Button deleteButton;

    private final UserService userService =
            new UserService();

    /**
     * Keeps track of whether the form is
     * creating or editing a user.
     */
    private Integer editingUserId = null;

    @FXML
    private void initialize() {

        configureTable();

        configureRoleComboBox();

        loadUsers();
    }

    private void configureTable() {

        idColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleObjectProperty<>(
                                cellData.getValue().getId()
                        )
        );

        usernameColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getUsername()
                        )
        );

        roleColumn.setCellValueFactory(
                cellData ->
                        new javafx.beans.property.SimpleStringProperty(
                                cellData.getValue().getRole()
                        )
        );

        userTable.setPlaceholder(
                new Label("No users found.")
        );
    }

    private void configureRoleComboBox() {

        roleComboBox.getItems().setAll(
                "USER",
                "ADMIN"
        );

        roleComboBox.setValue("USER");
    }

    /**
     * Load users from the backend.
     */
    private void loadUsers() {

        try {

            UserResponse[] users =
                    userService.getUsers();

            userTable.getItems().clear();

            Arrays.stream(users)
                    .map(UserRow::new)
                    .forEach(
                            userTable.getItems()::add
                    );

            messageLabel.setText(
                    "Loaded " + users.length + " users."
            );

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to load users: "
                            + getErrorMessage(e)
            );
        }
    }

    /**
     * Search users locally.
     *
     * The backend currently does not provide
     * a user search endpoint, so filtering is
     * performed against the users already loaded.
     */
    @FXML
    private void onSearchButtonClick() {

        String search =
                searchField.getText()
                        .trim()
                        .toLowerCase();

        if (search.isBlank()) {

            loadUsers();

            return;
        }

        try {

            UserResponse[] users =
                    userService.getUsers();

            userTable.getItems().clear();

            Arrays.stream(users)
                    .filter(user ->
                            user.getUsername()
                                    .toLowerCase()
                                    .contains(search)
                    )
                    .map(UserRow::new)
                    .forEach(
                            userTable.getItems()::add
                    );

            messageLabel.setText(
                    "Found "
                            + userTable.getItems().size()
                            + " matching users."
            );

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Search failed: "
                            + getErrorMessage(e)
            );
        }
    }

    /**
     * Prepare the form for creating a new user.
     */
    @FXML
    private void onAddUserButtonClick() {

        editingUserId = null;

        clearForm();

        messageLabel.setText(
                "Ready to add a new user."
        );
    }

    /**
     * Load the selected user into the form.
     */
    @FXML
    private void onEditUserButtonClick() {

        UserRow selectedUser =
                userTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedUser == null) {

            messageLabel.setText(
                    "Please select a user to edit."
            );

            return;
        }

        editingUserId =
                selectedUser.getId();

        usernameField.setText(
                selectedUser.getUsername()
        );

        roleComboBox.setValue(
                selectedUser.getRole()
        );

        /*
         * The backend currently requires a password
         * during updates, so the administrator must
         * enter one when saving an edited user.
         */
        passwordField.clear();

        messageLabel.setText(
                "Editing user: "
                        + selectedUser.getUsername()
        );
    }

    /**
     * Delete the selected user.
     */
    @FXML
    private void onDeleteUserButtonClick() {

        UserRow selectedUser =
                userTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedUser == null) {

            messageLabel.setText(
                    "Please select a user to delete."
            );

            return;
        }

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle("Delete User");

        alert.setHeaderText(
                "Delete "
                        + selectedUser.getUsername()
                        + "?"
        );

        alert.setContentText(
                "This action will permanently remove "
                        + "the user from the system."
        );

        if (alert.showAndWait()
                .orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            try {

                userService.deleteUser(
                        selectedUser.getId()
                );

                userTable.getItems()
                        .remove(selectedUser);

                clearForm();

                messageLabel.setText(
                        "User deleted successfully."
                );

            } catch (Exception e) {

                e.printStackTrace();

                messageLabel.setText(
                        "Unable to delete user: "
                                + getErrorMessage(e)
                );
            }
        }
    }

    /**
     * Create or update a user.
     */
    @FXML
    private void onSaveButtonClick() {

        String username =
                usernameField.getText().trim();

        String password =
                passwordField.getText();

        String role =
                roleComboBox.getValue();

        if (username.isBlank()) {

            messageLabel.setText(
                    "Username is required."
            );

            return;
        }

        if (password.isBlank()) {

            messageLabel.setText(
                    "Password is required."
            );

            return;
        }

        if (role == null || role.isBlank()) {

            messageLabel.setText(
                    "User role is required."
            );

            return;
        }

        try {

            if (editingUserId == null) {

                /*
                 * CREATE
                 */
                UserResponse createdUser =
                        userService.createUser(
                                username,
                                password,
                                role
                        );

                userTable.getItems().add(
                        new UserRow(createdUser)
                );

                messageLabel.setText(
                        "User created successfully."
                );

            } else {

                /*
                 * UPDATE
                 */
                UserResponse updatedUser =
                        userService.updateUser(
                                editingUserId,
                                username,
                                password,
                                role
                        );

                UserRow selectedUser =
                        userTable.getSelectionModel()
                                .getSelectedItem();

                if (selectedUser != null) {

                    selectedUser.update(
                            updatedUser
                    );

                    userTable.refresh();
                }

                messageLabel.setText(
                        "User updated successfully."
                );
            }

            clearForm();

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to save user: "
                            + getErrorMessage(e)
            );
        }
    }

    /**
     * Clear the form.
     */
    @FXML
    private void onClearButtonClick() {

        editingUserId = null;

        clearForm();

        messageLabel.setText("");
    }

    private void clearForm() {

        usernameField.clear();

        passwordField.clear();

        roleComboBox.setValue("USER");

        userTable.getSelectionModel()
                .clearSelection();
    }

    /**
     * Extract a useful message from API exceptions.
     */
    private String getErrorMessage(Exception e) {

        if (e.getMessage() == null
                || e.getMessage().isBlank()) {

            return "Unknown error.";
        }

        return e.getMessage();
    }

    /**
     * Table row model.
     */
    public static class UserRow {

        private int id;
        private String username;
        private String role;

        public UserRow(UserResponse user) {

            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
        }

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getRole() {
            return role;
        }

        public void update(UserResponse user) {

            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
        }
    }
}