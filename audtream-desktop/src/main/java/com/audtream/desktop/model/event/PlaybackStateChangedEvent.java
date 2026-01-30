package com.audtream.desktop.model.event;

public class PlaybackStateChangedEvent {
    private final boolean isPlaying;

    public PlaybackStateChangedEvent(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
