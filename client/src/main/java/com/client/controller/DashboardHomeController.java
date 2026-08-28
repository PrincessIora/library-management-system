package com.client.controller;

import com.client.model.BookResponse;
import com.client.model.LoanResponse;
import com.client.model.MemberResponse;
import com.client.service.BookService;
import com.client.service.LoanService;
import com.client.service.MemberService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class DashboardHomeController {

    @FXML
    private Label booksCountLabel;

    @FXML
    private Label membersCountLabel;

    @FXML
    private Label activeLoansCountLabel;

    @FXML
    private Label overdueCountLabel;

    @FXML
    private GridPane recentLoansGrid;

    @FXML
    private GridPane overdueLoansGrid;

    private final BookService bookService;
    private final MemberService memberService;
    private final LoanService loanService;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy");

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    public DashboardHomeController() {
        bookService = new BookService();
        memberService = new MemberService();
        loanService = new LoanService();
    }

    @FXML
    private void initialize() {

        loadDashboardStatistics();
    }

    private void loadDashboardStatistics() {

        Thread thread = new Thread(() -> {

            try {

                BookResponse[] books =
                        bookService.getBooks();

                MemberResponse[] members =
                        memberService.getMembers();

                LoanResponse[] activeLoans =
                        loanService.getActiveLoans();

                LoanResponse[] overdueLoans =
                        loanService.getOverdueLoans();

                LoanResponse[] allLoans =
                        loanService.getLoans();

                Map<Integer, String> bookNames =
                        createBookMap(books);

                Map<Integer, String> memberNames =
                        createMemberMap(members);

                Platform.runLater(() -> {

                    booksCountLabel.setText(
                            String.valueOf(books.length)
                    );

                    membersCountLabel.setText(
                            String.valueOf(members.length)
                    );

                    activeLoansCountLabel.setText(
                            String.valueOf(activeLoans.length)
                    );

                    overdueCountLabel.setText(
                            String.valueOf(overdueLoans.length)
                    );

                    displayRecentLoans(
                            allLoans,
                            bookNames,
                            memberNames
                    );

                    displayOverdueLoans(
                            overdueLoans,
                            bookNames,
                            memberNames
                    );
                });

            } catch (Exception e) {

                e.printStackTrace();

                Platform.runLater(() -> {

                    booksCountLabel.setText("—");
                    membersCountLabel.setText("—");
                    activeLoansCountLabel.setText("—");
                    overdueCountLabel.setText("—");

                    showMessage(
                            recentLoansGrid,
                            "Unable to load recent loans."
                    );

                    showMessage(
                            overdueLoansGrid,
                            "Unable to load overdue loans."
                    );
                });
            }

        });

        thread.setDaemon(true);
        thread.start();
    }

    private Map<Integer, String> createBookMap(
            BookResponse[] books
    ) {

        Map<Integer, String> bookNames =
                new HashMap<>();

        for (BookResponse book : books) {

            bookNames.put(
                    book.getId(),
                    book.getTitle()
            );
        }

        return bookNames;
    }

    private Map<Integer, String> createMemberMap(
            MemberResponse[] members
    ) {

        Map<Integer, String> memberNames =
                new HashMap<>();

        for (MemberResponse member : members) {

            memberNames.put(
                    member.getId(),
                    member.getFullName()
            );
        }

        return memberNames;
    }

    private void displayRecentLoans(
            LoanResponse[] loans,
            Map<Integer, String> bookNames,
            Map<Integer, String> memberNames
    ) {

        recentLoansGrid.getChildren().clear();

        addLoanHeader(
                recentLoansGrid,
                false
        );

        LoanResponse[] recentLoans =
                Arrays.stream(loans)
                        .sorted(
                                Comparator.comparing(
                                        LoanResponse::getBorrowedDate,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder()
                                        )
                                )
                        )
                        .limit(5)
                        .toArray(LoanResponse[]::new);

        if (recentLoans.length == 0) {

            showMessage(
                    recentLoansGrid,
                    "No loans have been recorded yet."
            );

            return;
        }

        int row = 1;

        for (LoanResponse loan : recentLoans) {

            String bookName =
                    bookNames.getOrDefault(
                            loan.getBookId(),
                            "Book #" + loan.getBookId()
                    );

            String memberName =
                    memberNames.getOrDefault(
                            loan.getMemberId(),
                            "Member #" + loan.getMemberId()
                    );

            String borrowedDate =
                    formatDateTime(
                            loan.getBorrowedDate()
                    );

            String dueDate =
                    formatDate(
                            loan.getDueDate()
                    );

            String status;

            if (loan.getReturnedDate() != null) {
                status = "Returned";
            } else if (
                    loan.getDueDate() != null &&
                            loan.getDueDate().isBefore(LocalDate.now())
            ) {
                status = "Overdue";
            } else {
                status = "Active";
            }

            addLoanRow(
                    recentLoansGrid,
                    row++,
                    bookName,
                    memberName,
                    borrowedDate,
                    dueDate,
                    status,
                    false
            );
        }
    }

    private void displayOverdueLoans(
            LoanResponse[] loans,
            Map<Integer, String> bookNames,
            Map<Integer, String> memberNames
    ) {

        overdueLoansGrid.getChildren().clear();

        addLoanHeader(
                overdueLoansGrid,
                true
        );

        LoanResponse[] overdueLoans =
                Arrays.stream(loans)
                        .sorted(
                                Comparator.comparing(
                                        LoanResponse::getDueDate,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder()
                                        )
                                )
                        )
                        .limit(5)
                        .toArray(LoanResponse[]::new);

        if (overdueLoans.length == 0) {

            showMessage(
                    overdueLoansGrid,
                    "✨ No overdue loans!"
            );

            return;
        }

        int row = 1;

        for (LoanResponse loan : overdueLoans) {

            String bookName =
                    bookNames.getOrDefault(
                            loan.getBookId(),
                            "Book #" + loan.getBookId()
                    );

            String memberName =
                    memberNames.getOrDefault(
                            loan.getMemberId(),
                            "Member #" + loan.getMemberId()
                    );

            String borrowedDate =
                    formatDateTime(
                            loan.getBorrowedDate()
                    );

            String dueDate =
                    formatDate(
                            loan.getDueDate()
                    );

            addLoanRow(
                    overdueLoansGrid,
                    row++,
                    bookName,
                    memberName,
                    borrowedDate,
                    dueDate,
                    "Overdue",
                    true
            );
        }
    }

    private void addLoanHeader(
            GridPane grid,
            boolean overdue
    ) {

        addHeaderLabel(
                grid,
                "Book",
                0
        );

        addHeaderLabel(
                grid,
                "Member",
                1
        );

        addHeaderLabel(
                grid,
                "Borrowed",
                2
        );

        addHeaderLabel(
                grid,
                "Due",
                3
        );

        addHeaderLabel(
                grid,
                "Status",
                4
        );
    }

    private void addHeaderLabel(
            GridPane grid,
            String text,
            int column
    ) {

        Label label = new Label(text);

        label.getStyleClass()
                .add("loan-table-header");

        GridPane.setMargin(
                label,
                new Insets(0, 5, 8, 5)
        );

        grid.add(
                label,
                column,
                0
        );
    }

    private void addLoanRow(
            GridPane grid,
            int row,
            String book,
            String member,
            String borrowed,
            String due,
            String status,
            boolean overdue
    ) {

        Label bookLabel =
                createLoanLabel(book);

        Label memberLabel =
                createLoanLabel(member);

        Label borrowedLabel =
                createLoanLabel(borrowed);

        Label dueLabel =
                createLoanLabel(due);

        Label statusLabel =
                createLoanLabel(status);

        statusLabel.getStyleClass()
                .add(
                        overdue
                                ? "loan-status-overdue"
                                : getStatusStyle(status)
                );

        grid.add(bookLabel, 0, row);
        grid.add(memberLabel, 1, row);
        grid.add(borrowedLabel, 2, row);
        grid.add(dueLabel, 3, row);
        grid.add(statusLabel, 4, row);
    }

    private Label createLoanLabel(String text) {

        Label label = new Label(text);

        label.getStyleClass()
                .add("loan-table-cell");

        label.setWrapText(true);

        return label;
    }

    private String getStatusStyle(String status) {

        return switch (status) {

            case "Returned" ->
                    "loan-status-returned";

            case "Overdue" ->
                    "loan-status-overdue";

            default ->
                    "loan-status-active";
        };
    }

    private void showMessage(
            GridPane grid,
            String message
    ) {

        grid.getChildren().clear();

        Label label =
                new Label(message);

        label.getStyleClass()
                .add("dashboard-empty-message");

        GridPane.setColumnSpan(
                label,
                5
        );

        grid.add(
                label,
                0,
                1
        );
    }

    private String formatDate(
            LocalDate date
    ) {

        if (date == null) {
            return "—";
        }

        return DATE_FORMAT.format(date);
    }

    private String formatDateTime(
            LocalDateTime dateTime
    ) {

        if (dateTime == null) {
            return "—";
        }

        return DATE_TIME_FORMAT.format(dateTime);
    }
}