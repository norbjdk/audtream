package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import com.audtream.desktop.service.CurrentUserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    private void loadMusicPlayer() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/audtream/desktop/fxml/components/music-player.fxml")
            );
            VBox musicPlayer = loader.load();
            //musicPlayerController = loader.getController();
        } catch (IOException e) {
            System.err.println("Failed to load music player: " + e.getMessage());
            e.printStackTrace();
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

    @FXML
    private void logout() {
        CurrentUserService.logout();

        // TODO: Back to login view
    }
}