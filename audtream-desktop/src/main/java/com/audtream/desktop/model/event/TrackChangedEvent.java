package com.audtream.desktop.model.event;

import com.audtream.desktop.model.dto.TrackDTO;

public class TrackChangedEvent {
    private final TrackDTO newTrack;
    private final TrackDTO previousTrack;

    public TrackChangedEvent(TrackDTO newTrack, TrackDTO previousTrack) {
        this.newTrack = newTrack;
        this.previousTrack = previousTrack;
    }

    public TrackDTO getNewTrack() {
        return newTrack;
    }

    public TrackDTO getPreviousTrack() {
        return previousTrack;
    }
}
