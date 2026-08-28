package com.client.controller;

import com.client.model.BookResponse;
import com.client.service.BookService;
import com.client.session.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class BookController {

    @FXML
    private TextField titleSearchField;

    @FXML
    private TextField authorSearchField;

    @FXML
    private TableView<BookRow> bookTable;

    @FXML
    private TableColumn<BookRow, Integer> idColumn;

    @FXML
    private TableColumn<BookRow, String> titleColumn;

    @FXML
    private TableColumn<BookRow, String> authorColumn;

    @FXML
    private TableColumn<BookRow, Integer> yearColumn;

    @FXML
    private TableColumn<BookRow, String> statusColumn;

    private final BookService bookService =
            new BookService();

    private final UserSession session =
            UserSession.getInstance();

    @FXML
    private void initialize() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title")
        );

        authorColumn.setCellValueFactory(
                new PropertyValueFactory<>("author")
        );

        yearColumn.setCellValueFactory(
                new PropertyValueFactory<>("year")
        );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        loadBooks();
    }

    private void loadBooks() {

        try {

            BookResponse[] books =
                    bookService.getBooks();

            displayBooks(books);

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Unable to load books.\n\n"
                            + getErrorMessage(e)
            );
        }
    }


    @FXML
    private void onTitleSearchButtonClick() {

        String title =
                titleSearchField.getText().trim();

        if (title.isBlank()) {

            showMessage(
                    "Search",
                    "Please enter a book title."
            );

            return;
        }

        try {

            BookResponse[] results =
                    bookService.searchByTitle(title);

            displayBooks(results);

            authorSearchField.clear();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Search Error",
                    getErrorMessage(e)
            );
        }
    }

    @FXML
    private void onAuthorSearchButtonClick() {

        String author =
                authorSearchField.getText().trim();

        if (author.isBlank()) {

            showMessage(
                    "Search",
                    "Please enter an author's name."
            );

            return;
        }

        try {

            BookResponse[] results =
                    bookService.searchByAuthor(author);

            displayBooks(results);


            titleSearchField.clear();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Search Error",
                    getErrorMessage(e)
            );
        }
    }


    private void displayBooks(
            BookResponse[] books
    ) {

        bookTable.setItems(
                FXCollections.observableArrayList(
                        java.util.Arrays.stream(books)
                                .map(BookRow::from)
                                .toList()
                )
        );
    }


    @FXML
    private void onAddBookButtonClick() {

        if (!session.isAdmin()) {

            showMessage(
                    "Access Denied",
                    "Only administrators can add books."
            );

            return;
        }

        openBookForm(
                false,
                null
        );
    }


    @FXML
    private void onEditBookButtonClick() {

        if (!session.isAdmin()) {

            showMessage(
                    "Access Denied",
                    "Only administrators can edit books."
            );

            return;
        }

        BookRow selectedBook =
                bookTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {

            showMessage(
                    "No Book Selected",
                    "Please select a book before editing."
            );

            return;
        }

        openBookForm(
                true,
                selectedBook
        );
    }

    @FXML
    private void onDeleteBookButtonClick() {

        if (!session.isAdmin()) {

            showMessage(
                    "Access Denied",
                    "Only administrators can delete books."
            );

            return;
        }

        BookRow selectedBook =
                bookTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (selectedBook == null) {

            showMessage(
                    "No Book Selected",
                    "Please select a book before deleting."
            );

            return;
        }

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle("Delete Book");

        alert.setHeaderText(
                "Delete \"" +
                        selectedBook.getTitle() +
                        "\"?"
        );

        alert.setContentText(
                "This action cannot be undone."
        );

        if (alert.showAndWait()
                .orElse(ButtonType.CANCEL)
                == ButtonType.OK) {

            try {

                bookService.deleteBook(
                        selectedBook.getId()
                );

                loadBooks();

                showMessage(
                        "Book Deleted",
                        "The book was deleted successfully."
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

        titleSearchField.clear();
        authorSearchField.clear();

        loadBooks();
    }


    private void openBookForm(
            boolean editing,
            BookRow book
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/com/client/fxml/book-form.fxml"
                            )
                    );

            Parent root =
                    loader.load();

            BookFormController controller =
                    loader.getController();

            if (editing && book != null) {

                controller.setBook(book);
            }

            Stage stage =
                    new Stage();

            stage.setTitle(
                    editing
                            ? "Edit Book"
                            : "Add Book"
            );

            stage.setScene(
                    new Scene(
                            root,
                            700,
                            650
                    )
            );

            stage.setMinWidth(600);
            stage.setMinHeight(550);

            stage.show();

        } catch (Exception e) {

            e.printStackTrace();

            showMessage(
                    "Error",
                    "Unable to open the book form."
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


    public static class BookRow {

        private final int id;
        private final String title;
        private final String author;
        private final int year;
        private final String status;

        public BookRow(
                int id,
                String title,
                String author,
                int year,
                String status
        ) {

            this.id = id;
            this.title = title;
            this.author = author;
            this.year = year;
            this.status = status;
        }

        public static BookRow from(
                BookResponse book
        ) {

            return new BookRow(
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getStatus().toString()
            );
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public int getYear() {
            return year;
        }

        public String getStatus() {
            return status;
        }
    }
}