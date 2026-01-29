package com.audtream.desktop.components;

import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Objects;

public class PlayerBox extends GridPane {
    private final CurrentTrackCard trackCard = new CurrentTrackCard(0L, "Je te laisserai des mots", "Patrick Watson", "/com/audtream/desktop/assets/img/temp/sample.jpg");
    private final PlayerPanel playerPanel = new PlayerPanel();

    public PlayerBox() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/player.css")).toExternalForm());
        getStyleClass().add("player-box");

        Region lSpacer = new Region();
        Region rSpacer = new Region();
        GridPane.setHgrow(lSpacer, Priority.ALWAYS);
        GridPane.setHgrow(rSpacer, Priority.ALWAYS);

        add(trackCard, 0, 0);
        add(lSpacer, 1, 0);
        add(playerPanel, 2, 0);
        add(rSpacer, 3, 0);

        getChildren().forEach(child -> {
            GridPane.setValignment(child, VPos.CENTER);
        });
    }
}
