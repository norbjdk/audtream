package com.audtream.desktop.model.event;

public class VolumeChangedEvent {
    private final double volume;

    public VolumeChangedEvent(double volume) {
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }
}
