package com.client.controller;

import com.client.service.MemberService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MemberFormController {

    @FXML
    private Label formTitle;

    @FXML
    private Label formSubtitle;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label errorLabel;

    private final MemberService memberService =
            new MemberService();

    private boolean editing;

    private int memberId;

    @FXML
    private void initialize() {

        editing = false;

        formTitle.setText("Add Member");

        formSubtitle.setText(
                "Add a new member to your library."
        );
    }

    public void setMember(
            MemberController.MemberRow member
    ) {

        editing = true;

        memberId = member.getId();

        formTitle.setText("Edit Member");

        formSubtitle.setText(
                "Update the information for this member."
        );

        firstNameField.setText(
                member.getFirstName()
        );

        lastNameField.setText(
                member.getLastName()
        );
    }

    @FXML
    private void onSaveButtonClick() {

        String firstName =
                firstNameField.getText().trim();

        String lastName =
                lastNameField.getText().trim();

        if (firstName.isBlank()) {

            showError(
                    "Please enter the member's first name."
            );

            return;
        }

        if (lastName.isBlank()) {

            showError(
                    "Please enter the member's last name."
            );

            return;
        }

        errorLabel.setText("");

        try {

            if (editing) {

                memberService.updateMember(
                        memberId,
                        firstName,
                        lastName
                );

            } else {

                memberService.createMember(
                        firstName,
                        lastName
                );
            }

            closeWindow();

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    getErrorMessage(e)
            );
        }
    }

    @FXML
    private void onCancelButtonClick() {

        closeWindow();
    }

    private void closeWindow() {

        firstNameField
                .getScene()
                .getWindow()
                .hide();
    }

    private void showError(
            String message
    ) {

        errorLabel.setText(message);
    }

    private String getErrorMessage(
            Exception e
    ) {

        if (e.getMessage() == null
                || e.getMessage().isBlank()) {

            return "Unable to save the member.";
        }

        return e.getMessage();
    }
}