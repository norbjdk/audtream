package com.audtream.desktop;

import com.audtream.desktop.config.AppConfig;
import com.audtream.desktop.controller.LoginController;
import com.audtream.desktop.controller.MainController;
import com.audtream.desktop.service.TokenStorage;
import com.audtream.desktop.util.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * Test user: tesguy, tesguy12345
 */

public final class Audtream extends Application {
    private static Stage primaryStage;
    public static Audtream instance;
    private static Image appIcon;
    private static int appXPos = 0;
    private static int appYPos = 0;
    @Override
    public void start(Stage stage) throws Exception {
        appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("assets/icons/logo.png")));
        instance = this;
        primaryStage = stage;
        primaryStage.getIcons().add(appIcon);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        if (TokenStorage.isLoggedIn()) showMainScene();
        else showLoginScene();
        AppConfig.configureSSL();
    }

    public static void main(String [] args) {
        launch();
    }

    private void showLoginScene() {
        int width = 900;
        int height = 600;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/views/login.fxml"));
            Parent root = fxmlLoader.load();
            LoginController controller = fxmlLoader.getController();
            controller.setOnLoginSuccess(() -> {
                try {
                    System.out.println("Logged in");
                    showMainScene();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            Scene loginScene = new Scene(root, width, height);
            loginScene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login | AudTream");
            primaryStage.setMinWidth(width);
            primaryStage.setMinHeight(height);
            primaryStage.show();
        } catch (IOException exception) {
            Logger.printErr("Error while loading login.fxml");
            throw new RuntimeException(exception);
        }
    }

    private void showMainScene() {
        int width = 1600;
        int height = 800;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/views/main.fxml"));
            Parent root = fxmlLoader.load();
            MainController controller = fxmlLoader.getController();

            Scene mainScene = new Scene(root, width, height);
            mainScene.setFill(Color.TRANSPARENT);
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Home | AudTream");
            primaryStage.setMinWidth(width);
            primaryStage.setMinHeight(height);
            primaryStage.show();
        } catch (IOException exception) {
            Logger.printErr("Error while loading home.fxml");
            throw new RuntimeException(exception);
        }
    }

    public static void applyAppMovement(Node target) {
        target.setOnMousePressed((MouseEvent event) -> {
            appXPos = (int) (event.getScreenX() - primaryStage.getX());
            appYPos = (int) (event.getScreenY() - primaryStage.getY());
        });

        target.setOnMouseDragged((MouseEvent event) -> {
            primaryStage.setX(event.getScreenX() - appXPos);
            primaryStage.setY(event.getScreenY() - appYPos);
        });
    }

    public static void minimize() {
        primaryStage.setIconified(true);
    }

    public static void close() {
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(300),
                new KeyValue(primaryStage.getScene().getRoot().opacityProperty(), 0)
        );
        timeline.getKeyFrames().add(keyFrame);
        timeline.setOnFinished(actionEvent -> System.exit(0));
        timeline.play();
    }
}
