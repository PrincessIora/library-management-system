package com.client.controller;

import com.client.model.LoanResponse;
import com.client.service.LoanService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Arrays;

public class LoanController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<LoanRow> loanTable;

    @FXML
    private TableColumn<LoanRow, Integer> idColumn;

    @FXML
    private TableColumn<LoanRow, String> bookColumn;

    @FXML
    private TableColumn<LoanRow, String> memberColumn;

    @FXML
    private TableColumn<LoanRow, String> borrowedColumn;

    @FXML
    private TableColumn<LoanRow, String> dueDateColumn;

    @FXML
    private TableColumn<LoanRow, String> returnedColumn;

    @FXML
    private TableColumn<LoanRow, String> statusColumn;

    @FXML
    private Label messageLabel;

    private final LoanService loanService =
            new LoanService();

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        bookColumn.setCellValueFactory(
                new PropertyValueFactory<>("book")
        );

        memberColumn.setCellValueFactory(
                new PropertyValueFactory<>("member")
        );

        borrowedColumn.setCellValueFactory(
                new PropertyValueFactory<>("borrowed")
        );

        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("dueDate")
        );

        returnedColumn.setCellValueFactory(
                new PropertyValueFactory<>("returned")
        );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        loadLoans();
    }

    private void loadLoans() {

        try {

            LoanResponse[] loans =
                    loanService.getLoans();

            displayLoans(loans);

            messageLabel.setText("");

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to load loans.\n\n"
                            + getErrorMessage(e)
            );
        }
    }

    private void displayLoans(
            LoanResponse[] loans
    ) {

        loanTable.setItems(
                FXCollections.observableArrayList(
                        Arrays.stream(loans)
                                .map(LoanRow::from)
                                .toList()
                )
        );
    }

    @FXML
    private void onSearchButtonClick() {

        String search =
                searchField.getText()
                        .trim();

        if (search.isBlank()) {

            loadLoans();

            return;
        }

        try {

            LoanResponse[] loans =
                    loanService.getLoans();

            String lowerSearch =
                    search.toLowerCase();

            LoanResponse[] results =
                    Arrays.stream(loans)
                            .filter(loan ->
                                    String.valueOf(
                                            loan.getId()
                                    ).contains(search)

                                            || String.valueOf(
                                            loan.getBookId()
                                    ).contains(search)

                                            || String.valueOf(
                                            loan.getMemberId()
                                    ).contains(search)
                            )
                            .toArray(
                                    LoanResponse[]::new
                            );

            displayLoans(results);

            if (results.length == 0) {

                messageLabel.setText(
                        "No loans found matching \""
                                + search
                                + "\"."
                );

            } else {

                messageLabel.setText(
                        "Found "
                                + results.length
                                + " loan(s)."
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to search loans.\n\n"
                            + getErrorMessage(e)
            );
        }
    }

    @FXML
    private void onBorrowButtonClick() {

        openLoanForm(false);
    }

    @FXML
    private void onReturnButtonClick() {

        LoanRow selectedLoan =
                loanTable.getSelectionModel()
                        .getSelectedItem();

        if (selectedLoan == null) {

            showMessage(
                    "No Loan Selected",
                    "Please select an active loan before returning a book."
            );

            return;
        }

        if ("RETURNED".equals(
                selectedLoan.getStatus()
        )) {

            showMessage(
                    "Book Already Returned",
                    "This loan has already been returned."
            );

            return;
        }

        openLoanForm(
                true,
                selectedLoan.getBookId()
        );
    }

    @FXML
    private void onRefreshButtonClick() {

        searchField.clear();

        loadLoans();
    }

    private void openLoanForm(
            boolean returning
    ) {

        openLoanForm(
                returning,
                null
        );
    }

    private void openLoanForm(
            boolean returning,
            Integer bookId
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/client/fxml/loan-form.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            LoanFormController controller =
                    loader.getController();

            controller.setOnLoanCompleted(
                    this::loadLoans
            );

            if (returning) {

                controller.setReturnMode();

                if (bookId != null) {

                    controller.setBookId(bookId);
                }

            } else {

                controller.setBorrowMode();
            }

            Stage stage =
                    new Stage();

            stage.setTitle(
                    returning
                            ? "Return Book"
                            : "Borrow Book"
            );

            stage.setScene(
                    new Scene(
                            root,
                            650,
                            500
                    )
            );

            stage.setMinWidth(550);
            stage.setMinHeight(450);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Unable to open the loan form."
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

    public static class LoanRow {

        private final int id;
        private final int bookId;
        private final int memberId;
        private final String book;
        private final String member;
        private final String borrowed;
        private final String dueDate;
        private final String returned;
        private final String status;

        public LoanRow(
                int id,
                int bookId,
                int memberId,
                String book,
                String member,
                String borrowed,
                String dueDate,
                String returned,
                String status
        ) {

            this.id = id;
            this.bookId = bookId;
            this.memberId = memberId;
            this.book = book;
            this.member = member;
            this.borrowed = borrowed;
            this.dueDate = dueDate;
            this.returned = returned;
            this.status = status;
        }

        public static LoanRow from(
                LoanResponse loan
        ) {

            String returned =
                    loan.getReturnedDate() == null
                            ? "-"
                            : loan.getReturnedDate()
                            .toString();

            String status =
                    loan.getReturnedDate() == null
                            ? "ACTIVE"
                            : "RETURNED";

            return new LoanRow(
                    loan.getId(),
                    loan.getBookId(),
                    loan.getMemberId(),
                    "Book #" + loan.getBookId(),
                    "Member #" + loan.getMemberId(),
                    loan.getBorrowedDate()
                            .toString(),
                    loan.getDueDate()
                            .toString(),
                    returned,
                    status
            );
        }

        public int getId() {
            return id;
        }

        public int getBookId() {
            return bookId;
        }

        public int getMemberId() {
            return memberId;
        }

        public String getBook() {
            return book;
        }

        public String getMember() {
            return member;
        }

        public String getBorrowed() {
            return borrowed;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getReturned() {
            return returned;
        }

        public String getStatus() {
            return status;
        }
    }
}