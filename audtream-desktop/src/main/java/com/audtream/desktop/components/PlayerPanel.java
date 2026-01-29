package com.audtream.desktop.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.Objects;

import static com.audtream.desktop.util.IconTool.setIcon;

public class PlayerPanel extends VBox {
    private final Button shuffleBtn = new Button();
    private final Button previousBtn = new Button();
    private final Button playBtn = new Button();
    private final Button nextBtn = new Button();
    private final Button loopBtn = new Button();

    private final Label startTimeLabel = new Label("0:00");
    private final Label endTimeLabel = new Label("2:40");
    private final Slider progressBar = new Slider();

    public PlayerPanel() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/player.css")).toExternalForm());
        getStyleClass().add("player-panel");
        VBox.setVgrow(this, Priority.NEVER);

        setupComponents();
        styleComponents();
        layoutComponents();
    }

    private void setupComponents() {
        setIcon(shuffleBtn, FontAwesomeSolid.RANDOM, 20);
        setIcon(previousBtn, FontAwesomeSolid.STEP_BACKWARD, 20);
        setIcon(playBtn, FontAwesomeSolid.PLAY_CIRCLE, 28);
        setIcon(nextBtn, FontAwesomeSolid.STEP_FORWARD, 20);
        setIcon(loopBtn, FontAwesomeSolid.RETWEET, 20);
    }

    private void styleComponents() {
        shuffleBtn.getStyleClass().add("player-btn");
        previousBtn.getStyleClass().add("player-btn");
        playBtn.getStyleClass().add("player-btn");
        nextBtn.getStyleClass().add("player-btn");
        loopBtn.getStyleClass().add("player-btn");
    }

    private void layoutComponents() {
        HBox firstRow = new HBox();
        firstRow.setAlignment(Pos.CENTER);
        firstRow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(firstRow, Priority.ALWAYS);

        firstRow.getStyleClass().add("player-panel-first-row");
        firstRow.getChildren().addAll(shuffleBtn, previousBtn, playBtn, nextBtn, loopBtn);

        HBox secondRow = new HBox();
        secondRow.setAlignment(Pos.CENTER);
        secondRow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(secondRow, Priority.ALWAYS);

        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBar.setMaxWidth(Double.MAX_VALUE);

        secondRow.getStyleClass().add("player-panel-second-row");
        secondRow.getChildren().addAll(startTimeLabel, progressBar, endTimeLabel);

        getChildren().addAll(firstRow, secondRow);
    }
}
