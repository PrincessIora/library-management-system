package com.client.controller;

import com.client.service.LoanService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoanFormController {

    @FXML
    private Label formTitle;

    @FXML
    private Label formSubtitle;

    @FXML
    private Label bookIdLabel;

    @FXML
    private TextField bookIdField;

    @FXML
    private Label memberIdLabel;

    @FXML
    private TextField memberIdField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button actionButton;

    private final LoanService loanService =
            new LoanService();

    private boolean returning;

    private Runnable onLoanCompleted;

    @FXML
    private void initialize() {

        setBorrowMode();
    }

    public void setOnLoanCompleted(
            Runnable onLoanCompleted
    ) {

        this.onLoanCompleted =
                onLoanCompleted;
    }

    public void setBorrowMode() {

        returning = false;

        formTitle.setText(
                "Borrow Book"
        );

        formSubtitle.setText(
                "Enter the book and member information."
        );

        bookIdLabel.setText(
                "Book ID"
        );

        memberIdLabel.setText(
                "Member ID"
        );

        memberIdField.setVisible(true);
        memberIdField.setManaged(true);

        actionButton.setText(
                "Borrow Book"
        );

        errorLabel.setText("");
    }

    public void setReturnMode() {

        returning = true;

        formTitle.setText(
                "Return Book"
        );

        formSubtitle.setText(
                "Enter the ID of the book being returned."
        );

        bookIdLabel.setText(
                "Book ID"
        );

        memberIdField.setVisible(false);
        memberIdField.setManaged(false);

        actionButton.setText(
                "Return Book"
        );

        errorLabel.setText("");
    }

    public void setBookId(
            int bookId
    ) {

        bookIdField.setText(
                String.valueOf(bookId)
        );
    }

    @FXML
    private void onSaveButtonClick() {

        String bookIdText =
                bookIdField.getText()
                        .trim();

        if (bookIdText.isBlank()) {

            showError(
                    "Please enter a book ID."
            );

            return;
        }

        int bookId;

        try {

            bookId =
                    Integer.parseInt(bookIdText);

        } catch (NumberFormatException e) {

            showError(
                    "Book ID must be a number."
            );

            return;
        }

        if (bookId <= 0) {

            showError(
                    "Book ID must be greater than zero."
            );

            return;
        }

        errorLabel.setText("");

        try {

            if (returning) {

                loanService.returnBook(
                        bookId
                );

                showSuccess(
                        "Book Returned",
                        "The book was returned successfully."
                );

            } else {

                String memberIdText =
                        memberIdField.getText()
                                .trim();

                if (memberIdText.isBlank()) {

                    showError(
                            "Please enter a member ID."
                    );

                    return;
                }

                int memberId;

                try {

                    memberId =
                            Integer.parseInt(
                                    memberIdText
                            );

                } catch (NumberFormatException e) {

                    showError(
                            "Member ID must be a number."
                    );

                    return;
                }

                if (memberId <= 0) {

                    showError(
                            "Member ID must be greater than zero."
                    );

                    return;
                }

                loanService.borrowBook(
                        bookId,
                        memberId
                );

                showSuccess(
                        "Book Borrowed",
                        "The book was borrowed successfully."
                );
            }

            if (onLoanCompleted != null) {

                onLoanCompleted.run();
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

        bookIdField
                .getScene()
                .getWindow()
                .hide();
    }

    private void showError(
            String message
    ) {

        errorLabel.setText(
                message
        );
    }

    private void showSuccess(
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

            if (returning) {

                return "Unable to return the book.";
            }

            return "Unable to borrow the book.";
        }

        return e.getMessage();
    }
}