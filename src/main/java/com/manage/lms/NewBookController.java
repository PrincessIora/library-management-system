package com.manage.lms;

import com.manage.lms.library.application.service.BookService;
import com.manage.lms.library.domain.exception.ValidationException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewBookController {
    public Button newBookCancel;
    public Button newBookAdd;
    public Label newBookError;
    public TextField newBookStocks;
    public TextField newBookYear;
    public TextField newBookAuthor;
    public TextField newBookName;
    public Scene panelScene;
    public static Connection connection;
    public static TableView<Books> booksTable;
    BookService bookService;

    // loads scene for adding book
    void addBook(Connection conn,TableView<Books> adminBooksTable){
        booksTable = adminBooksTable;
        connection = conn;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newBook.fxml"));
            panelScene = Main.getMainStage().getScene();
            Main.mainStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void Book2DB() {

        try {

            bookService.createBook(
                    newBookName.getText(),
                    newBookAuthor.getText(),
                    Integer.parseInt(newBookYear.getText())
            );

            // navigate back to the book screen

        } catch (ValidationException e) {

            newBookError.setText(e.getMessage());

        }
    }
}
