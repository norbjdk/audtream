package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import com.audtream.desktop.util.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    Audtream audtream;

    @FXML private GridPane topBar;
    @FXML private ImageView logoView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Audtream.applyAppMovement(topBar);
        loadLogo();

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

    public void setAudtream(Audtream audtream) {
        this.audtream = audtream;
    }
}

