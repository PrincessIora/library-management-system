package com.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                LibraryApplication.class.getResource(
                        "fxml/login.fxml"
                )
        );

        Scene scene = new Scene(
                loader.load(),
                1100,
                700
        );

        scene.getStylesheets().add(
                LibraryApplication.class
                        .getResource("css/application.css")
                        .toExternalForm()
        );

        stage.setTitle("Library Management System");
        stage.setScene(scene);

        stage.setMinWidth(900);
        stage.setMinHeight(600);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}