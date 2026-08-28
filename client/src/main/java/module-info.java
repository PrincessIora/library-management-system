module com.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;


    opens com.client to javafx.fxml;
    exports com.client;
    exports com.client.controller;
    opens com.client.controller to javafx.fxml;
    exports com.client.model;
    opens com.client.model to com.fasterxml.jackson.databind;
    exports com.client.service;
    opens com.client.service to javafx.fxml;
}