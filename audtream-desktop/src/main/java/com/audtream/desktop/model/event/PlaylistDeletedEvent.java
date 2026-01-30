package com.audtream.desktop.model.event;

public class PlaylistDeletedEvent {
    private final Long playlistId;

    public PlaylistDeletedEvent(Long playlistId) {
        this.playlistId = playlistId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }
}