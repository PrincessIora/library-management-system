package com.client.controller;

import com.client.model.MemberResponse;
import com.client.service.MemberService;
import com.client.session.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MemberController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<MemberRow> memberTable;

    @FXML
    private TableColumn<MemberRow, Integer> idColumn;

    @FXML
    private TableColumn<MemberRow, String> firstNameColumn;

    @FXML
    private TableColumn<MemberRow, String> lastNameColumn;

    @FXML
    private TableColumn<MemberRow, String> fullNameColumn;

    private final MemberService memberService =
            new MemberService();

    private final UserSession session =
            UserSession.getInstance();

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        firstNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName")
        );

        lastNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastName")
        );

        fullNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("fullName")
        );

        loadMembers();
    }

    private void loadMembers() {

        try {

            MemberResponse[] members =
                    memberService.getMembers();

            displayMembers(members);

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Unable to load members.\n\n"
                            + getErrorMessage(e)
            );
        }
    }

    private void displayMembers(
            MemberResponse[] members
    ) {

        memberTable.setItems(
                FXCollections.observableArrayList(
                        java.util.Arrays.stream(members)
                                .map(MemberRow::from)
                                .toList()
                )
        );
    }

    @FXML
    private void onSearchButtonClick() {

        String search =
                searchField.getText().trim();

        if (search.isBlank()) {

            loadMembers();

            return;
        }

        try {

            MemberResponse[] results =
                    memberService.searchByName(search);

            displayMembers(results);

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Search Error",
                    getErrorMessage(e)
            );
        }
    }

    @FXML
    private void onAddMemberButtonClick() {

        if (!session.isAdmin()) {

            showMessage(
                    "Access Denied",
                    "Only administrators can add members."
            );

            return;
        }

        openMemberForm(
                false,
                null
        );
    }

    @FXML
    private void onEditMemberButtonClick() {

        if (!session.isAdmin()) {

            showMessage(
                    "Access Denied",
                    "Only administrators can edit members."
            );

            return;
        }

        MemberRow selectedMember =
                memberTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedMember == null) {

            showMessage(
                    "No Member Selected",
                    "Please select a member before editing."
            );

            return;
        }

        openMemberForm(
                true,
                selectedMember
        );
    }

    @FXML
    private void onDeleteMemberButtonClick() {

        if (!session.isAdmin()) {

            showMessage(
                    "Access Denied",
                    "Only administrators can delete members."
            );

            return;
        }

        MemberRow selectedMember =
                memberTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedMember == null) {

            showMessage(
                    "No Member Selected",
                    "Please select a member before deleting."
            );

            return;
        }

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle("Delete Member");

        alert.setHeaderText(
                "Delete \"" +
                        selectedMember.getFullName() +
                        "\"?"
        );

        alert.setContentText(
                "This action cannot be undone."
        );

        if (alert.showAndWait()
                .orElse(
                        javafx.scene.control.ButtonType.CANCEL
                )
                == javafx.scene.control.ButtonType.OK) {

            try {

                memberService.deleteMember(
                        selectedMember.getId()
                );

                loadMembers();

                showMessage(
                        "Member Deleted",
                        "The member was deleted successfully."
                );

            } catch (Exception e) {

                e.printStackTrace();

                showMessage(
                        "Delete Error",
                        getErrorMessage(e)
                );
            }
        }
    }

    @FXML
    private void onRefreshButtonClick() {

        searchField.clear();

        loadMembers();
    }

    private void openMemberForm(
            boolean editing,
            MemberRow member
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/client/fxml/member-form.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            MemberFormController controller =
                    loader.getController();

            if (editing && member != null) {

                controller.setMember(member);
            }

            Stage stage =
                    new Stage();

            stage.setTitle(
                    editing
                            ? "Edit Member"
                            : "Add Member"
            );

            stage.setScene(
                    new Scene(
                            root,
                            700,
                            550
                    )
            );

            stage.setMinWidth(600);
            stage.setMinHeight(500);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Unable to open the member form."
            );
        }
    }

    private void showMessage(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private String getErrorMessage(
            Exception e
    ) {

        if (e.getMessage() == null
                || e.getMessage().isBlank()) {

            return "An unexpected error occurred.";
        }

        return e.getMessage();
    }

    public static class MemberRow {

        private final int id;
        private final String firstName;
        private final String lastName;

        public MemberRow(
                int id,
                String firstName,
                String lastName
        ) {

            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public static MemberRow from(
                MemberResponse member
        ) {

            return new MemberRow(
                    member.getId(),
                    member.getFirstName(),
                    member.getLastName()
            );
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFullName() {
            return firstName + " " + lastName;
        }
    }
}