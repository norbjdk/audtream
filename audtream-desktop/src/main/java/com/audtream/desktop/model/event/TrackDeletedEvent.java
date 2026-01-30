package com.audtream.desktop.model.event;

public class TrackDeletedEvent {
    private final Long trackId;

    public TrackDeletedEvent(Long trackId) {
        this.trackId = trackId;
    }

    public Long getTrackId() {
        return trackId;
    }
}