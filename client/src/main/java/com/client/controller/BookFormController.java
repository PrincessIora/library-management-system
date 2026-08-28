package com.client.controller;

import com.client.model.BookResponse;
import com.client.service.BookService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BookFormController {

    @FXML
    private Label formTitle;

    @FXML
    private Label formSubtitle;

    @FXML
    private TextField titleField;

    @FXML
    private TextField authorField;

    @FXML
    private TextField yearField;

    @FXML
    private Label errorLabel;

    private final BookService bookService =
            new BookService();

    private boolean editing;

    private int bookId;

    @FXML
    private void initialize() {

        editing = false;

        formTitle.setText("Add Book");

        formSubtitle.setText(
                "Add a new book to your library."
        );
    }

    public void setBook(BookController.BookRow book) {

        editing = true;

        bookId = book.getId();

        formTitle.setText("Edit Book");

        formSubtitle.setText(
                "Update the information for this book."
        );

        titleField.setText(
                book.getTitle()
        );

        authorField.setText(
                book.getAuthor()
        );

        yearField.setText(
                String.valueOf(book.getYear())
        );
    }

    @FXML
    private void onSaveButtonClick() {

        String title =
                titleField.getText().trim();

        String author =
                authorField.getText().trim();

        String yearText =
                yearField.getText().trim();

        if (title.isBlank()) {

            showError(
                    "Please enter a book title."
            );

            return;
        }

        if (author.isBlank()) {

            showError(
                    "Please enter the author's name."
            );

            return;
        }

        if (yearText.isBlank()) {

            showError(
                    "Please enter the publication year."
            );

            return;
        }

        int year;

        try {

            year = Integer.parseInt(yearText);

        } catch (NumberFormatException e) {

            showError(
                    "Publication year must be a number."
            );

            return;
        }

        if (year <= 0) {

            showError(
                    "Publication year must be greater than zero."
            );

            return;
        }

        errorLabel.setText("");

        try {

            if (editing) {

                bookService.updateBook(
                        bookId,
                        title,
                        author,
                        year
                );

            } else {

                bookService.createBook(
                        title,
                        author,
                        year
                );
            }

            closeWindow();

        } catch (Exception e) {

            showError(
                    getErrorMessage(e)
            );

            e.printStackTrace();
        }
    }

    @FXML
    private void onCancelButtonClick() {

        closeWindow();
    }

    private void closeWindow() {

        yearField.getScene()
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

            return "Unable to save the book.";
        }

        return e.getMessage();
    }
}