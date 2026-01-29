module audtream.desktop {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;
    requires com.fasterxml.jackson.annotation;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.prefs;
    requires com.google.gson;
    requires javafx.media;
    requires org.apache.commons.net;
    requires java.net.http;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires org.checkerframework.checker.qual;

    opens com.audtream.desktop to javafx.fxml;
    exports com.audtream.desktop;
    opens com.audtream.desktop.controller to javafx.fxml;
    exports com.audtream.desktop.controller;
    opens com.audtream.desktop.model.dto to com.fasterxml.jackson.databind;
}