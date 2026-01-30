package com.audtream.desktop.model.event;

public class PlaylistLikedEvent {
    private final Long playlistId;

    public PlaylistLikedEvent(Long playlistId) {
        this.playlistId = playlistId;
    }

    public Long getPlaylistId() {
        return playlistId;
    }
}
