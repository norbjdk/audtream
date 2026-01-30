package com.audtream.desktop.model.event;

public class PlaylistTracksChangedEvent {
    private final Long playlistId;

    public PlaylistTracksChangedEvent(Long playlistId) {
        this.playlistId = playlistId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }
}
