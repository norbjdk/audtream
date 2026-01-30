package com.audtream.desktop.components;

import com.audtream.desktop.manager.AppStateManager;
import com.audtream.desktop.model.dto.UserStatsDTO;
import com.audtream.desktop.model.entity.User;
import com.audtream.desktop.util.DataBindingHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class ProfilePanel extends VBox {
    private User user;
    private final DataBindingHelper binder = new DataBindingHelper();
    private final AppStateManager app = AppStateManager.getInstance();

    private final Label usernameLabel = new Label();
    private final Label emailLabel = new Label();
    private final Label roleLabel = new Label();
    private final Label totalTracksLabel = new Label();
    private final Label totalPlaysLabel = new Label();
    private final Label totalLikesLabel = new Label();
    private final Label topGenreLabel = new Label();
    private final Label avgDurationLabel = new Label();
    private final Label mostPlayedLabel = new Label();

    public ProfilePanel() {
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/audtream/desktop/styles/components/profile.css")).toExternalForm());
        getStyleClass().add("profile-panel");

        setPadding(new Insets(20));
        setSpacing(10);

        setupLayout();
        loadData();
    }

    private void setupLayout() {
        getChildren().addAll(
                usernameLabel,
                emailLabel,
                roleLabel,
                new Label(""),
                totalTracksLabel,
                totalPlaysLabel,
                totalLikesLabel,
                topGenreLabel,
                avgDurationLabel,
                mostPlayedLabel
        );
    }

    public void setData(User user) {
        this.user = user;
        loadData();
    }

    private void loadData() {
        User currentUser = app.loadCurrentUser();
        if (currentUser != null) {
            Platform.runLater(() -> {
                usernameLabel.setText("Username: " + currentUser.getUsername());
                emailLabel.setText("Email: " + currentUser.getEmail());
                roleLabel.setText("Role: " + currentUser.getRole());
            });
        }

        binder.bindUserStats(
                stats -> {
                    Platform.runLater(() -> drawStats(stats));
                },
                error -> {
                }
        );
    }

    private void drawStats(UserStatsDTO stats) {
        totalTracksLabel.setText("Total Tracks: " + stats.getTotalTracks());
        totalPlaysLabel.setText("Total Plays: " + stats.getTotalPlays());
        totalLikesLabel.setText("Total Likes: " + stats.getTotalLikes());
        topGenreLabel.setText("Top Genre: " + stats.getTopGenre());

        int avgDuration = stats.getAverageDuration();
        int minutes = avgDuration / 60;
        int seconds = avgDuration % 60;
        avgDurationLabel.setText(String.format("Average Duration: %d:%02d", minutes, seconds));

        if (stats.getMostPlayedTrack() != null) {
            mostPlayedLabel.setText("Most Played: " + stats.getMostPlayedTrack() +
                    " (" + stats.getMostPlayedTrackPlays() + " plays)");
        }
    }
}