package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import com.audtream.desktop.model.dto.AuthResponse;
import com.audtream.desktop.service.AuthService;
import com.audtream.desktop.util.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML private GridPane topBar;
    @FXML private ImageView logoView;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private AuthService authService;
    private Runnable onLoginSuccess;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        authService = new AuthService();

        Audtream.applyAppMovement(topBar);
        loadLogo();

        loginButton.setOnAction(e -> handleLogin());
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        new Thread(() -> {
            try {
                var response = authService.login(username, password);

                Platform.runLater(() -> {
                    if (onLoginSuccess != null) {
                        onLoginSuccess.run();
                    }
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    showError("Login failed: " + e.getMessage());
                });
            }
        }).start();
    }

    private void loadLogo() {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/assets/img/logo.png")).toExternalForm());
            if (!image.isError()) {
                logoView.setFitWidth(300);
                logoView.setFitHeight(120);
                logoView.setImage(image);
            }
        } catch (Exception e) {
            Logger.printErr("Couldn't load logo image. Error message: " + e.getMessage());
        }
    }

    @FXML
    private void close() {
        Audtream.close();
    }

    @FXML
    private void minimize() {
        Audtream.minimize();
    }

    private void showError(String message) {
        System.out.println(message);
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}

