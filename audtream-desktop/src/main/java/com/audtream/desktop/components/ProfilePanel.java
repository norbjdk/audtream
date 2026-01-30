package com.audtream.desktop.components;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.model.dto.UserStatsDTO;
import com.audtream.desktop.model.entity.User;
import com.audtream.desktop.util.DataBindingHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Objects;

public class ProfilePanel extends VBox {
    private User user;
    private final DataBindingHelper binder = new DataBindingHelper();
    private final AppStateManager app = AppStateManager.getInstance();

    // Header elements
    private final Circle avatar = new Circle(40);
    private final Label usernameLabel = new Label();
    private final Label emailLabel = new Label();
    private final Label roleLabel = new Label();

    // Stats elements
    private final Label totalTracksLabel = createStatLabel("total-tracks-label");
    private final Label totalPlaysLabel = createStatLabel("total-plays-label");
    private final Label totalLikesLabel = createStatLabel("total-likes-label");
    private final Label topGenreLabel = createStatLabel("top-genre-label");
    private final Label avgDurationLabel = createStatLabel("avg-duration-label");
    private final Label mostPlayedLabel = createStatLabel("most-played-label");

    public ProfilePanel() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/profile.css")).toExternalForm());
        getStyleClass().add("profile-panel");

        setPadding(new Insets(25));
        setSpacing(15);

        setupAvatar();
        setupLayout();
        loadData();
    }

    private void setupAvatar() {
        avatar.getStyleClass().add("profile-avatar");
    }

    private Label createStatLabel(String styleClass) {
        Label label = new Label();
        label.getStyleClass().addAll("stat-label", styleClass, "fade-in");
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
        return label;
    }

    private void setupLayout() {
        // Header with avatar
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(avatar, createUserInfoBox());

        // Stats section
        VBox statsBox = new VBox(10);
        statsBox.getStyleClass().add("stats-section");
        statsBox.getChildren().addAll(
                createStatRow("🎵 Tracks", totalTracksLabel),
                createStatRow("▶️ Plays", totalPlaysLabel),
                createStatRow("❤️ Likes", totalLikesLabel),
                createStatRow("🎭 Top Genre", topGenreLabel),
                createStatRow("⏱️ Avg Duration", avgDurationLabel),
                createStatRow("🔥 Most Played", mostPlayedLabel)
        );

        getChildren().addAll(headerBox, createSeparator(), statsBox);

        // Trigger animations
        Platform.runLater(() -> {
            totalTracksLabel.getStyleClass().add("animated");
            totalPlaysLabel.getStyleClass().add("animated");
            totalLikesLabel.getStyleClass().add("animated");
            topGenreLabel.getStyleClass().add("animated");
            avgDurationLabel.getStyleClass().add("animated");
            mostPlayedLabel.getStyleClass().add("animated");
        });
    }

    private VBox createUserInfoBox() {
        VBox userBox = new VBox(5);

        usernameLabel.getStyleClass().add("username-label");
        emailLabel.getStyleClass().add("email-label");
        roleLabel.getStyleClass().add("role-label");

        userBox.getChildren().addAll(usernameLabel, emailLabel, roleLabel);
        return userBox;
    }

    private HBox createStatRow(String title, Label valueLabel) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        row.getChildren().addAll(titleLabel, valueLabel);
        return row;
    }

    private HBox createSeparator() {
        HBox separator = new HBox();
        separator.getStyleClass().add("separator");
        separator.setPrefHeight(1);
        return separator;
    }

    public void setData(User user) {
        this.user = user;
        loadData();
    }

    private void loadData() {
        User currentUser = app.loadCurrentUser();
        if (currentUser != null) {
            Platform.runLater(() -> {
                usernameLabel.setText(currentUser.getUsername());
                emailLabel.setText(currentUser.getEmail());
                roleLabel.setText(currentUser.getRole());

                // Set avatar with first letter
                if (currentUser.getUsername() != null && !currentUser.getUsername().isEmpty()) {
                    avatar.setFill(createColorFromName(currentUser.getUsername()));
                }
            });
        }

        binder.bindUserStats(
                stats -> Platform.runLater(() -> drawStats(stats)),
                error -> System.err.println("Error loading user stats: " + error.getMessage())
        );
    }

    private Color createColorFromName(String name) {
        // Generate consistent color from username hash
        int hash = name.hashCode();
        float hue = (hash & 0xFFFFFF) % 360;
        return Color.hsb(hue, 0.7, 0.8);
    }

    private void drawStats(UserStatsDTO stats) {
        totalTracksLabel.setText(String.valueOf(stats.getTotalTracks()));
        totalPlaysLabel.setText(String.valueOf(stats.getTotalPlays()));
        totalLikesLabel.setText(String.valueOf(stats.getTotalLikes()));
        topGenreLabel.setText(stats.getTopGenre() != null ? stats.getTopGenre() : "N/A");

        int avgDuration = stats.getAverageDuration();
        int minutes = avgDuration / 60;
        int seconds = avgDuration % 60;
        avgDurationLabel.setText(String.format("%d:%02d", minutes, seconds));

        if (stats.getMostPlayedTrack() != null) {
            mostPlayedLabel.setText(stats.getMostPlayedTrack() +
                    " (" + stats.getMostPlayedTrackPlays() + " plays)");
        } else {
            mostPlayedLabel.setText("N/A");
        }
    }
}