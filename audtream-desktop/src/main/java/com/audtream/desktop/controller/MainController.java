package com.audtream.desktop.controller;

import com.audtream.desktop.Audtream;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.*;
import java.net.HttpURLConnection;

public class MainController implements Initializable {
    @FXML private ImageView imgPrev;
    @FXML private GridPane topBarPane;
    @FXML private Button closeBtn;

    /**
     * Music Controller
     */
    @FXML private GridPane musicPlayerGrid;
    @FXML private Label musicTitleLabel;
    @FXML private Label musicAuthorLabel;
    @FXML private Button musicPlayButton;
    @FXML private ProgressBar musicProgressBar;

    private Audtream app;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private final Gson gson = new Gson();

    public void setMainApp(Audtream app) {
        this.app = app;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imgPrev.setPreserveRatio(false);
        imgPrev.setFitHeight(150);
        imgPrev.setFitWidth(150);
        imgPrev.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/assets/img/temp/me.jpg")).toExternalForm()));
        Circle clip = new Circle(75, 75, 75);
        imgPrev.setClip(clip);

//        App.loadAppMovement(topBarPane);
//        closeBtn.setOnAction(actionEvent -> App.close());
    }

    private String fetchTrackMetadata(int trackId) throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String url = "http://localhost:8080/api/v1/desktop/track?id=" + trackId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("API error: " + response.statusCode());
        }
    }
}
