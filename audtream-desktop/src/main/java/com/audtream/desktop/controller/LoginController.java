package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    Audtream audtream;

    @FXML private GridPane topBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Audtream.applyAppMovement(topBar);
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

