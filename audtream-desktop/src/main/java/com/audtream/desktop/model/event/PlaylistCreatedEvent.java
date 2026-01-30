package com.audtream.desktop.model.event;

public class PlaylistCreatedEvent {
    private final Long playlistId;

    public PlaylistCreatedEvent(Long playlistId) {
        this.playlistId = playlistId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }
}