package com.audtream.desktop.model.event;

public class PlaybackTimeChangedEvent {
    private final double currentTime;

    public PlaybackTimeChangedEvent(double currentTime) {
        this.currentTime = currentTime;
    }

    public double getCurrentTime() {
        return currentTime;
    }
}
