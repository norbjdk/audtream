package com.audtream.desktop.model.event;

public class TrackLikedEvent {
    private final Long trackId;

    public TrackLikedEvent(Long trackId) {
        this.trackId = trackId;
    }

    public Long getTrackId() {
        return trackId;
    }
}