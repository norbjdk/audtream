package com.audtream.desktop.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CreatePlaylistDialog extends Stage {
    private TextField nameField;
    private boolean confirmed = false;

    public CreatePlaylistDialog() {
        initStyle(StageStyle.UNDECORATED);
        initModality(Modality.APPLICATION_MODAL);

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #1a1a1a; -fx-background-radius: 10;");

        Label titleLabel = new Label("Create New Playlist");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label nameLabel = new Label("Playlist Name:");
        nameLabel.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 14px;");

        nameField = new TextField();
        nameField.setPromptText("Enter playlist name...");
        nameField.setPrefWidth(300);
        nameField.setStyle(
                "-fx-background-color: #2a2a2a; " +
                        "-fx-text-fill: white; " +
                        "-fx-prompt-text-fill: #666666; " +
                        "-fx-padding: 10; " +
                        "-fx-font-size: 14px; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-color: transparent; " +
                        "-fx-border-radius: 5;"
        );

        nameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                nameField.setStyle(
                        "-fx-background-color: #2a2a2a; " +
                                "-fx-text-fill: white; " +
                                "-fx-prompt-text-fill: #666666; " +
                                "-fx-padding: 10; " +
                                "-fx-font-size: 14px; " +
                                "-fx-background-radius: 5; " +
                                "-fx-border-color: #5c2091; " +
                                "-fx-border-width: 2; " +
                                "-fx-border-radius: 5;"
                );
            } else {
                nameField.setStyle(
                        "-fx-background-color: #2a2a2a; " +
                                "-fx-text-fill: white; " +
                                "-fx-prompt-text-fill: #666666; " +
                                "-fx-padding: 10; " +
                                "-fx-font-size: 14px; " +
                                "-fx-background-radius: 5; " +
                                "-fx-border-color: transparent; " +
                                "-fx-border-radius: 5;"
                );
            }
        });

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button createButton = new Button("Create");
        createButton.setPrefWidth(280);
        createButton.setStyle(
                "-fx-background-color: #5c2091; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 40 12 40; " +
                        "-fx-background-radius: 20; " +
                        "-fx-cursor: hand;"
        );

        createButton.setOnMouseEntered(e ->
                createButton.setStyle(
                        "-fx-background-color: #5c2091; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 12 40 12 40; " +
                                "-fx-background-radius: 20; " +
                                "-fx-cursor: hand;"
                )
        );

        createButton.setOnMouseExited(e ->
                createButton.setStyle(
                        "-fx-background-color: #5c2091; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-font-weight: bold; " +
                                "-fx-padding: 12 40 12 40; " +
                                "-fx-background-radius: 20; " +
                                "-fx-cursor: hand;"
                )
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(280);
        cancelButton.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: #b3b3b3; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 12 40 12 40; " +
                        "-fx-background-radius: 20; " +
                        "-fx-border-color: #535353; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 20; " +
                        "-fx-cursor: hand;"
        );

        cancelButton.setOnMouseEntered(e ->
                cancelButton.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-text-fill: white; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 12 40 12 40; " +
                                "-fx-background-radius: 20; " +
                                "-fx-border-color: white; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 20; " +
                                "-fx-cursor: hand;"
                )
        );

        cancelButton.setOnMouseExited(e ->
                cancelButton.setStyle(
                        "-fx-background-color: transparent; " +
                                "-fx-text-fill: #b3b3b3; " +
                                "-fx-font-size: 14px; " +
                                "-fx-padding: 12 40 12 40; " +
                                "-fx-background-radius: 20; " +
                                "-fx-border-color: #535353; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 20; " +
                                "-fx-cursor: hand;"
                )
        );

        createButton.setOnAction(e -> {
            if (nameField.getText() != null && !nameField.getText().trim().isEmpty()) {
                confirmed = true;
                close();
            }
        });

        cancelButton.setOnAction(e -> {
            confirmed = false;
            close();
        });

        nameField.setOnAction(e -> {
            if (nameField.getText() != null && !nameField.getText().trim().isEmpty()) {
                confirmed = true;
                close();
            }
        });

        buttonBox.getChildren().addAll(createButton, cancelButton);

        root.getChildren().addAll(titleLabel, nameLabel, nameField, buttonBox);

        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        setScene(scene);

        setWidth(400);
        setHeight(250);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getPlaylistName() {
        return nameField.getText();
    }
}