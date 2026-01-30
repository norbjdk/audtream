package com.audtream.desktop.model.event;

public class TrackPlayedEvent {
    private final Long trackId;

    public TrackPlayedEvent(Long trackId) {
        this.trackId = trackId;
    }

    public Long getTrackId() {
        return trackId;
    }
}