module com.manage.lms {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
//    requires eu.hansolo.tilesfx;

    opens com.manage.lms to javafx.fxml;
    exports com.manage.lms;
    exports com.manage.lms.library.domain.repository;
    exports com.manage.lms.library.domain.model;
    exports com.manage.lms.library.application.service;
    exports com.manage.lms.library.domain.exception;
}