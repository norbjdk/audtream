package com.audtream.desktop.model.event;

public class ShuffleChangedEvent {
    private final boolean isShuffle;

    public ShuffleChangedEvent(boolean isShuffle) {
        this.isShuffle = isShuffle;
    }

    public boolean isShuffle() {
        return isShuffle;
    }
}
