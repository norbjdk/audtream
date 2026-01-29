package com.audtream.desktop.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.Objects;

import static com.audtream.desktop.util.IconTool.setIcon;

public class SoundBox extends HBox {
    private final Button speakerBtn = new Button();
    private final Slider soundBar = new Slider();

    public SoundBox() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/player.css")).toExternalForm());
        getStyleClass().add("sound-box");

        setupComponents();
    }

    private void setupComponents() {
        setIcon(speakerBtn, FontAwesomeSolid.VOLUME_UP);
        speakerBtn.getStyleClass().add("sound-btn");
        soundBar.getStyleClass().add("sound-bar");
        getChildren().add(speakerBtn);
        getChildren().add(soundBar);
    }
}
