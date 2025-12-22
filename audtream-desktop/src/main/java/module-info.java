module audtream.desktop {
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.controlsfx.controls;

    opens com.audtream.desktop to javafx.fxml;
    exports com.audtream.desktop;
    opens com.audtream.desktop.controller to javafx.fxml;
    exports com.audtream.desktop.controller;
}