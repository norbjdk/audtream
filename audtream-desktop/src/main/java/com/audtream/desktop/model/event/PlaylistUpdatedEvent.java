package com.audtream.desktop.model.event;

public class PlaylistUpdatedEvent {
    private final Long playlistId;

    public PlaylistUpdatedEvent(Long playlistId) {
        this.playlistId = playlistId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }
}